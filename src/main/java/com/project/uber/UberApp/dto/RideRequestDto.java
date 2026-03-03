package com.project.uber.UberApp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideRequestDto {

    @Valid
    @NotNull(message = "Pickup location is required")
    private PointDto pickUpLocation;

    @Valid
    @NotNull(message = "Drop-off location is required")
    private PointDto dropOffLocation;
}