// PromptHistoryRepository
package com.syu.cara.rentalrequest.repository;

import com.syu.cara.rentalrequest.domain.PromptHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptHistoryRepository extends JpaRepository<PromptHistory, Long> {
}