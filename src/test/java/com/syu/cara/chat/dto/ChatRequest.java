package com.syu.cara.chat.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private Long userId;
    private String message; // 사용자의 자연어 입력
}
