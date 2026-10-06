package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/customer-request/newsletter: đăng ký nhận tin qua email. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterRequestDto {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;
}
