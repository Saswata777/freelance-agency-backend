package com.freelance.agency.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TestimonialRequest {

    private Long leadId;

    @NotBlank(message = "Name is required")
    private String name;

    private String company;

    @NotBlank(message = "Review cannot be empty")
    private String review;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
}