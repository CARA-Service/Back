package com.syu.cara.recommendation.dto;

import lombok.Data;

@Data
public class GPTRecommendationBundle {
    private RentalCondition condition;
    private String naturalLanguageMessage;
}
