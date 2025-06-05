//package com.tranner.external_api_proxy.search.controller;
//
//import com.tranner.external_api_proxy.search.service.SearchService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.lang.Nullable;
//import org.springframework.web.bind.annotation.*;
//
//public class SearchController {
//
//    @RestController
//    @RequiredArgsConstructor
//    @RequestMapping("/api/discovery")
//    public class DiscoveryController {
//
//        private final SearchService searchService;
//
//        /*
//            1. 검색어로 검색 text search, location 설정
//            2. 타입 별 검색(명소, 맛집, 숙소)
//         */
//
//        /**
//         * @param text
//         * @param latitude
//         * @param longitude
//         * @return
//         */
//        //1. 검색어로 검색
//        @GetMapping("/searchByText")
//        public ResponseEntity<PlaceListResponseDTO> searchByText(@RequestParam("text") String text,
//                                                                @RequestParam("latitude") Double latitude,
//                                                                @RequestParam("longitude") Double longitude,
//                                                                 @RequestParam("pageToken") @Nullable String pageToken){
//            PlaceListResponseDTO placeListResponseDTO = searchService.searchByText(text, latitude, longitude, pageToken);
//            return ResponseEntity.ok().body(placeListResponseDTO);
//        }
//
//        /**
//         * @param latitude
//         * @param longitude
//         * @param type
//         * @return
//         */
//        //1. 타입 별 검색(명소, 맛집, 숙소)
//        @GetMapping("/searchByText")
//        public ResponseEntity<PlaceListResponseDTO> searchByType(@RequestParam("type") String type,
//                                                                 @RequestParam("latitude") Double latitude,
//                                                                 @RequestParam("longitude") Double longitude,
//                                                                 @RequestParam("pageToken") @Nullable String pageToken){
//            PlaceListResponseDTO placeListResponseDTO = searchService.searchByType(type, latitude, longitude, pageToken);
//            return ResponseEntity.ok().body(placeListResponseDTO);
//        }
//
//    }
//
//
//}
