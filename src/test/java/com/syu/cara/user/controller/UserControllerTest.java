package com.syu.cara.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.user.domain.User;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;
    @MockBean
    JwtService jwtService;
    @MockBean
    CustomUserDetailsService userDetailsService;
    @MockBean
    UserRepository userRepo;
    @MockBean
    private com.syu.cara.user.service.UserService userService;


    @Test
    void getMyProfile_success() throws Exception {
        // 1) JWT 검증, UserDetails 세팅
        String token = "dummy.jwt.token";
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.getUserIdFromToken(token)).thenReturn(42L);

        User user = User.builder()
                        .loginId("kakao_42")
                        .email("u@ex.com")
                        .fullName("테스터")
                        .profileImageUrl("url")
                        .driverLicenseNumber("DL")
                        .address("addr")
                        .build();
        when(userRepo.findById(42L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loginId").value("kakao_42"))
            .andExpect(jsonPath("$.email").value("u@ex.com"))
            .andExpect(jsonPath("$.fullName").value("테스터"));
    }

    @Test
    void getMyProfile_unauthorized() throws Exception {
        when(jwtService.validateToken(any())).thenReturn(false);

        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer bad.token"))
            .andExpect(status().isUnauthorized());
    }

        @Test
    void deleteMyAccount_success() throws Exception {
        String jwt = "dummy.jwt.token";
        when(jwtService.validateToken(jwt)).thenReturn(true);
        when(jwtService.getUserIdFromToken(jwt)).thenReturn(42L);

        mockMvc.perform(delete("/api/v1/users")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isNoContent());

        // userService.deleteAccount(42L) 가 호출됐는지 검증
        verify(userService).deleteAccount(42L);
    }

    @Test
    void deleteMyAccount_unauthorized_noHeader() throws Exception {
        mockMvc.perform(delete("/api/v1/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMyAccount_unauthorized_badToken() throws Exception {
        when(jwtService.validateToken(any())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/users")
                .header("Authorization", "Bearer bad.token"))
            .andExpect(status().isUnauthorized());
    }
}
