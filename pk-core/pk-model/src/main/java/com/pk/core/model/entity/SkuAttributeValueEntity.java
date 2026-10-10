package com.pk.core.model.entity;

import com.pk.core.common.entity.UpdateEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/** Giá trị thuộc tính gắn vào 1 SKU (mỗi cặp sku-attribute tối đa 1 dòng). MULTISELECT lưu các giá trị nối bằng "||". */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.SKU_ATTRIBUTE_VALUES)
public class SkuAttributeValueEntity extends UpdateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "sku_attribute_values_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "attribute_id")
    private Long attributeId;

    @Column(name = "value")
    private String value;

    public SkuAttributeValueEntity(Long skuId, Long attributeId, String value) {
        this.skuId = skuId;
        this.attributeId = attributeId;
        this.value = value;
    }
}
