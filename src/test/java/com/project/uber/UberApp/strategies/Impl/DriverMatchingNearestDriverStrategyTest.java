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
class DriverMatchingNearestDriverStrategyTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverMatchingNearestDriverStrategy nearestDriverStrategy;

    @Test
    void testFindMatchingDriver_Success() {
        // Arrange
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point pickup = geometryFactory.createPoint(new Coordinate(77.0, 28.0));

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPickupLocation(pickup);

        Driver mockDriver1 = new Driver();
        mockDriver1.setId(1L);
        Driver mockDriver2 = new Driver();
        mockDriver2.setId(2L);

        List<Driver> expectedDrivers = List.of(mockDriver1, mockDriver2);

        when(driverRepository.findTenNearestDrivers(pickup)).thenReturn(expectedDrivers);

        // Act
        List<Driver> matchedDrivers = nearestDriverStrategy.findMatchingDriver(rideRequest);

        // Assert
        assertThat(matchedDrivers).hasSize(2);
        assertThat(matchedDrivers).containsExactly(mockDriver1, mockDriver2);

        // Verify that it specifically called the Nearest algorithm, not the Highest Rated algorithm
        verify(driverRepository).findTenNearestDrivers(pickup);
    }
}