// RecommendationRepository
package com.syu.cara.recommendation.repository;

import com.syu.cara.recommendation.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}