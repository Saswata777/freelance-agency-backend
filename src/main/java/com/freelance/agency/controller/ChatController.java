package com.freelance.agency.controller;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.RequirementRequest;
import com.freelance.agency.dto.response.AnalysisResponse;
import com.freelance.agency.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.API_CHAT)
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeRequirement(
            @Valid @RequestBody RequirementRequest request) {

        log.info("Analyze request received: {}", request.getMessage());
        AnalysisResponse response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }
}