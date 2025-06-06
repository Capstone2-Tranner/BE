package com.tranner.external_api_proxy.discovery.controller;

import com.tranner.external_api_proxy.discovery.dto.response.DetailResponseDTO;
import com.tranner.external_api_proxy.discovery.dto.response.PlaceListResponseDTO;
import com.tranner.external_api_proxy.discovery.dto.response.PlacesDTO;
import com.tranner.external_api_proxy.discovery.service.DiscoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    /*
        1. 장소 리스트 출력(장소 리스트 및 최근 인기 여행지)
        2. 장소 상세정보 출력
        3. 최근 인기 장소 출력
     */

    @GetMapping("/places")
    public ResponseEntity<PlaceListResponseDTO> places(@RequestParam("countryName") String countryName,
                                                       @RequestParam("regionName") String regionName,
                                                       @RequestParam("pageToken") @Nullable String pageToken){
        System.out.println("/api/discovery/places controller 진입");
        System.out.println("countryName"+countryName);
        System.out.println("regionName"+regionName);
        PlaceListResponseDTO placeListResponseDTO = discoveryService.getPlaces(countryName, regionName, pageToken).block();
        return ResponseEntity.ok().body(placeListResponseDTO);
    }

    @GetMapping("/details/{placeId}")
    public ResponseEntity<DetailResponseDTO> placeDetail(@PathVariable("placeId") String placeId){
        DetailResponseDTO detailResponseDTO = discoveryService.getDetails(placeId).block();
        return ResponseEntity.ok().body(detailResponseDTO);
    }

    // 최근 인기 장소 출력
    @GetMapping("/popularPlaces")
    public ResponseEntity<List<PlacesDTO>> popularPlaces(@RequestParam("countryName") String countryName,
                                                       @RequestParam("regionName") String regionName){
        System.out.println("/api/discovery/places controller 진입");
        System.out.println("countryName"+countryName);
        System.out.println("regionName"+regionName);
        List<PlacesDTO> placesDTO = discoveryService.getPopularPlaces(countryName, regionName);
        return ResponseEntity.ok().body(placesDTO);
    }

}
