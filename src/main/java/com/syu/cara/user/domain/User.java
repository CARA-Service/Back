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
    private Long userId;

    // 내부 식별용 ID (예: "kakao_" + kakaoId)
    @Column(nullable = false, unique = true)
    private String loginId;

    // 일반 로그인용 비밀번호(카카오 로그인만 쓸 경우 null)
    @Column(nullable = false)
    private String passwordHash;

    // 카카오 계정 고유 ID (카카오 서버에서 받아옴)
    @Column(unique = true)
    private String kakaoId;

    // 카카오에서 가져온 프로필 정보
    private String fullName;
    private String email;
    private String profileImageUrl;

    // 카카오에서 가져온 프로필 정보 외 추가로 입력할 정보
    private String phoneNumber;
    private LocalDate birthDate;
    private String driverLicenseNumber;
    private String address;

    @Column(updatable = false)
    private LocalDateTime createdAt;
} 