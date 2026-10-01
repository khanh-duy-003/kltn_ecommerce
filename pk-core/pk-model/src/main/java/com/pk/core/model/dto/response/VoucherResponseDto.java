package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.VoucherEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponseDto extends BaseDto {

    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private int usedCount;
    private Date startsAt;
    private Date endsAt;
    private String status;

    public static VoucherResponseDto from(VoucherEntity v) {
        return BaseDto.of(new VoucherResponseDto(v.getId(), v.getCode(), v.getDiscountType(),
                v.getDiscountValue(), v.getMaxDiscountAmount(), v.getMinOrderValue(), v.getUsageLimit(),
                v.getUsedCount(), v.getStartsAt(), v.getEndsAt(), v.getStatus()), v);
    }
}
