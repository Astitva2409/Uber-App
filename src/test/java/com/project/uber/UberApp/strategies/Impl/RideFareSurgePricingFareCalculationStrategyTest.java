package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.services.DistanceService;
import com.project.uber.UberApp.strategies.RideFareCalculationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideFareSurgePricingFareCalculationStrategyTest {

    @Mock
    private DistanceService distanceService;

    @InjectMocks
    private RideFareSurgePricingFareCalculationStrategy surgeFareStrategy;

    @Test
    void testCalculateFare_WithSurge_Success() {
        // Arrange
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point pickup = geometryFactory.createPoint(new Coordinate(77.0, 28.0));
        Point dropOff = geometryFactory.createPoint(new Coordinate(77.1, 28.1));

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPickupLocation(pickup);
        rideRequest.setDropOffLocation(dropOff);

        double mockDistance = 15.0; // Let's pretend the distance is 15.0 km
        when(distanceService.calculateDistance(pickup, dropOff)).thenReturn(mockDistance);

        // Act
        double fare = surgeFareStrategy.calculateFare(rideRequest);

        // Assert
        // Surge factor is 2, so the fare should be double the default calculation
        double expectedFare = mockDistance * RideFareCalculationStrategy.RIDE_FARE_MULTIPLIER * 2;
        assertThat(fare).isEqualTo(expectedFare);
    }
}