package com.safehouse.domain.user.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StreamUtils;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // InternetAddress를 사용하여 발신자 설정
            helper.setFrom(new InternetAddress("SafeHouse@gmail.com", "홈쁘라스"));
            helper.setTo(to);
            helper.setSubject("안주 회원가입 : 이메일 인증");

            // 6자리 숫자 인증코드 생성
            String verificationCode = String.format("%06d", Integer.parseInt(token));

            // 이미지 파일을 Base64로 인코딩
            ClassPathResource imageResource = new ClassPathResource("static/images/mascot.jpg");
            byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String htmlContent =
                    "<div style='background-color: #ffffff; padding: 20px; font-family: Arial, sans-serif;'>" +
                            "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                            "<div style='text-align: center;'>" +
                            "<img src='data:image/png;base64," + base64Image + "' alt='SafeHouse 로고' style='width: 200px; margin-bottom: 20px;'/>" +
                            "</div>" +
                            "<h1 style='color: #333333; text-align: center; font-size: 24px; margin: 20px 0;'>당신의 안전한 주택, 안주</h1>" +
                            "<p style='color: #666666; text-align: center; font-size: 16px; margin: 10px 0;'>아래의 인증번호를 입력해주세요.</p>" +
                            "<div style='text-align: center; margin: 30px 0;'>" +
                            "<div style='font-size: 32px; font-weight: bold; color: #4a90e2; letter-spacing: 8px; background-color: #f8f9fa; padding: 20px; display: inline-block; border-radius: 5px;'>" +
                            verificationCode +
                            "</div>" +
                            "</div>" +
                            "<p style='color: #999999; text-align: center; font-size: 14px; margin-top: 30px;'>본 인증번호는 30분간 유효합니다.</p>" +
                            "</div>" +
                            "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("email.delivery.failed", e);
        }
    }
    public void sendPasswordResetCode(String to, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("SafeHouse@gmail.com", "홈쁘라스"));
            helper.setTo(to);
            helper.setSubject("안주 : 비밀번호 재설정 코드");

            String htmlContent =
                    "<div style='background-color: #ffffff; padding: 20px; font-family: Arial, sans-serif;'>" +
                            "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                            "<h1 style='color: #333333; text-align: center; font-size: 24px; margin: 20px 0;'>비밀번호 재설정 코드</h1>" +
                            "<p style='color: #666666; text-align: center; font-size: 16px; margin: 10px 0;'>아래의 코드를 입력하여 비밀번호를 재설정해주세요.</p>" +
                            "<div style='text-align: center; margin: 30px 0;'>" +
                            "<div style='font-size: 32px; font-weight: bold; color: #4a90e2; letter-spacing: 8px; background-color: #f8f9fa; padding: 20px; display: inline-block; border-radius: 5px;'>" +
                            code +
                            "</div>" +
                            "</div>" +
                            "<p style='color: #999999; text-align: center; font-size: 14px; margin-top: 30px;'>본 코드는 15분간 유효합니다.</p>" +
                            "</div>" +
                            "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("email.delivery.failed", e);
        }
    }


}
