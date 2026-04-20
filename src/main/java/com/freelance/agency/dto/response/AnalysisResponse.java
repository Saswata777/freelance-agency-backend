package com.freelance.agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    private String intent;
    private String message;
    private boolean matched;
    private List<ProjectResponse> matchedProjects;
}