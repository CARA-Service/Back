package com.syu.cara.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String loginId;
    private String email;
    private String fullName;
    private String profileImageUrl;
    private String driverLicense;
    private String address;
}
