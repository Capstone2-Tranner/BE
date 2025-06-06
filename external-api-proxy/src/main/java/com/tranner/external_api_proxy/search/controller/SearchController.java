package com.tranner.external_api_proxy.search.controller;

import com.tranner.external_api_proxy.discovery.dto.response.DetailResponseDTO;
import com.tranner.external_api_proxy.search.dto.response.SearchResponseDTO;
import com.tranner.external_api_proxy.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {


    private final SearchService searchService;

    /*
        1. 검색어로 검색 text search, location 설정
        2. 타입 별 검색(명소, 맛집, 숙소)
     */

    /**
     * @param text
     * @param latitude
     * @param longitude
     * @return
     */
    //1. 검색어로 검색
    @GetMapping("/searchByText")
    public ResponseEntity<SearchResponseDTO> searchByText(@RequestParam("text") String text,
                                                          @RequestParam("latitude") Double latitude,
                                                          @RequestParam("longitude") Double longitude,
                                                          @RequestParam("pageToken") @Nullable String pageToken){
        SearchResponseDTO searchResponseDTO = searchService.searchByText(text, latitude, longitude, pageToken);
        return ResponseEntity.ok().body(searchResponseDTO);
    }

    /**
     * @param latitude
     * @param longitude
     * @param type
     * @return
     */
    //1. 타입 별 검색(명소, 맛집, 숙소)
    @GetMapping("/searchByType")
    public ResponseEntity<SearchResponseDTO> searchByType(@RequestParam("type") String type,
                                                             @RequestParam("latitude") Double latitude,
                                                             @RequestParam("longitude") Double longitude,
                                                             @RequestParam("pageToken") @Nullable String pageToken){
        SearchResponseDTO searchResponseDTO = searchService.searchByType(type, latitude, longitude, pageToken);
        return ResponseEntity.ok().body(searchResponseDTO);
    }



    @GetMapping("/details/{placeId}")
    public ResponseEntity<DetailResponseDTO> placeDetail(@PathVariable("placeId") String placeId){
        DetailResponseDTO detailResponseDTO = searchService.getDetails(placeId).block();
        return ResponseEntity.ok().body(detailResponseDTO);
    }


}
