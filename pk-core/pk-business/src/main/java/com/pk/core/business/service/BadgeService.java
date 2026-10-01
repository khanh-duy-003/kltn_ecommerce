package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminBadgeFlowRequestDto;
import com.pk.core.model.dto.request.AdminBadgeTemplateRequestDto;
import com.pk.core.model.dto.response.BadgeFlowResponseDto;
import com.pk.core.model.dto.response.BadgeResponseDto;
import com.pk.core.model.dto.response.BadgeTemplateResponseDto;

import java.util.List;

/**
 * Badge/nhãn dán sản phẩm - gồm 2 phần dùng CHUNG 1 interface (đúng khuôn mẫu Category/Collection/
 * Product/Voucher: service dùng chung cho storefront + admin, không tách bản riêng):
 * <p>(1) Storefront ({@code forSkus}, mục H spec, có SẴN TỪ TRƯỚC 2026-09-28) - badge suy ra TỰ ĐỘNG
 * từ tồn kho/ngày tạo SKU, KHÔNG đọc bảng {@code badge_templates} admin cấu hình - xem javadoc
 * {@code BadgeResponseDto} và {@code BadgeServiceImpl.forSkus()}.</p>
 * <p>(2) Admin (mục P spec, thêm 2026-09-29) - quản lý {@code badge_templates}/{@code badge_flow}.</p>
 * <p><b>SỰ CỐ ĐÃ SỬA (2026-09-30)</b>: lúc thêm phần (2), Claude tạo file MỚI bằng lệnh ghi đè toàn
 * bộ (không đọc file cũ trước) nên đã XOÁ MẤT method {@code forSkus()} sẵn có của phần (1) - khiến
 * {@code BadgeRest} (storefront, gọi {@code badgeService.forSkus(skuIds)}) không còn biên dịch được.
 * Người dùng phát hiện qua cảnh báo "no usages" của IntelliJ trên 1 file admin liên quan, Claude rà
 * lại TOÀN BỘ project (script kiểm tra mọi lời gọi `field.method()` so với method thực sự khai báo ở
 * interface tương ứng) và phát hiện đúng chỗ hỏng NÀY - DUY NHẤT trong cả dự án. Đã khôi phục lại
 * {@code forSkus()} (suy luận lại logic từ javadoc còn sót ở {@code BadgeResponseDto}/{@code
 * BadgeRest} vì môi trường không có git để phục hồi y hệt bản gốc - xem RULE-CODE.md mục "Badge - sự
 * cố ghi đè service (2026-09-30)" để biết chi tiết + quy tắc phòng tránh áp dụng từ nay: LUÔN đọc
 * file service/DTO hiện có trước khi tạo file cùng tên, không dùng lệnh ghi đè toàn bộ mù quáng).
 */
public interface BadgeService {

    // ---------- Storefront (mục H, KHÔNG đổi logic - chỉ khôi phục lại method bị mất) ----------

    /** GET /storefront/badge?skuIds=... - mỗi SKU tối đa 1 badge (ADR "mỗi sản phẩm một Label", xem
     * 00-GHI-NHO-DU-AN.md mục 1). SKU không khớp điều kiện nào thì KHÔNG có mặt trong kết quả (không
     * trả phần tử null/rỗng cho SKU đó). */
    List<BadgeResponseDto> forSkus(List<Long> skuIds);

    // ---------- Admin (mục P) ----------

    List<BadgeTemplateResponseDto> findAllTemplates();

    BadgeTemplateResponseDto findTemplateById(Long badgeId);

    BadgeTemplateResponseDto createTemplate(AdminBadgeTemplateRequestDto req);

    BadgeTemplateResponseDto updateTemplate(Long badgeId, AdminBadgeTemplateRequestDto req);

    /** Xoá MỀM (setDeletedDate) - BadgeTemplateEntity extends BaseEntity. */
    void deleteTemplate(Long badgeId);

    List<BadgeFlowResponseDto> findAllFlows();

    BadgeFlowResponseDto createFlow(AdminBadgeFlowRequestDto req);

    BadgeFlowResponseDto updateFlow(Long flowId, AdminBadgeFlowRequestDto req);
}
