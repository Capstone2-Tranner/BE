package com.tranner.external_api_proxy.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class V1KeywordSearchResponse {
    private List<V1KeywordSearchResult> places;
    private String nextPageToken;

}
