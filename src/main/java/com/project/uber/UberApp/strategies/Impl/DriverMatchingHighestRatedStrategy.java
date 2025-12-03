package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.dto.RideRequestDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.strategies.DriverMatchingStrategy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverMatchingHighestRatedStrategy implements DriverMatchingStrategy {
    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        return List.of();
    }
}
