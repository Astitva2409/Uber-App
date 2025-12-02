package com.project.uber.UberApp.services;

import com.project.uber.UberApp.dto.RideRequestDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.Ride;
import com.project.uber.UberApp.entities.enums.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RideService {

    Ride getRideById(Long rideId);
    void matchWithDrivers(RideRequestDto rideRequestDto);
    Ride createNewRide(RideRequestDto rideRequestDto, Driver driver);
    Ride updateRideStatus(Long rideId, RideStatus rideStatus);
    Page<Ride> getAllRidesOfRider(Long riderId, PageRequest pageRequest);
    Page<Ride> getAllRidesOfDriver(Long driverId, PageRequest pageRequest);
}
