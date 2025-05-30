package com.syu.cara.user.dto;

import java.time.LocalDate;

public class UserSignupRequest {
    private String loginId;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String driverLicenseNumber;
    private String address;
}
