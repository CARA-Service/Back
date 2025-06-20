package com.syu.cara.user.service;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * JWT에서 추출한 userId를 기반으로 DB에서 User를 조회하고,
 * Spring Security에게 필요한 UserDetails 타입으로 변환해서 반환하는 서비스
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * (1) JWT 필터가 userId를 갖고 있는 상황을 가정합니다.
     *     여기서는 loadUserById 메서드를 별도로 만들어 userId로 조회하고,
     *     기존 UserDetailsService의 loadUserByUsername은 username(즉 loginId) 기반 조회를 지원합니다.
     */

    // JWT 필터에서 userId를 직접 넘겨받아 사용할 메서드
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return toUserDetails(user);
    }

    // (2) username(=loginId) 기반 인증을 지원하려면 반드시 이 메서드가 loginId 조회를 해야 합니다.
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // ★ 여기서 findByKakaoId(loginId) 가 아니라, findByLoginId(loginId) 로 바꿔야 합니다.
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));

        return toUserDetails(user);
    }

    // User 엔티티를 Spring Security가 필요로 하는 UserDetails로 변환
    private UserDetails toUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}
