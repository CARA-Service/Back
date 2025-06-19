package com.syu.cara.user.service;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.UserProfileResponse;
import com.syu.cara.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepo;

    public UserProfileServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserProfileResponse getMyProfile() {
        String loginId = SecurityContextHolder.getContext()
                              .getAuthentication()
                              .getName();

        User user = userRepo.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("로그인된 사용자를 찾을 수 없습니다."));

        return new UserProfileResponse(
            user.getLoginId(),
            user.getEmail(),
            user.getFullName(),
            user.getProfileImageUrl(),
            user.getDriverLicenseNumber(),
            user.getAddress()
        );
    }
}
