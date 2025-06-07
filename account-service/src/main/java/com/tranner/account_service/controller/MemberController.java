package com.tranner.account_service.controller;


import com.tranner.account_service.dto.request.*;
import com.tranner.account_service.dto.response.BasketResponseDTO;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.security.user.CustomUserDetails;
import com.tranner.account_service.service.BasketService;
import com.tranner.account_service.service.MailService;
import com.tranner.account_service.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class MemberController {

    private final MemberService memberService;
    private final BasketService basketService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;

    /*
        엑세스 토큰 재발급 메서드
     */
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        memberService.refreshAccessToken(request, response);
        return ResponseEntity.ok().build();  // 응답 바디는 service 내부에서 직접 작성됨
    }


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
        System.out.println("Controller: signup 진입");
        memberService.signup(request);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }

    // 1-2. 아이디 중복 체크
    // return: 중복 -> true
    @GetMapping("/idDuplicatedCheck")
    public ResponseEntity<Boolean> idDuplicatedCheck(@RequestParam("id") String id){
        System.out.println("Controller: 아아디 중복 체크 진입");
        boolean response =  memberService.idDuplicatedCheck(id);
        return ResponseEntity.ok().body(response);
    }
    
    // 1-3. 인증코드 전송 요청
    @PostMapping("/email/verification")
    public ResponseEntity<Void> emailVerification(@Valid @RequestBody EmailOnlyRequestDTO requestDTO) {
        System.out.println("Controller: 인증코드 전송 요청 진입");
        mailService.sendCodeToEmail(requestDTO.email());
        return ResponseEntity.ok().build();
    }
    
    // 1-4. 인증코드 검증 요청
    @PostMapping("/email/verification/check")
    public ResponseEntity<Boolean> checkEmailVerification(@Valid @RequestBody EmailVerificationRequestDTO requestDTO) {
        System.out.println("Controller: 인증코드 검증 요청 진입");
        Boolean response = mailService.checkVerificationCode(requestDTO);
        return ResponseEntity.ok().body(response);
    }
    
    //1-5. 회원 탈퇴
    //@PostMapping("/")

    /*
        2. 회원 관련 기능
        2-1. 장바구니 조회
        2-2. 장바구니 아이템 삽입
        2-3. 장바구니 아이템 삭제
        2-4. 마이페이지-최근 조회 목록. (내 여행 계획 목록은 다른 곳)
     */

    // 2-1. 장바구니 조회
    @GetMapping("/basket/read")
    public ResponseEntity<BasketResponseDTO> readBasket(HttpServletRequest request,
                                                        @RequestParam("countryName") String countryName,
                                                        @RequestParam("regionName") String regionName){
        //String memberId = tokenExtractor.extractMemberId(request, jwtUtil);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberId = authentication.getName(); // 보통 여기서 memberId 반환됨

        //String memberId = "testUser01";

        System.out.println("✔ [readBasket] memberId: "+memberId);

        BasketResponseDTO basketResponseDTO = basketService.readBasket(memberId, countryName, regionName);
        return ResponseEntity.ok().body(basketResponseDTO);
    }

    // 2-2. 장바구니 아이템 삽입
    @PostMapping("/basket/insert")
    public ResponseEntity<String> insertBasket(HttpServletRequest request,
                                             @RequestBody InsertBasketRequestDTO insertBasketRequestDTO){
        //String memberId = tokenExtractor.extractMemberId(request, jwtUtil);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberId = authentication.getName(); // 보통 여기서 memberId 반환됨
        //String memberId = "testUser01";
        System.out.println("✔ [readBasket] memberId: "+memberId);

        // 저장 로직 수행
        basketService.insertBasket(memberId,insertBasketRequestDTO);
        return ResponseEntity.ok("장바구니에 장소 저장 성공");
    }

    // 2-2. 장바구니 아이템 삭제
    @PostMapping("/basket/delete")
    public ResponseEntity<String> deleteBasket(HttpServletRequest request,
                                             @RequestBody DeleteBasketRequestDTO deleteBasketRequestDTO){
        //String memberId = tokenExtractor.extractMemberId(request, jwtUtil);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberId = authentication.getName(); // 보통 여기서 memberId 반환됨
        //String memberId = "testUser01";
        System.out.println("✔ [readBasket] memberId: "+memberId);
        // 삭제 로직 수행
        basketService.deleteBasket(memberId,deleteBasketRequestDTO);
        return ResponseEntity.ok("장바구니에서 장소 삭제 성공");
    }


/*
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
