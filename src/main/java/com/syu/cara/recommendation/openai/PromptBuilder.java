package com.syu.cara.recommendation.openai;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {
    public String buildParsingPrompt(String userInput) {
        return String.format("""
    사용자의 차량 요청:

    "%s"

    위 내용을 바탕으로, 아래 항목들을 **가능한 한 추론하여** JSON 형식으로 구성해줘.
    - 값이 명확히 언급되지 않았다면, **일반적인 추론 또는 합리적인 기본값을 사용**해줘.
    - 연비는 무조건 float (예: 13.5), 인원수는 int로, 예산도 int로 작성.
    - 문장 기반 GPT 해석 결과도 "naturalLanguageMessage" 항목에 함께 제공해줘.

    {
      "pickupLocation": string,
      "rentalDate": string,
      "returnDate": string,
      "passengerCount": int,
      "luggageSize": string,
      "fuelEfficiencyPreference": float,
      "budget": int,
      "additionalOptions": string,
      "purpose": string,
      "naturalLanguageMessage": string
    }
    """, userInput);
    }

}
