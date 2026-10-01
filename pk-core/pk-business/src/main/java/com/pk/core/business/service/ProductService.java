package com.pk.core.business.service;

import com.pk.core.common.web.PageResponse;
import com.pk.core.model.dto.request.AdminCreateProductRequestDto;
import com.pk.core.model.dto.request.AdminUpdateProductRequestDto;
import com.pk.core.model.dto.response.ProductResponseDto;
import com.pk.core.model.dto.response.ProductSkuResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /** Chi tiết sản phẩm theo slug. Chỉ trả sản phẩm PUBLISHED (draft/archived -> 404) -
     * GET /storefront/product/{slug}. */
    ProductResponseDto findBySlug(String slug);

    /** Danh sách/tìm kiếm sản phẩm - GET /storefront/product. Tham số rỗng/null = không lọc
     * (categorySlug, collectionSlug, keyword, material truyền "" hoặc null đều được coi là bỏ qua;
     * minPrice/maxPrice truyền null sẽ tự lấy khoảng mặc định [0, vô cực]).
     * sort: NEWEST (mặc định) | PRICE_ASC | PRICE_DESC. RATING_DESC có trong spec FE nhưng DB
     * chưa có cột rating -> tạm coi như NEWEST (chưa làm đợt này).
     * page: 1-based (mặc định 1). take: mặc định 20, tối đa 100 (quy ước chung, xem PageResponse.paginate). */
    PageResponse<ProductResponseDto> search(String categorySlug, String collectionSlug, String keyword,
                                             String material, BigDecimal minPrice, BigDecimal maxPrice,
                                             String sort, int page, int take);

    // ===================== ADMIN (mục J spec) =====================

    /** Danh sách sản phẩm cho admin, KHÔNG giới hạn PUBLISHED - GET /admin/catalog/products.
     * Tham số rỗng/null = không lọc (status/categorySlug/collectionSlug/keyword), cùng quy ước
     * search() storefront. page 1-based, take mặc định 20 tối đa 100. */
    PageResponse<ProductResponseDto> searchForAdmin(String status, String categorySlug, String collectionSlug,
                                                     String keyword, int page, int take);

    /** Chi tiết để sửa (bao gồm mọi status, kể cả DRAFT/ARCHIVED) - GET /admin/catalog/products/{id}.
     * 404 nếu không có hoặc đã xoá mềm. */
    ProductResponseDto findByIdForAdmin(Long productId);

    /** Tạo sản phẩm + SKU - POST /admin/catalog/products. 409 DUPLICATE_CODE/DUPLICATE_SLUG/
     * DUPLICATE_SKU nếu trùng. */
    ProductResponseDto create(AdminCreateProductRequestDto req);

    /** Cập nhật sản phẩm - PUT /admin/catalog/products/{id}. KHÔNG đổi variants (xem
     * AdminUpdateProductRequestDto javadoc). */
    ProductResponseDto update(Long productId, AdminUpdateProductRequestDto req);

    /** Xoá mềm (set deletedId/deletedDate) - DELETE /admin/catalog/products/{id}. Sau khi xoá mềm,
     * sản phẩm không còn xuất hiện ở bất kỳ truy vấn storefront/admin nào (searchAdmin/searchPublished/
     * findBySlug đều lọc deleted_date IS NULL). */
    void softDelete(Long productId);

    /** Archive/unpublish - PATCH /admin/catalog/products/{id}/archive. Chuyển status -> ARCHIVED
     * (không xoá dữ liệu, chỉ ẩn khỏi storefront vì storefront chỉ hiển thị PUBLISHED). */
    ProductResponseDto archive(Long productId);

    /** SKU thuộc 1 sản phẩm - GET /admin/catalog/products/{id}/variants. 404 nếu sản phẩm không có. */
    List<ProductSkuResponseDto> findVariantsByProductId(Long productId);

    /** Tìm SKU theo mã/tên (không giới hạn theo sản phẩm) - GET /admin/catalog/variants. */
    List<ProductSkuResponseDto> searchVariants(String keyword);
}
