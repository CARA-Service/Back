package com.syu.cara.user;

import com.syu.cara.rentalrequest.RentalRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fullName;

    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private String driverLicenseNumber;
    private String address;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RentalRequest> rentalRequests;
}
