package com.pk.core.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CategoryService;
import com.pk.core.business.service.CollectionService;
import com.pk.core.business.service.ProductService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Storefront - đọc catalog, không cần đăng nhập (xem document/09-tong-hop-api-fe.md mục C).
 * Admin CRUD (tạo/sửa/xoá category, product, sku...) CHƯA làm lại đợt này - phần đã xoá trước đó
 * (AdminCatalogRest, CategoryRest cũ) không nằm trong lần dựng lại này (yêu cầu "ngoài admin").
 */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class ProductRest extends AbstractRest {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CollectionService collectionService;

    @GetMapping(UrlConstant.Product.BASE)
    public BaseRes search(@RequestParam(required = false) String category,
                           @RequestParam(required = false) String collection,
                           @RequestParam(required = false) String search,
                           @RequestParam(required = false) String material,
                           @RequestParam(required = false) BigDecimal minPrice,
                           @RequestParam(required = false) BigDecimal maxPrice,
                           @RequestParam(required = false, defaultValue = "NEWEST") String sort,
                           @RequestParam(required = false, defaultValue = "1") int page,
                           @RequestParam(required = false, defaultValue = "20") int take,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(
                    productService.search(category, collection, search, material, minPrice, maxPrice, sort, page, take));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlConstant.Product.CATEGORIES)
    public BaseRes categories(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(categoryService.findAllActive());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlConstant.Product.COLLECTIONS)
    public BaseRes collections(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(collectionService.findAllPublished());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** Đặt sau CATEGORIES/COLLECTIONS: Spring ưu tiên khớp path tĩnh hơn {slug} nên không xung đột. */
    @GetMapping(UrlConstant.Product.BASE + "/{slug}")
    public BaseRes detail(@PathVariable String slug,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(productService.findBySlug(slug));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
