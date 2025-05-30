// RentalRequestRepository
package com.syu.cara.rentalrequest.repository;

import com.syu.cara.rentalrequest.domain.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {
}