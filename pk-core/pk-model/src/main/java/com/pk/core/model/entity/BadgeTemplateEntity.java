package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/** Mẫu nhãn dán sản phẩm (Admin mục P, VD "Mới", "Bán chạy", "Giảm giá"). Bảng `badge_templates`
 * (V2__admin_extensions.sql) CÓ deleted_id/deleted_date nên extends BaseEntity - xoá mẫu là xoá MỀM,
 * cùng kiểu Product/Promotion/CmsPage. Luồng hiển thị (khi nào áp mẫu nào) là bảng con riêng
 * {@link BadgeFlowEntity}, 1-n qua badge_id. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.BADGE_TEMPLATES)
public class BadgeTemplateEntity extends BaseEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "badge_templates_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "label_text")
    private String labelText;

    @Column(name = "color")
    private String color;

    public BadgeTemplateEntity(String name, String labelText, String color) {
        this.name = name;
        this.labelText = labelText;
        this.color = color;
    }
}
