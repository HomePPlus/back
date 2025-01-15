package com.safehouse.service;

import com.safehouse.domain.Inspector;
import com.safehouse.domain.Resident;
import com.safehouse.domain.User;
import com.safehouse.dto.InspectorSignUpDto;
import com.safehouse.dto.RegistrationResponseDto;
import com.safehouse.dto.ResidentSignUpDto;
import com.safehouse.dto.SignUpDto;
import com.safehouse.exception.CustomException;
import com.safehouse.repository.InspectorRepository;
import com.safehouse.repository.ResidentRepository;
import com.safehouse.repository.UserRepository;
import com.safehouse.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 입주민 등록, 점검자 등록
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;
    private final InspectorRepository inspectorRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Transactional
    public ResponseEntity<?> registerResident(ResidentSignUpDto dto) {
        try {
            validateRegistration(dto.getEmail(), dto.getPassword(), dto.getConfirmPassword());
            User user = createUser(dto, "RESIDENT");
            Resident resident = new Resident();
            resident.setUser(user);
            residentRepository.save(resident);
            return ResponseEntity.ok(new RegistrationResponseDto(getMessage("registration.success")));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> registerInspector(InspectorSignUpDto dto) {
        try {
            validateRegistration(dto.getEmail(), dto.getPassword(), dto.getConfirmPassword());
            User user = createUser(dto, "INSPECTOR");
            Inspector inspector = new Inspector();
            inspector.setUser(user);
            inspector.setInspector_company(dto.getInspector_company());
            inspector.setInspector_number(dto.getInspector_number());
            inspectorRepository.save(inspector);
            return ResponseEntity.ok(new RegistrationResponseDto(getMessage("registration.success")));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private void validateRegistration(String email, String password, String confirmPassword) {
        if (!tokenRepository.existsByEmailAndVerified(email, true)) {
            throw new CustomException.EmailNotVerifiedException(getMessage("email.not.verified"));
        }
        if (userRepository.existsByEmail(email)) {
            throw new CustomException.EmailAlreadyExistsException(getMessage("email.duplicate"));
        }
        if (!password.equals(confirmPassword)) {
            throw new CustomException.PasswordMismatchException(getMessage("password.mismatch"));
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
