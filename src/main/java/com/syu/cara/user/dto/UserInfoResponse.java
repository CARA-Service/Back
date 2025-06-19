package com.syu.cara.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserInfoResponse {
    private Long userId;
    private String loginId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String driverLicenseNumber;
    private LocalDate birthDate;
    private String address;
}

