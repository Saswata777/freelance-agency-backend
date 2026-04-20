package com.freelance.agency.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequirementRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    @Email(message = "Invalid email format")
    private String email;

    private String name;
}