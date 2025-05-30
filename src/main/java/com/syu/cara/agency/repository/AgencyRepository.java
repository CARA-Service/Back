// RentalAgencyRepository
package com.syu.cara.agency.repository;

import com.syu.cara.agency.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
}