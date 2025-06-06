package com.tranner.external_api_proxy.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResponseDTO {
    private List<PlaceDTO> places;
    private String pageToken; // null이면 더 이상 없음
}
