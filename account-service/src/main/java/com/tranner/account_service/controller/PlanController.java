package com.tranner.account_service.controller;

import com.tranner.account_service.dto.request.EmailOnlyRequestDTO;
import com.tranner.account_service.dto.request.EmailVerificationRequestDTO;
import com.tranner.account_service.dto.request.PlanRequestDTO;
import com.tranner.account_service.dto.request.SignupRequestDTO;
import com.tranner.account_service.dto.response.PlanDetailResponseDTO;
import com.tranner.account_service.dto.response.PlanListResponseDTO;
import com.tranner.account_service.dto.response.PlanModifyResponseDTO;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.service.PlanService;
import com.tranner.account_service.util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class PlanController {

    private final PlanService planService;

    private final JwtUtil jwtUtil;
    private final TokenExtractor tokenExtractor;
    /*
        1. 여행 계획 관련 메서드
        1-1. 여행 계획 리스트 출력
        1-2. 여행 계획 상세 정보 출력
        1-3. 여행 계획 생성
        1-4. 여행 계획 삭제
        1-5. 여행 계획 수정
     */
    // 1-1. 여행 계획 리스트 출력
    @GetMapping("/planList")
    public ResponseEntity<PlanListResponseDTO> readPlanList(HttpServletRequest request) {
        String memberId = tokenExtractor.extractMemberId(request, jwtUtil);
        //String memberId = "testUser01";
        //여행 계획 리스트 출력
        PlanListResponseDTO planListResponseDTO = planService.readPlanList(memberId);
        return ResponseEntity.ok().body(planListResponseDTO);
    }

    /**
     *
     * @param id
     * @return
     */
    // 1-2. 여행 계획 상세 정보 출력
    // return: 중복 -> true
    @GetMapping("/planDetail")
    public ResponseEntity<PlanDetailResponseDTO> readPlanDetail(@RequestParam("id") Long id){
        //스케줄 식별자로 스케줄 가져오기
        PlanDetailResponseDTO planDetailResponseDTO = planService.readPlanDetail(id);
        return ResponseEntity.ok().body(planDetailResponseDTO);
    }

    // 여행 계획 상세 정보 -> 수정으로 넘어갈 때 따로 가져오기.
    @GetMapping("/planDetail/modify")
    public ResponseEntity<PlanModifyResponseDTO> readPlanDetailToModify(@RequestParam("id") Long id){
        //스케줄 식별자로 스케줄 가져오기
        PlanModifyResponseDTO planModifyResponseDTO = planService.readPlanDetailToModify(id);
        return ResponseEntity.ok().body(planModifyResponseDTO);
    }

    /**
     *
     * @param planRequestDTO
     * @return
     */
    // 1-3. 여행 계획 생성
    @PostMapping("/plan/save")
    public ResponseEntity<Void> savePlan(HttpServletRequest request, @RequestBody PlanRequestDTO planRequestDTO) {
        String memberId = tokenExtractor.extractMemberId(request, jwtUtil);
        //String memberId = "testUser01";
        //db에 저장
        planService.savePlan(memberId, planRequestDTO);
        return ResponseEntity.ok().build();
    }

    // 1-4. 여행 계획 삭제
    @DeleteMapping("/plan/delete")
    public ResponseEntity<Void> deletePlan(@RequestParam("id") Long id) {
        //삭제
        planService.deletePlan(id);
        return ResponseEntity.ok().build();
    }

    //1-5. 여행 계획 수정
    @PostMapping("/plan/modify")
    public ResponseEntity<Boolean> modifyPlan(HttpServletRequest request, @RequestBody PlanRequestDTO planRequestDTO) {
        String memberId = tokenExtractor.extractMemberId(request, jwtUtil);
        //String memberId = "testUser01";
        //db에서 삭제
        planService.deletePlan(planRequestDTO.scheduleId());
        //db에 저장
        planService.savePlan(memberId, planRequestDTO);
        return ResponseEntity.ok().build();
    }

}
