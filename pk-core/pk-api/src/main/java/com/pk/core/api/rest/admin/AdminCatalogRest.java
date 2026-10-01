package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CategoryService;
import com.pk.core.business.service.CollectionService;
import com.pk.core.business.service.ProductAttributeService;
import com.pk.core.business.service.ProductService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.AdminAttributeRequestDto;
import com.pk.core.model.dto.request.AdminCategoryRequestDto;
import com.pk.core.model.dto.request.AdminCollectionRequestDto;
import com.pk.core.model.dto.request.AdminCreateProductRequestDto;
import com.pk.core.model.dto.request.AdminUpdateProductRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin - Catalog/PIM (document/09-tong-hop-api-fe.md mục J). Gói api.rest.admin (pk-api), TÁCH
 * RIÊNG khỏi Rest storefront (ProductRest, gói api.rest) - xem claude/RULE-CODE.md. Mọi route ở đây
 * đã bị SecurityConfig (pk-identity) bắt buộc hasRole("ADMIN") (rule chung cho cả UrlAdminConstant.
 * Common.BASE + "/**", trừ /admin/auth/login).
 *
 * <p>KHÔNG có endpoint sửa/thêm variant (SKU) riêng sau khi sản phẩm đã tạo - spec chỉ liệt kê GET
 * cho variants (xem {@code AdminSkuRequestDto} javadoc), nên PUT sản phẩm chỉ sửa thông tin mô tả,
 * không đụng tới SKU.</p>
 */
@RestController
@RequiredArgsConstructor
public class AdminCatalogRest extends AbstractRest {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CollectionService collectionService;
    private final ProductAttributeService attributeService;

    // ---------- Products ----------

    @GetMapping(UrlAdminConstant.Catalog.PRODUCTS)
    public BaseRes listProducts(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String categorySlug,
                                 @RequestParam(required = false) String collectionSlug,
                                 @RequestParam(required = false, defaultValue = "1") int page,
                                 @RequestParam(required = false, defaultValue = "20") int take,
                                 HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(
                    productService.searchForAdmin(status, categorySlug, collectionSlug, search, page, take));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Catalog.PRODUCTS)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createProduct(@Valid @RequestBody AdminCreateProductRequestDto request,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlAdminConstant.Catalog.PRODUCTS + "/{productId}")
    public BaseRes productDetail(@PathVariable Long productId,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.findByIdForAdmin(productId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Catalog.PRODUCTS + "/{productId}")
    public BaseRes updateProduct(@PathVariable Long productId,
                                  @Valid @RequestBody AdminUpdateProductRequestDto request,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.update(productId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** 204 No Content: không có body nên không bọc AbstractRest/try-catch, giống các endpoint 204
     * khác của dự án (AuthRest.logout...) - lỗi (nếu có) rơi vào GlobalExceptionHandler. */
    @DeleteMapping(UrlAdminConstant.Catalog.PRODUCTS + "/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        productService.softDelete(productId);
    }

    @PatchMapping(UrlAdminConstant.Catalog.PRODUCTS + "/{productId}/archive")
    public BaseRes archiveProduct(@PathVariable Long productId,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.archive(productId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlAdminConstant.Catalog.PRODUCTS + "/{productId}/variants")
    public BaseRes productVariants(@PathVariable Long productId,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.findVariantsByProductId(productId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    // ---------- Variants (tìm chung, không theo 1 sản phẩm) ----------

    @GetMapping(UrlAdminConstant.Catalog.VARIANTS)
    public BaseRes searchVariants(@RequestParam(required = false) String search,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.searchVariants(search));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    // ---------- Categories ----------

    @GetMapping(UrlAdminConstant.Catalog.CATEGORIES)
    public BaseRes listCategories(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(categoryService.findAllForAdmin());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Catalog.CATEGORIES)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createCategory(@Valid @RequestBody AdminCategoryRequestDto request,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(categoryService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Catalog.CATEGORIES + "/{categoryId}")
    public BaseRes updateCategory(@PathVariable Long categoryId,
                                   @Valid @RequestBody AdminCategoryRequestDto request,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(categoryService.update(categoryId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    // ---------- Collections ----------

    @GetMapping(UrlAdminConstant.Catalog.COLLECTIONS)
    public BaseRes listCollections(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(collectionService.findAllForAdmin());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Catalog.COLLECTIONS)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createCollection(@Valid @RequestBody AdminCollectionRequestDto request,
                                     HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(collectionService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Catalog.COLLECTIONS + "/{collectionId}")
    public BaseRes updateCollection(@PathVariable Long collectionId,
                                     @Valid @RequestBody AdminCollectionRequestDto request,
                                     HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(collectionService.update(collectionId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    // ---------- Attributes ----------

    @GetMapping(UrlAdminConstant.Catalog.ATTRIBUTES)
    public BaseRes listAttributes(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(attributeService.findAll());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Catalog.ATTRIBUTES)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createAttribute(@Valid @RequestBody AdminAttributeRequestDto request,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(attributeService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Catalog.ATTRIBUTES + "/{attributeId}")
    public BaseRes updateAttribute(@PathVariable Long attributeId,
                                    @Valid @RequestBody AdminAttributeRequestDto request,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(attributeService.update(attributeId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
