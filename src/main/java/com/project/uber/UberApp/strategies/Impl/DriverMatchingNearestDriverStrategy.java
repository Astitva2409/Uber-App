package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.dto.RideRequestDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.repository.DriverRepository;
import com.project.uber.UberApp.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        return driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());
    }
}
