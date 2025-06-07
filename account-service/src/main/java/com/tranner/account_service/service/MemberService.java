package com.tranner.account_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.account_service.dto.request.SignupRequestDTO;
import com.tranner.account_service.domain.Member;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.type.MemberType;
import com.tranner.account_service.type.Role;
import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    /*
        엑세스 토큰 재발급
     */
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 refreshToken 꺼내기
        String refreshToken = extractRefreshTokenFromCookies(request);
        System.out.println("refreshToken: "+refreshToken);

        if (refreshToken == null) {
            System.out.println("refreshToken is null");
            throw new BusinessLogicException(AccountErrorCode.MISSING_REFRESH_TOKEN);
        }

        // 2. Redis에서 memberId 추출 (value = memberId)
        String memberId = redisService.getMemberIdFromRefreshToken(refreshToken);
        System.out.println("memberId: "+memberId);
        if (memberId == null) {
            throw new BusinessLogicException(AccountErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. role (DB나 캐시에서 가져오거나 하드코딩)
        String role = Role.ROLE_USER.getKey();

        // 5. 새 AccessToken 발급
        String newAccessToken = jwtUtil.createAccessToken(memberId, role);

        // 이 이후에 redis에서 refresh token 삭제??

        // 6. 응답 헤더 및 바디 구성
        response.addHeader("Authorization", "Bearer " + newAccessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> responseBody = Map.of("memberId", memberId);
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        } catch (IOException e) {
            throw new RuntimeException("[Service: refreshAccessToken]응답 생성 실패", e);
        }
    }

    /*
        1. 회원 가입 관련 메서드
        1-1. 회원 가입(회원 정보 저장)
        1-2. 아이디 중복 확인
        1-3. 회원 삭제
     */

    // 1-1. 회원 가입(회원 정보 저장)
    public void signup(SignupRequestDTO request) {
        // 이미 등록된 이메일 여부 확인
        if (memberRepository.existsByEmail(request.memberEmail())) {
            throw new BusinessLogicException(AccountErrorCode.DUPLICATE_EMAIL);
        }
        // 아이디 중복 확인
        if (idDuplicatedCheck(request.memberId())){
            throw new BusinessLogicException(AccountErrorCode.USERID_EXISTS);
        }
        Member member = Member.builder()
                .memberId(request.memberId())
                .password(bCryptPasswordEncoder.encode(request.password())) //비밀번호는 암호화하여 저장
                .email(request.memberEmail())
                .memberType(MemberType.OWN)
                .registerDate(LocalDate.now())
                .role(Role.ROLE_USER) // ADMIN은 관리자만 주도록 설정해야함
                .build();
        memberRepository.save(member);
    }


    // 1-2. 아이디 중복 확인
    public boolean idDuplicatedCheck(String memberID) {
        System.out.println("Service: 아아디 중복 체크 진입");
        //중복: true 중복 X: false
        return memberRepository.existsByMemberId(memberID);
    }
    
    // 1-3. 회원 삭제

    
    /*
        2. 회원 정보 관련 메서드
        2-1. 장바구니 조회
        2-2. 장바구니 저장
        2-3. 장바구니 삭제
        2-4. 최근 조회 목록 조회
     */
    
    // 2-1. 장바구니 조회
    
    // 2-2. 장바구니 저장
    
    // 2-3. 장바구니 삭제
    
    // 2-4. 최근 조회 목록 조회



/*
    // 토큰에서 추출한 사용자 정보로 마이페이지에서 조회할 찜 리스트, 스케줄 리스트 반환
    public MypageResponse getMyPage(String username){

        Member member =memberRepository.findByUsername(username);

        List<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(member.getId()); // 멤버가 찜한 장소 리스트 반환
        List<BookmarkResponse> bookmarksList = bookmarks.stream().map(BookmarkResponse::of).toList();
        log.info("마이페이지 내부 북마크 = {}", bookmarksList);

        List<Schedule> schedules = member.getSchedules();
        List<ScheduleResponse> schedulesList = schedules.stream().map(ScheduleResponse::of).toList();
        log.info("마이페이지 내부 스케줄 = {}", schedulesList);

        MypageResponse mypageResponse = new MypageResponse(schedulesList, bookmarksList);
        log.info("mypageResponse = {}",mypageResponse);

        return mypageResponse;
    }



    // 사용자의 장바구니 정보를 추출
    public MainpageResponse getCandidateLocations(String username){
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username);
        }
        List<CandidateLocation> candidateLocations = candidateLocationRepository.findAllByMemberId(member.getId());
        List<CandidateLocationResponse> candidateLocationList = candidateLocations.stream()
                .map(CandidateLocationResponse::of)
                .toList();
        log.info("멤버의 장바구니 정보 = {}", candidateLocationList);
        return new MainpageResponse(candidateLocationList, null);
    }




    // 이메일 형식 검증 메서드
    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // 간단한 이메일 정규식
        return email.matches(emailRegex);
    }

    // 이메일 등록 여부 확인 메서드
    private boolean isEmailRegistered(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
*/
    /**
     * 창을 닫거나, 로그아웃 시
     * user의 정보
     * bookmarks(찜 리스트),
     * candidateLocations(장바구니 리스트)
     * 를 저장
     */

    /*
    @Transactional
    public void saveUserData(String username,
                             SaveUserInfoRequest saveUserInfoRequest) {

        // 1. username으로 Member 조회
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        log.info("member 조회 결과 = {}", member);

        // 2. 기존 Bookmarks 삭제 후 새로 추가
        member.deleteAllBookmarks();
        log.info("{}의 모든 bookmarks = {}", member, member.getBookmarks());
        List<Bookmark> bookmarks = getBookmarks(saveUserInfoRequest);
        for(Bookmark bookmark : bookmarks){
            member.addBookmark(bookmark);
        }
        log.info("{}의 모든 bookmarks = {}", member, member.getBookmarks());
        log.info("북마크 데이터 저장 완료. 데이터: {}", bookmarks);

        // 3. 기존 CandidateLocations 삭제 후 새로 추가
        member.deleteAllCandidateLocations();
        log.info("{}의 모든 candidateLocations = {}", member, member.getCandidateLocations());
        List<CandidateLocation> candidateLocations = getCandidateLocations(saveUserInfoRequest, member);
        for(CandidateLocation candidateLocation : candidateLocations){
            member.addCandidateLocation(candidateLocation);
        }
        log.info("{}의 모든 candidateLocations = {}", member, member.getCandidateLocations());
        log.info("장바구니 데이터 저장 완료. 데이터: {}", candidateLocations);
    }

    // 장바구니 조회
    private static List<CandidateLocation> getCandidateLocations(SaveUserInfoRequest saveUserInfoRequest, Member member) {
        return saveUserInfoRequest.getCandidateLocations().stream()
                .map(locationRequest -> CandidateLocation.builder()
                        .member(member)
                        .location(locationRequest.location())
                        .build())
                .toList();
    }

    */


    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


}
