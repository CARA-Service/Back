// SurchargePolicyRepository
package com.syu.cara.policy.repository;

import com.syu.cara.policy.domain.SurchargePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurchargePolicyRepository extends JpaRepository<SurchargePolicy, Long> {
}