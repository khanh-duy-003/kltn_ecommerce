package com.pk.core.model.dto.response;

import com.pk.core.model.entity.OrderTimelineEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** DTO con nằm trong OrderResponseDto.timeline - KHÔNG kế thừa BaseDto/CreateDto (occurredAt đã là
 * mốc thời gian có ý nghĩa, không cần thêm createdDate trùng lặp). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineResponseDto {

    private String status;
    private Date occurredAt;
    private String note;
    private String actor;

    public static OrderTimelineResponseDto from(OrderTimelineEntity t) {
        return new OrderTimelineResponseDto(t.getStatus(), t.getOccurredAt(), t.getNote(), t.getActor());
    }
}
