package com.tranner.account_service.service;

import com.tranner.account_service.dto.request.EmailVerificationRequestDTO;
import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.exception.custom.InternalServerException;
import com.tranner.account_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final long VERIFICATION_CODE_TTL = 5L; // 5분

    private final RedisService redisService;
    private final MemberRepository memberRepository;

    private final JavaMailSender emailSender;

    /*
        1. 인증코드 전송
        2. 인증코드 검증
     */

    // 1. 인증코드 전송
    public void sendCodeToEmail(String email) {
        // 이미 등록된 이메일인지 확인
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessLogicException(AccountErrorCode.DUPLICATE_EMAIL);
        }
        // 인증 코드 생성(랜덤 코드 생성)
        String code = generateRandomCode(); 
        sendEmail(email, "Tranner 인증코드", "인증 코드: " + code);

        // Redis에 인증 코드 저장 (TTL: 5분)
        redisService.saveEmailVerificationCode(email, code, VERIFICATION_CODE_TTL);
    }

    // 2. 인증코드 검증
    public Boolean checkVerificationCode(EmailVerificationRequestDTO requestDTO) {

        String email = requestDTO.email();
        String storedCode = redisService.getEmailVerificationCode(email);

        if (storedCode == null) {
            throw new BusinessLogicException(AccountErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!storedCode.equals(requestDTO.verificationCode())) {
            throw new BusinessLogicException(AccountErrorCode.WRONG_VERIFICATION_CODE);
        }

        redisService.deleteEmailVerificationCode(email); // 검증 후 제거

        return true;

    }

    // 인증코드 생성(랜덤 코드 생성)
    private String generateRandomCode() {
        int randomCode = new Random().nextInt(999999); // 0~999999 사이의 난수 생성
        return String.format("%06d", randomCode); // 6자리 문자열로 변환
    }

    // 이메일 전송
    public void sendEmail(String toEmail,
                          String title,
                          String text){
        SimpleMailMessage emailForm = createEmailForm(toEmail,title,text);
        try{
            emailSender.send(emailForm);
        }catch (RuntimeException e){
            throw new InternalServerException(AccountErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }
    
    // 이메일 형식 작성
    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

}
