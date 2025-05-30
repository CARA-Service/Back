// InsuranceOptionRepository
package com.syu.cara.policy.repository;

import com.syu.cara.policy.domain.InsuranceOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceOptionRepository extends JpaRepository<InsuranceOption, Long> {
}