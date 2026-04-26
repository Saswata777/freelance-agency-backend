package com.freelance.agency.service;

import com.freelance.agency.dto.response.ProjectResponse;
import com.freelance.agency.entity.Project;
import com.freelance.agency.exception.ResourceNotFoundException;
import com.freelance.agency.mapper.ProjectMapper;
import com.freelance.agency.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectResponse> getAllActiveProjects() {
        log.debug("Fetching all active projects");
        List<Project> projects = projectRepository.findByActiveTrue();
        return projectMapper.toResponseList(projects);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + id));
        return projectMapper.toResponse(project);
    }
}