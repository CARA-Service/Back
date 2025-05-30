// User Entity
package com.syu.cara.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String driverLicenseNumber;
    private String address;

    @Column(updatable = false)
    private LocalDate createdAt;
} 