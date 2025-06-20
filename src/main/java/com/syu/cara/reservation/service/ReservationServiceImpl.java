package com.syu.cara.reservation.service;

import com.syu.cara.car.domain.Car;
import com.syu.cara.policy.domain.InsuranceOption;
import com.syu.cara.recommendation.domain.Recommendation;
import com.syu.cara.reservation.domain.Reservation;
import com.syu.cara.reservation.dto.ReservationCreateRequest;
import com.syu.cara.reservation.repository.ReservationRepository;
import com.syu.cara.recommendation.repository.RecommendationRepository;
import com.syu.cara.policy.repository.InsuranceOptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RecommendationRepository recommendationRepository;
    private final InsuranceOptionRepository insuranceOptionRepository;

    @Override
    @Transactional
    public Long createReservation(ReservationCreateRequest request) {
        // 추천 정보 조회
        Recommendation recommendation = recommendationRepository.findById(request.getRecommendationId())
                .orElseThrow(() -> new EntityNotFoundException("추천 정보를 찾을 수 없습니다: " + request.getRecommendationId()));

        // 보험 옵션 조회
        InsuranceOption insuranceOption = insuranceOptionRepository.findById(request.getInsuranceOptionId())
                .orElseThrow(() -> new EntityNotFoundException("보험 옵션을 찾을 수 없습니다: " + request.getInsuranceOptionId()));

        // 차량 정보 가져오기
        Car car = recommendation.getCar();
        if (car == null) {
            throw new EntityNotFoundException("추천에 연결된 차량 정보가 없습니다.");
        }

        // 예약 기간 계산 (예: "3일")
        long days = ChronoUnit.DAYS.between(request.getRentalDate(), request.getReturnDate()) + 1;
        String reservedPeriod = days + "일";

        // 예약 엔티티 생성 - status는 "결제완료"로 고정
        Reservation reservation = Reservation.builder()
                .recommendation(recommendation)
                .insuranceOption(insuranceOption)
                .rentalDate(request.getRentalDate())
                .returnDate(request.getReturnDate())
                .totalPrice(request.getTotalPrice())
                .status("결제완료")  // 상태를 "결제완료"로 고정
                .payment(request.getPayment())
                .createdAt(LocalDateTime.now())
                .build();

        // 저장
        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getReservationId();
    }
}