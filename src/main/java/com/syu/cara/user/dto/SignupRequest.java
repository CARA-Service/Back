package com.syu.cara.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "loginId는 필수입니다.")
    private String loginId;

    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @NotBlank(message = "name은 필수입니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "email은 필수입니다.")
    private String email;

    @NotBlank(message = "phoneNumber는 필수입니다.")
    private String phoneNumber;

    @NotNull(message = "birthDate는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "address는 필수입니다.")
    private String address;
}
