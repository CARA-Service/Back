package com.syu.cara.user.service;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.UserProfileResponse;
import com.syu.cara.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {
    
    @Mock
    private UserRepository userRepo;
    
    @InjectMocks
    private UserProfileServiceImpl profileService;
    
    @Test
    void getMyProfile_returnsCorrectResponse() {
        // 1) SecurityContext에 사용자 세팅
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("kakao_12345", null)
        );
        
        // 2) userRepo가 가짜 User 반환
        User fake = User.builder()
                        .loginId("kakao_12345")
                        .email("foo@bar.com")
                        .fullName("홍길동")
                        .profileImageUrl("http://img")
                        .driverLicenseNumber("DL123")
                        .address("서울")
                        .build();
        when(userRepo.findByLoginId("kakao_12345")).thenReturn(Optional.of(fake));
        
        // 3) 서비스 호출
        UserProfileResponse dto = profileService.getMyProfile();
        
        // 4) 검증
        assertEquals("kakao_12345", dto.getLoginId());
        assertEquals("foo@bar.com", dto.getEmail());
        assertEquals("홍길동", dto.getFullName());
        // …나머지도 검증
    }
    
    @Test
    void getMyProfile_whenNotFound_throws() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("no_user", null)
        );
        when(userRepo.findByLoginId("no_user")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> profileService.getMyProfile());
    }
}
