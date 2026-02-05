package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.DriverDto;
import com.project.uber.UberApp.dto.RiderDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.Rating;
import com.project.uber.UberApp.entities.Ride;
import com.project.uber.UberApp.entities.Rider;
import com.project.uber.UberApp.exception.ResourceNotFoundException;
import com.project.uber.UberApp.exception.RuntimeConflictException;
import com.project.uber.UberApp.repository.DriverRepository;
import com.project.uber.UberApp.repository.RatingRepository;
import com.project.uber.UberApp.repository.RiderRepository;
import com.project.uber.UberApp.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverDto rateDriver(Ride ride, Integer rating) {
        // get the driver from ride
        Driver driver = ride.getDriver();

        // get the rating for the particular ride
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for this ride id "+ride.getId()));

        // if driver is already rated
        if (ratingObj.getDriverRating() != null)
            throw new RuntimeConflictException("Driver is already rated, cannot rate again");

        // set driver rating in ratingObj
        ratingObj.setDriverRating(rating);

        // save the rating in repository
        ratingRepository.save(ratingObj);

        // calculate average rating of driver
        Double averageDriverRating = ratingRepository.findByDriver(driver)
                .stream()
                .mapToDouble(rating1 -> rating1.getDriverRating())
                .average().orElse(0.0);

        // set the average driver rating in driver object
        driver.setRating(averageDriverRating);

        // save the updated driver in the repository
        Driver savedDriver = driverRepository.save(driver);

        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public RiderDto rateRider(Ride ride, Integer rating) {
        // ge the rider from ride
        Rider rider = ride.getRider();

        // get the rating for the particular ride
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for this ride id "+ride.getId()));

        // if rider is already rated
        if (ratingObj.getRiderRating() != null)
            throw new RuntimeConflictException("Rider is already rated, cannot rate again");

        // set rider rating in ratingObj
        ratingObj.setRiderRating(rating);

        // save the rating in repository
        ratingRepository.save(ratingObj);

        // calculate average rating of rider
        Double averageRiderRating = ratingRepository.findByRider(rider)
                .stream()
                .mapToDouble(rating1 -> rating1.getRiderRating())
                .average().orElse(0.0);

        // set the average rider rating to the rider
        rider.setRating(averageRiderRating);

        // save the updated rider in the repository
        Rider savedRider = riderRepository.save(rider);

        // return rider dto
        return modelMapper.map(savedRider, RiderDto.class);
    }

    @Override
    public void cresateNewRating(Ride ride) {
        // create a new rating
        Rating rating = Rating.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .build();

        // save the rating to repository
        ratingRepository.save(rating);
    }
}
