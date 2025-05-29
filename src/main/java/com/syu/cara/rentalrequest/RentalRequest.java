package com.syu.cara.rentalrequest;

import com.syu.cara.recommendation.Recommendation;
import com.syu.cara.user.User;
import jakarta.persistence.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rental_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RentalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime pickupDate;
    private LocalDateTime dropoffDate;
    private Integer passengerCount;
    private Integer luggageCount;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "rentalRequest", cascade = CascadeType.ALL)
    private List<Recommendation> recommendations;
    
    public enum RequestStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
