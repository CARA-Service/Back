// RentalRequest Entity
package com.syu.cara.rentalrequest.domain;

import com.syu.cara.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_request")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RentalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String pickupLocation;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String purpose;
    private Integer passengerCount;
    private String luggageSize;
    private BigDecimal fuelEfficiencyPreference;
    private Integer budget;
    private String additionalOptions;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}