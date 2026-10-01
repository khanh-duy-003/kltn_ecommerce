package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.BadgeFlowRepo;
import com.pk.core.business.repository.BadgeTemplateRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.BadgeService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminBadgeFlowRequestDto;
import com.pk.core.model.dto.request.AdminBadgeTemplateRequestDto;
import com.pk.core.model.dto.response.BadgeFlowResponseDto;
import com.pk.core.model.dto.response.BadgeResponseDto;
import com.pk.core.model.dto.response.BadgeTemplateResponseDto;
import com.pk.core.model.entity.BadgeFlowEntity;
import com.pk.core.model.entity.BadgeTemplateEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private static final Set<String> RULE_TYPES = Set.of(BadgeFlowEntity.RULE_MANUAL, BadgeFlowEntity.RULE_ALL,
            BadgeFlowEntity.RULE_CATEGORY, BadgeFlowEntity.RULE_COLLECTION, BadgeFlowEntity.RULE_PROMOTION,
            BadgeFlowEntity.RULE_OUT_OF_STOCK);
    private static final Set<String> CHANNELS = Set.of(BadgeFlowEntity.CHANNEL_ALL, BadgeFlowEntity.CHANNEL_WEB,
            BadgeFlowEntity.CHANNEL_MOBILE_WEB, BadgeFlowEntity.CHANNEL_APP);

    /** SKU tạo trong vòng bao nhiêu ngày thì được coi "mới" (NEW_ARRIVAL) - bảng product_skus không
     * có cột publishedAt riêng nên dùng createdDate làm proxy cho "ngày publish". Ngưỡng 14 ngày là
     * suy luận hợp lý (không có tài liệu gốc ghi lại con số chính xác - xem javadoc BadgeService về
     * sự cố ghi đè). */
    private static final long NEW_ARRIVAL_WINDOW_DAYS = 14;

    private final BadgeTemplateRepo templates;
    private final BadgeFlowRepo flows;
    private final ProductSkuRepo productSkus;

    // ---------- Storefront (mục H) ----------

    @Transactional(readOnly = true)
    @Override
    public List<BadgeResponseDto> forSkus(List<Long> skuIds) {
        List<BadgeResponseDto> result = new ArrayList<>();
        if (skuIds == null) {
            return result;
        }
        for (Long skuId : skuIds) {
            ProductSkuEntity sku = productSkus.findOne(skuId);
            if (sku == null) {
                continue;
            }
            String badgeType = inferBadgeType(sku);
            if (badgeType == null) {
                continue;
            }
            result.add(new BadgeResponseDto(sku.getId(), badgeType, displayTextFor(badgeType)));
        }
        return result;
    }

    /** Suy badge TỰ ĐỘNG từ tồn kho (ProductSkuEntity.stockStatus() có sẵn) và ngày tạo SKU. Ưu tiên
     * OUT_OF_STOCK > LOW_STOCK > NEW_ARRIVAL - CHỈ 1 badge/SKU (ADR "mỗi sản phẩm một Label"). SKU
     * không PUBLISHED thì không có badge (DRAFT/ARCHIVED không hiển thị ở storefront). Trả null nếu
     * không khớp điều kiện nào. */
    private String inferBadgeType(ProductSkuEntity sku) {
        if (!ProductSkuEntity.PUBLISHED.equals(sku.getStatus())) {
            return null;
        }
        String stockStatus = sku.stockStatus();
        if ("OUT_OF_STOCK".equals(stockStatus)) {
            return "OUT_OF_STOCK";
        }
        if ("LOW_STOCK".equals(stockStatus)) {
            return "LOW_STOCK";
        }
        if (isRecentlyCreated(sku)) {
            return "NEW_ARRIVAL";
        }
        return null;
    }

    private boolean isRecentlyCreated(ProductSkuEntity sku) {
        if (sku.getCreatedDate() == null) {
            return false;
        }
        long ageMillis = System.currentTimeMillis() - sku.getCreatedDate().getTime();
        return ageMillis <= NEW_ARRIVAL_WINDOW_DAYS * 24 * 60 * 60 * 1000L;
    }

    private static String displayTextFor(String badgeType) {
        return switch (badgeType) {
            case "OUT_OF_STOCK" -> "Hết hàng";
            case "LOW_STOCK" -> "Sắp hết hàng";
            case "NEW_ARRIVAL" -> "Mới";
            default -> badgeType;
        };
    }

    // ---------- Admin (mục P) ----------

    @Transactional(readOnly = true)
    @Override
    public List<BadgeTemplateResponseDto> findAllTemplates() {
        return templates.findAll(Sort.unsorted()).stream()
                .filter(t -> t.getDeletedDate() == null)
                .map(BadgeTemplateResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BadgeTemplateResponseDto findTemplateById(Long badgeId) {
        return BadgeTemplateResponseDto.from(findTemplateOrThrow(badgeId));
    }

    @Transactional
    @Override
    public BadgeTemplateResponseDto createTemplate(AdminBadgeTemplateRequestDto req) {
        BadgeTemplateEntity template = new BadgeTemplateEntity(req.getName().trim(), req.getLabelText().trim(),
                req.getColor());
        templates.create(template);
        return BadgeTemplateResponseDto.from(template);
    }

    @Transactional
    @Override
    public BadgeTemplateResponseDto updateTemplate(Long badgeId, AdminBadgeTemplateRequestDto req) {
        BadgeTemplateEntity template = findTemplateOrThrow(badgeId);
        template.setName(req.getName().trim());
        template.setLabelText(req.getLabelText().trim());
        template.setColor(req.getColor());
        template.touch();
        templates.update(template);
        return BadgeTemplateResponseDto.from(template);
    }

    @Transactional
    @Override
    public void deleteTemplate(Long badgeId) {
        BadgeTemplateEntity template = findTemplateOrThrow(badgeId);
        template.setDeletedDate(new Date());
        template.touch();
        templates.update(template);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BadgeFlowResponseDto> findAllFlows() {
        return flows.findAll(Sort.unsorted()).stream().map(BadgeFlowResponseDto::from).toList();
    }

    @Transactional
    @Override
    public BadgeFlowResponseDto createFlow(AdminBadgeFlowRequestDto req) {
        findTemplateOrThrow(req.getBadgeId());
        validateRuleAndChannel(req.getRuleType(), req.getChannel());
        BadgeFlowEntity flow = new BadgeFlowEntity(req.getBadgeId(), req.getRuleType(), req.getRuleRefId(),
                req.getChannel(), req.getPriority(), req.isActive());
        flows.create(flow);
        return BadgeFlowResponseDto.from(flow);
    }

    @Transactional
    @Override
    public BadgeFlowResponseDto updateFlow(Long flowId, AdminBadgeFlowRequestDto req) {
        BadgeFlowEntity flow = flows.findOne(flowId);
        if (flow == null) {
            throw new ResourceNotFoundException("Luồng hiển thị nhãn", "BadgeFlow", flowId);
        }
        findTemplateOrThrow(req.getBadgeId());
        validateRuleAndChannel(req.getRuleType(), req.getChannel());

        flow.setBadgeId(req.getBadgeId());
        flow.setRuleType(req.getRuleType());
        flow.setRuleRefId(req.getRuleRefId());
        flow.setChannel(req.getChannel() != null && !req.getChannel().isBlank()
                ? req.getChannel() : BadgeFlowEntity.CHANNEL_ALL);
        flow.setPriority(req.getPriority());
        flow.setActive(req.isActive());
        flow.touch();
        flows.update(flow);
        return BadgeFlowResponseDto.from(flow);
    }

    private BadgeTemplateEntity findTemplateOrThrow(Long badgeId) {
        BadgeTemplateEntity template = templates.findOne(badgeId);
        if (template == null || template.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Mẫu nhãn", "BadgeTemplate", badgeId);
        }
        return template;
    }

    private void validateRuleAndChannel(String ruleType, String channel) {
        if (!RULE_TYPES.contains(ruleType)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "ruleType không hợp lệ: " + ruleType, ruleType);
        }
        if (channel != null && !channel.isBlank() && !CHANNELS.contains(channel)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "channel không hợp lệ: " + channel, channel);
        }
    }
}
