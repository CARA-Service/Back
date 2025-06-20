// RentalAgencyRepository
package com.syu.cara.agency.repository;

import com.syu.cara.agency.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    List<Agency> findByLocationContaining(String location);
    List<Agency> findByAgencyNameContaining(String agencyName);

    // 지점명 또는 위치에서 검색 (OR 조건)
    @Query("SELECT a FROM Agency a WHERE a.agencyName LIKE %:keyword% OR a.location LIKE %:keyword%")
    List<Agency> findByKeyword(@Param("keyword") String keyword);
}