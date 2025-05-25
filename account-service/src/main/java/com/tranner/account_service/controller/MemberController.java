package com.tranner.account_service.controller;


import com.tranner.account_service.dto.request.EmailVerificationRequestDTO;
import com.tranner.account_service.dto.request.SignupRequestDTO;
import com.tranner.account_service.service.MailService;
import com.tranner.account_service.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    
    /*
        1. 회원가입 관련 메서드
        1-1. 회원 가입
        1-2. 아이디 중복 체크
        1-3. 이메일 전송
        1-4. 이메일 인증
        1-5. 회원 탈퇴
     */
    // 1-1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDTO request) {
        memberService.signup(request);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }

    // 1-2. 아이디 중복 체크
    // return: 중복 -> true
    @GetMapping("/idDuplicatedCheck")
    public ResponseEntity<Boolean> idDuplicatedCheck(@RequestParam("id") String id){
        boolean response =  memberService.idDuplicatedCheck(id);
        return ResponseEntity.ok().body(response);
    }
    
    // 1-3. 인증코드 전송 요청
    @PostMapping("/email/verification")
    public ResponseEntity<Void> emailVerification(@RequestBody Map<String,String> request) {
        mailService.sendCodeToEmail(request.get("email"));
        return ResponseEntity.ok().build();
    }
    
    // 1-4. 인증코드 검증 요청
    @PostMapping("/email/verification/check")
    public ResponseEntity<Boolean> checkEmailVerification(@Valid @RequestBody EmailVerificationRequestDTO requestDTO) {
        Boolean response = mailService.checkVerificationCode(requestDTO);
        return ResponseEntity.ok().body(response);
    }
    
    //1-5. 회원 탈퇴
    //@PostMapping("/")

    /*
        2. 회원 관련 기능
        2-1. 장바구니 저장
        2-2. 장바구니 조회
        2-3. 마이페이지-최근 조회 목록. (내 여행 계획 목록은 다른 곳)
     */
/*
    // 2-1. 장바구니 저장
    @PostMapping("/basket/save")
    public ResponseEntity<String> saveBasket(HttpServletRequest request,
                                               @RequestBody SaveUserInfoRequest saveUserInfoRequest){

        String tokenStr = request.getHeader("Authorization");
        String token = tokenStr.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        // 저장 로직 수행
        memberService.saveUserData(username,saveUserInfoRequest);

        return ResponseEntity.ok("User 정보 (bookmarks(찜 리스트),candidateLocations(장바구니 리스트)) 저장 성공");
    }

    // 2-2. 장바구니 조회
    @GetMapping("/basket/read")

    // 2-3. 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<MypageResponse> mypage(HttpServletRequest request) {
        String tokenStr = request.getHeader("Authorization"); // jwt토큰에서 사용자 정보 추출
        String token = tokenStr.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        MypageResponse mypageResponse = memberService.getMyPage(username);
        return ResponseEntity.ok().body(mypageResponse);
    }
*/

}
