package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.DriverDto;
import com.project.uber.UberApp.dto.RideDto;
import com.project.uber.UberApp.dto.RideRequestDto;
import com.project.uber.UberApp.dto.RiderDto;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.entities.Rider;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.RideRequestStatus;
import com.project.uber.UberApp.exception.ResourceNotFoundException;
import com.project.uber.UberApp.repository.RideRequestRepository;
import com.project.uber.UberApp.repository.RiderRepository;
import com.project.uber.UberApp.services.RiderService;
import com.project.uber.UberApp.strategies.DriverMatchingStrategy;
import com.project.uber.UberApp.strategies.RideFareCalculationStrategy;
import com.project.uber.UberApp.strategies.StrategyManager;
import com.project.uber.UberApp.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import static com.project.uber.UberApp.entities.enums.RideRequestStatus.PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final StrategyManager strategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        Rider rider = getCurrentRider();
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        Double fare = strategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        rideRequestRepository.save(rideRequest);
        strategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);

        return modelMapper.map(rideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public RiderDto getMyProfile() {
        return null;
    }

    @Override
    public List<RideDto> getAllRides() {
        return List.of();
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .rating(0.0)
                .build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        // TODO: implement spring security to get current rider context
        return riderRepository.findById(1L).orElseThrow(
                () -> new ResourceNotFoundException("Rider not found with id " + 1));
    }
}
