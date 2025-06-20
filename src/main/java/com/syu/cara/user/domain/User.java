// User Entity
package com.syu.cara.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String loginId;

    private String passwordHash;
    
    @Column(nullable = false)
    private String fullName;
    
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String driverLicense;
    private String driverLicenseNumber;
    private String address;
    
    @Column(unique = true)
    private String kakaoId;
    
    private String profileImageUrl;
    private LocalDateTime createdAt;
} 