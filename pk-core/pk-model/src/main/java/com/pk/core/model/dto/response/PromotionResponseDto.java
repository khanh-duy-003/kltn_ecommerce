package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.PromotionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponseDto extends BaseDto {

    private Long id;
    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private Date startsAt;
    private Date endsAt;
    private String status;
    private List<Long> productIds;

    public static PromotionResponseDto from(PromotionEntity p, List<Long> productIds) {
        return BaseDto.of(new PromotionResponseDto(p.getId(), p.getName(), p.getDiscountType(),
                p.getDiscountValue(), p.getMaxDiscountAmount(), p.getStartsAt(), p.getEndsAt(),
                p.getStatus(), productIds), p);
    }
}
