package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.service.AiStylistService;
import com.pk.core.model.dto.request.StylistRequestDto;
import com.pk.core.model.dto.response.SetItemResponseDto;
import com.pk.core.model.dto.response.SetResponseDto;
import com.pk.core.model.dto.response.StylistRecommendationResponseDto;
import com.pk.core.model.entity.CategoryEntity;
import com.pk.core.model.entity.ProductEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** HEURISTIC đơn giản (KHÔNG phải AI/ML thật - xem javadoc interface): lọc sản phẩm PUBLISHED theo
 * occasion + ngân sách (dùng lại ProductRepo.searchPublished có sẵn, occasion lọc thêm bằng Java vì
 * repo hiện không có tham số occasion), matchScore là dãy số giảm dần cố định theo thứ tự chọn - chỉ
 * mang tính minh hoạ thứ tự ưu tiên, không phản ánh độ tương đồng ngữ nghĩa thật. */
@Service
@RequiredArgsConstructor
public class AiStylistServiceImpl implements AiStylistService {

    private static final BigDecimal NO_MAX_PRICE = new BigDecimal("999999999999");
    private static final double[] SCORES = {0.95, 0.9, 0.85, 0.8, 0.75};

    private final ProductRepo products;
    private final CategoryRepo categories;

    @Transactional(readOnly = true)
    @Override
    public List<StylistRecommendationResponseDto> recommend(StylistRequestDto req) {
        List<ProductEntity> candidates = candidates(req);
        List<StylistRecommendationResponseDto> result = new ArrayList<>();
        int i = 0;
        for (ProductEntity p : candidates) {
            if (i >= SCORES.length) {
                break;
            }
            result.add(new StylistRecommendationResponseDto("PRODUCT", p.getId(), p.getName(), p.getThumbnailUrl(),
                    reasonFor(req), SCORES[i]));
            i++;
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public SetResponseDto buildSet(StylistRequestDto req) {
        List<ProductEntity> candidates = candidates(req);

        // Mỗi sản phẩm trong set thuộc 1 danh mục KHÁC NHAU (đa dạng bộ set) - tối đa 3 món.
        Map<Long, ProductEntity> byCategory = new LinkedHashMap<>();
        for (ProductEntity p : candidates) {
            byCategory.putIfAbsent(p.getCategoryId(), p);
            if (byCategory.size() >= 3) {
                break;
            }
        }

        List<SetItemResponseDto> items = new ArrayList<>();
        int sortOrder = 0;
        for (ProductEntity p : byCategory.values()) {
            CategoryEntity category = categories.findOne(p.getCategoryId());
            items.add(new SetItemResponseDto(p.getId(), p.getName(), p.getThumbnailUrl(),
                    category != null ? category.getName() : "Khác", sortOrder == 0, sortOrder));
            sortOrder++;
        }

        String code = "SET" + System.currentTimeMillis();
        String name = "Set gợi ý" + (req.getOccasion() != null ? " cho dịp " + req.getOccasion() : "");
        String description = "Bộ trang sức gợi ý tự động theo danh mục/ngân sách - sinh tức thời, "
                + "không lưu lại (chưa có domain Set quản lý được ở admin)";
        return new SetResponseDto(code, name, description, BigDecimal.ZERO, items);
    }

    private List<ProductEntity> candidates(StylistRequestDto req) {
        BigDecimal maxPrice = req.getBudget() != null ? req.getBudget() : NO_MAX_PRICE;
        List<ProductEntity> all = products.searchPublished("", "", "", "", BigDecimal.ZERO, maxPrice);
        if (req.getOccasion() == null || req.getOccasion().isBlank()) {
            return all;
        }
        List<ProductEntity> byOccasion = all.stream()
                .filter(p -> req.getOccasion().equalsIgnoreCase(p.getOccasion()))
                .toList();
        // Không có sản phẩm nào khớp đúng occasion -> dùng lại danh sách theo ngân sách thay vì trả
        // rỗng (spec yêu cầu tối thiểu 3 gợi ý, dù không khớp tuyệt đối occasion vẫn nên có gợi ý).
        return byOccasion.isEmpty() ? all : byOccasion;
    }

    private static String reasonFor(StylistRequestDto req) {
        StringBuilder sb = new StringBuilder("Phù hợp dịp ").append(req.getOccasion());
        if (req.getBudget() != null) {
            sb.append(", trong ngân sách ").append(req.getBudget());
        }
        if (req.getRecipient() != null && !req.getRecipient().isBlank()) {
            sb.append(", dành tặng ").append(req.getRecipient());
        }
        return sb.toString();
    }
}
