//package com.tranner.account_service.util;
//
//import com.tranner.account_service.security.jwt.JwtUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TokenExtractor {
//
//    public String extractToken(HttpServletRequest request) {
//        System.out.println("extractToken 진입");
//        String header = request.getHeader("Authorization");
//        if (header == null || !header.startsWith("Bearer ")) {
//            // 토큰 존재 X 예외 발생시키기
//            //throw new CustomAuthException("토큰이 존재하지 않거나 형식이 올바르지 않습니다.");
//        }
//        return header.split(" ")[1]; // 또는 substring(7)
//    }
//
//    public String extractMemberId(HttpServletRequest request, JwtUtil jwtUtil) {
//        System.out.println("extractMemberId 진입");
//        String token = extractToken(request);
//        System.out.println("accessToken: "+token);
//        String memberId = jwtUtil.getMemberId(token);
//        System.out.println("memberId: "+memberId);
//        return memberId;
//    }
//
//}
