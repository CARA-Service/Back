// DiscountPolicyRepository
package com.syu.cara.policy.repository;

import com.syu.cara.policy.domain.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {
}