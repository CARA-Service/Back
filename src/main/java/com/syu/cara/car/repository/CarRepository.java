// CarRepository
package com.syu.cara.car.repository;

import com.syu.cara.car.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}