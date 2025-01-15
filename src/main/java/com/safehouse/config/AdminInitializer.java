// 관리자 계정 자동 생성
package com.safehouse.config;
import com.safehouse.domain.Admin;
import com.safehouse.domain.User;
import com.safehouse.repository.AdminRepository;
import com.safehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정이 없을 경우에만 생성
        if (!userRepository.existsByRole("ADMIN")) {
            // User 엔티티 생성
            User adminUser = new User();
            adminUser.setEmail("admin@safehouse.com");
            adminUser.setPassword(passwordEncoder.encode("admin123!@#"));
            adminUser.setRole("ADMIN");
            adminUser.setUserName("책임자");
            userRepository.save(adminUser);

            // Admin 엔티티 생성
            Admin admin = new Admin();
            admin.setUser(adminUser);
            adminRepository.save(admin);
        }
    }
}
