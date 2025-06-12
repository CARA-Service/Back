package com.syu.cara.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.repository.UserRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** 이미 DB에 실제로 접근해서 저장하기 위해 UserRepository를 추가 주입합니다 */
    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KakaoClient kakaoClient;

    private KakaoUserInfoDTO fakeKakaoInfo;

    @BeforeEach
    void setUp() {
        // 매 테스트 이전에 테이블을 깨끗하게 비웁니다.
        userRepository.deleteAll();

        // 1) 카카오 서버가 반환할 가짜 사용자 정보 준비
        fakeKakaoInfo = new KakaoUserInfoDTO();
        fakeKakaoInfo.setId("55555");

        KakaoUserInfoDTO.KakaoAccount account = new KakaoUserInfoDTO.KakaoAccount();
        account.setEmail("testuser@example.com");
        fakeKakaoInfo.setKakao_account(account);

        KakaoUserInfoDTO.Properties props = new KakaoUserInfoDTO.Properties();
        props.setNickname("테스트유저");
        props.setProfile_image("https://example.com/profile.jpg");
        fakeKakaoInfo.setProperties(props);

        // 2) KakaoClient.getKakaoUserInfo(...) 호출하면 fakeKakaoInfo를 반환하도록 모킹
        Mockito.when(kakaoClient.getKakaoUserInfo(anyString()))
               .thenReturn(fakeKakaoInfo);
    }

    @Test
    @DisplayName("카카오 로그인 → 신규 회원 가입 및 JWT 발급")
    void testKakaoLogin_NewUser_Signup() throws Exception {
        // (1) 요청 DTO 준비
        KakaoAuthRequest requestDto = new KakaoAuthRequest("dummy-access-token");

        // (2) 신규 가입 시나리오이므로, DB에 동일한 kakaoId가 없어야 합니다.(@BeforeEach에서 deleteAll() 했으므로 그냥 호출)
        mockMvc.perform(post("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.loginId").value("kakao_55555"))
            .andExpect(jsonPath("$.isNewUser").value(true))
            .andExpect(jsonPath("$.jwtToken").exists());
    }

    @Test
    @DisplayName("카카오 로그인 → 이미 가입된 회원일 경우 신규 가입 없이 로그인 처리")
    void testKakaoLogin_ExistingUser_Login() throws Exception {
        // (A) 사전에 DB에 똑같은 kakaoId를 가진 유저 한 명을 저장해 둡니다.
        User preExisting = User.builder()
                .kakaoId("55555")
                .loginId("kakao_55555")
                .email("testuser@example.com")
                .fullName("테스트유저")
                .profileImageUrl("https://example.com/profile.jpg")
                // (builder로 만든 순간 userId는 null 상태입니다)
                .build();

        // 저장 직후에 userId(PK)가 채워집니다.
        preExisting = userRepository.save(preExisting);

        KakaoAuthRequest requestDto = new KakaoAuthRequest("dummy-access-token");

        mockMvc.perform(post("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            // ★★ 여기! preExisting.getUserId() → getUserId()가 잘 생성되어야 합니다.
            .andExpect(jsonPath("$.userId").value(preExisting.getUserId()))
            .andExpect(jsonPath("$.loginId").value("kakao_55555"))
            .andExpect(jsonPath("$.isNewUser").value(false))
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
            .andExpect(status().isUnauthorized());
    }
}
