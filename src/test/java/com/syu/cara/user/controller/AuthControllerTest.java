package com.syu.cara.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.service.KakaoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController에 대한 통합 테스트 (MockMvc 사용).
 * - 실제 스프링 컨텍스트를 띄우고, KakaoClient는 @MockBean으로 대체하여 카카오 API 호출을 모킹.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;           // MockMvc를 통해 HTTP 요청/응답 시뮬레이션

    @Autowired
    private ObjectMapper objectMapper;  // JSON 직렬화/역직렬화용

    @MockBean
    private KakaoClient kakaoClient;    // 카카오 API 호출을 MockBean으로 대체

    private KakaoUserInfoDTO fakeKakaoInfo;

    @BeforeEach
    void setUp() {
        // 1) 카카오 서버가 반환할 가짜 사용자 정보(KakaoUserInfoDTO) 구성
        fakeKakaoInfo = new KakaoUserInfoDTO();
        fakeKakaoInfo.setId("55555");

        // kakao_account 필드 설정 (예: 이메일이 동의된 경우)
        KakaoUserInfoDTO.KakaoAccount account = new KakaoUserInfoDTO.KakaoAccount();
        account.setEmail("testuser@example.com");
        fakeKakaoInfo.setKakao_account(account);

        // properties 필드 설정 (닉네임, 프로필 이미지)
        KakaoUserInfoDTO.Properties props = new KakaoUserInfoDTO.Properties();
        props.setNickname("테스트유저");
        props.setProfile_image("https://example.com/profile.jpg");
        fakeKakaoInfo.setProperties(props);

        // 2) KakaoClient.getKakaoUserInfo(...) 호출 시 fakeKakaoInfo를 반환하도록 설정
        Mockito.when(kakaoClient.getKakaoUserInfo(anyString()))
               .thenReturn(fakeKakaoInfo);
    }

    @Test
    @DisplayName("카카오 로그인 → 신규 회원 가입 및 JWT 발급")
    void testKakaoLogin_NewUser_Signup() throws Exception {
        // (1) 요청 DTO 준비
        KakaoAuthRequest requestDto = new KakaoAuthRequest("dummy-access-token");

        // (2) HTTP POST 요청 수행
        mockMvc.perform(post("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // (3) 예상 HTTP 상태 코드 200 (OK)
            .andExpect(status().isOk())
            // (4) 응답 JSON에 userId 필드가 존재
            .andExpect(jsonPath("$.userId").exists())
            // (5) loginId가 "kakao_{카카오ID}" 형태인지 체크
            .andExpect(jsonPath("$.loginId").value("kakao_55555"))
            // (6) isNewUser가 true (신규 회원 가입이므로 true)
            .andExpect(jsonPath("$.isNewUser").value(true))
            // (7) jwtToken 필드가 존재 (Fake JWT라도 필드 자체만 확인)
            .andExpect(jsonPath("$.jwtToken").exists());
    }

    @Test
    @DisplayName("카카오 로그인 → 이미 가입된 회원일 경우 신규 가입 없이 로그인 처리")
    void testKakaoLogin_ExistingUser_Login() throws Exception {
        // 신규 가입 로직을 우회하기 위해,
        // UserService 내부적으로 userRepository.findByKakaoId(...)를 모킹해야 하지만
        // AuthController 통합 테스트에서는 카카오클라이언트 반환 정보만 제공하면,
        // 실제 DB에 이미 같은 kakaoId가 있을 경우의 시나리오는
        // UserService 단위 테스트에서 검증했으므로, 여기서는 주로 “200 OK + 반환 값 형식”만 확인합니다.

        KakaoAuthRequest requestDto = new KakaoAuthRequest("dummy-access-token");

        mockMvc.perform(post("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.loginId").value("kakao_55555"))
            .andExpect(jsonPath("$.isNewUser").exists())
            .andExpect(jsonPath("$.jwtToken").exists());
    }

    @Test
    @DisplayName("카카오 로그인 → 잘못된 토큰으로 인한 인증 실패 시 401 반환")
    void testKakaoLogin_InvalidToken_Unauthorized() throws Exception {
        // 잘못된 토큰 예시로 KakaoClient가 예외를 던지도록 모킹
        Mockito.when(kakaoClient.getKakaoUserInfo(anyString()))
               .thenThrow(new RuntimeException("Invalid access token"));

        KakaoAuthRequest requestDto = new KakaoAuthRequest("invalid-token");

        mockMvc.perform(post("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // 인증 실패하는 경우, UserServiceImpl이나 GlobalExceptionHandler에서 던진 예외에 따라 401 또는 400을 기대할 수 있음.
            // 여기서는 401 Unauthorized로 가정
            .andExpect(status().isUnauthorized());
    }
}
