package com.syu.cara.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_profile")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String preferredCarType;
    private BigDecimal minFuelEfficiency;
    private String profileImageUrl;
}