package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverMatchingHighestRatedStrategyTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverMatchingHighestRatedStrategy highestRatedStrategy;

    @Test
    void testFindMatchingDriver_Success() {
        // Arrange
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point pickup = geometryFactory.createPoint(new Coordinate(77.0, 28.0));

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPickupLocation(pickup);

        Driver mockDriver = new Driver();
        mockDriver.setId(10L);
        mockDriver.setRating(4.9);

        List<Driver> expectedDrivers = List.of(mockDriver);

        when(driverRepository.findTenNearbyTopRatedDrivers(pickup)).thenReturn(expectedDrivers);

        // Act
        List<Driver> matchedDrivers = highestRatedStrategy.findMatchingDriver(rideRequest);

        // Assert
        assertThat(matchedDrivers).hasSize(1);
        assertThat(matchedDrivers.get(0).getId()).isEqualTo(10L);

        // Verify that it specifically called the Top Rated algorithm, not the Nearest algorithm
        verify(driverRepository).findTenNearbyTopRatedDrivers(pickup);
    }
}