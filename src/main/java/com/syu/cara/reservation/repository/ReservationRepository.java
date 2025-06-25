// ReservationRepository
package com.syu.cara.reservation.repository;

import com.syu.cara.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 특정 기간에 이미 예약된 차량 ID 목록을 조회합니다.
     * 두 기간이 하루라도 겹치면 예약된 것으로 간주합니다.
     * status가 "결제완료"인 예약만 고려합니다.
     *
     * @param startDate 대여 시작일
     * @param endDate 대여 종료일
     * @return 예약된 차량 ID 집합
     */
    @Query("SELECT r.recommendation.car.carId FROM Reservation r " +
            "WHERE (r.rentalDate <= :endDate AND r.returnDate >= :startDate) " +
            "AND r.status = '결제완료'")
    Set<Long> findReservedCarIds(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}