package com.project.uber.UberApp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PointDto {

    @NotNull(message = "Coordinates cannot be null")
    @Size(min = 2, max = 2, message = "Coordinates must exactly contain longitude and latitude")
    private double[] coordinates;

    private String type = "Point";

    public PointDto(double[] coordinates) {
        this.coordinates = coordinates;
    }
}