package com.safehouse.domain.user.service;

import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.service.AddressUtil;
import com.safehouse.domain.user.entity.Inspector;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.users.dto.request.InspectorSignUpDto;
import com.safehouse.api.users.dto.response.RegistrationResponseDto;
import com.safehouse.api.users.dto.request.ResidentSignUpDto;
import com.safehouse.api.users.dto.request.SignUpDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.domain.user.repository.InspectorRepository;
import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.domain.user.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 입주민 등록, 점검자 등록
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final InspectorRepository inspectorRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Transactional
    public ApiResponse<RegistrationResponseDto> registerResident(ResidentSignUpDto dto) {
        validateRegistration(dto.getEmail(), dto.getPassword());

         User user = createUser(dto, "RESIDENT");
        RegistrationResponseDto responseDto = new RegistrationResponseDto(
                true,
                getMessage("registration.success"),
                "RESIDENT"
        );

        return new ApiResponse<>(
                200,
                getMessage("registration.success"),
                responseDto
        );
    }

    @Transactional
    public ApiResponse<RegistrationResponseDto> registerInspector(InspectorSignUpDto dto) {
        validateRegistration(dto.getEmail(), dto.getPassword());

        User user = createUser(dto, "INSPECTOR");

        // 주소에서 구 정보 추출
        String district = AddressUtil.extractDistrict(dto.getDetailAddress());

        Inspector inspector = new Inspector();
        inspector.setUser(user);
        inspector.setInspector_company(dto.getInspector_company());
        inspector.setInspector_number(dto.getInspector_number());
        inspector.setArea(district); // 추출한 구 정보 설정

        inspectorRepository.save(inspector);

        RegistrationResponseDto responseDto = new RegistrationResponseDto(
                true,
                getMessage("registration.success"),
                "INSPECTOR"
        );

        return new ApiResponse<>(
                200,
                getMessage("registration.success"),
                responseDto
        );
    }

    private void validateRegistration(String email, String password) {
        if (!tokenRepository.existsByEmailAndVerified(email, true)) {
            throw new CustomException.EmailNotVerifiedException(getMessage("email.not.verified"));
        }
        if (userRepository.existsByEmail(email)) {
            throw new CustomException.ConflictException(getMessage("email.duplicate"));
        }
    }

    private User createUser(SignUpDto dto, String role) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(formatPhoneNumber(dto.getPhone()));
        user.setDetailAddress(dto.getDetailAddress());
        user.setRole(role);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    private String formatPhoneNumber(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
