package com.freelance.agency.service;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.response.AnalysisResponse;
import com.freelance.agency.dto.response.ProjectResponse;
import com.freelance.agency.entity.Project;
import com.freelance.agency.mapper.ProjectMapper;
import com.freelance.agency.repository.ProjectRepository;
import com.freelance.agency.util.TextNormalizer;
import com.freelance.agency.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TextNormalizer textNormalizer;
    private final ValidationUtil validationUtil;

    /**
     * Main method — analyze user input and return matched projects
     */
    public AnalysisResponse analyze(String input) {

        // Step 1: Handle empty or spam input
        if (validationUtil.isBlank(input)) {
            log.warn("Empty input received");
            return buildNoMatchResponse(AppConstants.MSG_EMPTY_INPUT, "unknown");
        }

        if (validationUtil.isSpam(input)) {
            log.warn("Spam input detected: {}", input);
            return buildNoMatchResponse(AppConstants.MSG_SPAM_INPUT, "spam");
        }

        // Step 2: Normalize input → extract keywords
        List<String> keywords = textNormalizer.normalize(input);
        log.debug("Normalized keywords: {}", keywords);

        if (keywords.isEmpty()) {
            return buildNoMatchResponse(AppConstants.MSG_EMPTY_INPUT, "unknown");
        }

        // Step 3: Detect intent from keywords
        String intent = detectIntent(keywords);
        log.debug("Detected intent: {}", intent);

        // Step 4: Load all active projects
        List<Project> allProjects = projectRepository.findByActiveTrue();

        // Step 5: Score each project
        Map<Project, Integer> scoreMap = scoreProjects(allProjects, keywords);

        // Step 6: Filter projects with score >= MIN_MATCH_SCORE
        List<Project> matched = scoreMap.entrySet().stream()
                .filter(e -> e.getValue() >= AppConstants.MIN_MATCH_SCORE)
                .sorted(Map.Entry.<Project, Integer>comparingByValue().reversed())
                .limit(AppConstants.MAX_RECOMMENDATIONS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Step 7: No match → fallback
        if (matched.isEmpty()) {
            log.info("No projects matched for input: {}", input);
            return buildNoMatchResponse(AppConstants.MSG_NO_MATCH, intent);
        }

        // Step 8: Return matched projects
        List<ProjectResponse> projectResponses = projectMapper.toResponseList(matched);

        log.info("Matched {} project(s) for intent: {}", matched.size(), intent);

        return AnalysisResponse.builder()
                .intent(intent)
                .message(AppConstants.MSG_MATCH_FOUND)
                .matched(true)
                .matchedProjects(projectResponses)
                .build();
    }

    /**
     * Score each project based on keyword overlap with project tags
     */
    private Map<Project, Integer> scoreProjects(List<Project> projects, List<String> keywords) {
        Map<Project, Integer> scoreMap = new HashMap<>();

        for (Project project : projects) {
            int score = 0;

            if (project.getTags() == null || project.getTags().isEmpty()) continue;

            for (String keyword : keywords) {
                for (String tag : project.getTags()) {
                    if (tag.toLowerCase().contains(keyword)
                            || keyword.contains(tag.toLowerCase())) {
                        score++;
                    }
                }
            }

            if (score > 0) {
                scoreMap.put(project, score);
            }
        }

        return scoreMap;
    }

    /**
     * Detect high-level intent from keywords
     */
    private String detectIntent(List<String> keywords) {

        Map<String, List<String>> intentMap = new LinkedHashMap<>();
        intentMap.put("chatbot",    Arrays.asList("chatbot", "bot", "support", "faq", "chat", "assistant"));
        intentMap.put("ecommerce",  Arrays.asList("ecommerce", "shop", "store", "product", "cart", "order", "buy", "sell"));
        intentMap.put("dashboard",  Arrays.asList("dashboard", "analytics", "report", "graph", "chart", "data", "admin"));
        intentMap.put("email",      Arrays.asList("email", "mail", "newsletter", "campaign", "smtp", "notification"));
        intentMap.put("booking",    Arrays.asList("booking", "appointment", "schedule", "slot", "calendar", "reservation"));
        intentMap.put("payment",    Arrays.asList("payment", "gateway", "razorpay", "stripe", "invoice", "billing"));
        intentMap.put("website",    Arrays.asList("website", "landing", "portfolio", "blog", "page", "site"));
        intentMap.put("mobile",     Arrays.asList("mobile", "app", "android", "ios", "flutter", "react native"));
        intentMap.put("api",        Arrays.asList("api", "rest", "backend", "server", "integration", "microservice"));
        intentMap.put("automation", Arrays.asList("automation", "automate", "workflow", "script", "cron", "scheduler"));

        Map<String, Integer> intentScores = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : intentMap.entrySet()) {
            int score = 0;
            for (String keyword : keywords) {
                if (entry.getValue().contains(keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                intentScores.put(entry.getKey(), score);
            }
        }

        return intentScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("custom");
    }

    /**
     * Build a no-match / fallback response
     */
    private AnalysisResponse buildNoMatchResponse(String message, String intent) {
        return AnalysisResponse.builder()
                .intent(intent)
                .message(message)
                .matched(false)
                .matchedProjects(Collections.emptyList())
                .build();
    }
}