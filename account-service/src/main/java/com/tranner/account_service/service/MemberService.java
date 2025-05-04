package com.tranner.account_service.service;

import com.tranner.account_service.dto.request.SignupRequestDTO;
import com.tranner.account_service.entity.Member;
import com.tranner.account_service.entity.MemberType;
import com.tranner.account_service.entity.Role;
import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                .role(Role.USER) // ADMIN은 관리자만 주도록 설정해야함
                .build();
        memberRepository.save(member);
    }


    // 1-2. 아이디 중복 확인
    public boolean idDuplicatedCheck(String memberID) {
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



}
