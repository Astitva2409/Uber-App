package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.dto.RideRequestDto;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.services.DistanceService;
import com.project.uber.UberApp.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideFareDefaultFareCalculationStrategy implements RideFareCalculationStrategy {

    private final DistanceService distanceService;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService
                .calculateDistance(rideRequest.getPickupLocation(), rideRequest.getDropOffLocation());
        return distance * RIDE_FARE_MULTIPLIER;
    }
}
