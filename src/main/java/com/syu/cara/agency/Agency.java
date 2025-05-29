package com.syu.cara.agency;

import com.syu.cara.car.Car;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "agencies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private Double rating;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    private List<Car> cars;
}
