// CarAvailabilityRepository
package com.syu.cara.car.repository;

import com.syu.cara.car.domain.CarAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarAvailabilityRepository extends JpaRepository<CarAvailability, Long> {
}