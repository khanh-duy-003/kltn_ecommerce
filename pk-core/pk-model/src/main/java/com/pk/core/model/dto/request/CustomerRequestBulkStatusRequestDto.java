package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** PATCH /admin/customer-request/status/bulk: đổi trạng thái nhiều yêu cầu CÙNG loại (type) một lượt. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestBulkStatusRequestDto {

    @NotBlank
    @Pattern(regexp = "^(NEW|UNPROCESSED|CONTACTED|IN_PROGRESS|COMPLETED)$", message = "status không hợp lệ")
    private String status;

    @NotEmpty
    @Size(max = 200)
    private List<@Pattern(regexp = "^[0-9]{1,18}$", message = "id phải là số") String> ids;

    @NotBlank
    @Pattern(regexp = "^(BACK_IN_STOCK|ORDER_SUPPORT|NEWSLETTER)$", message = "type không hợp lệ")
    private String type;
}
