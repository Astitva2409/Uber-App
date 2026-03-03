package com.project.uber.UberApp.repository;

import com.project.uber.UberApp.TestContainerConfig;
import com.project.uber.UberApp.entities.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverRepositoryTest extends TestContainerConfig {

    @Autowired
    private DriverRepository driverRepository;

    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        // SRID 4326 is the standard for GPS coordinates (Longitude, Latitude)
        geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        driverRepository.deleteAll(); // Clean DB before each test
    }

    @Test
    void testFindTenNearestDrivers() {
        // Arrange: Create a search point (Longitude 77.0, Latitude 28.0)
        Point searchLocation = geometryFactory.createPoint(new Coordinate(77.0, 28.0));

        // Create a driver very close to the search point
        Driver closeDriver = new Driver();
        closeDriver.setAvailable(true);
        closeDriver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(77.01, 28.01)));
        driverRepository.save(closeDriver);

        // Create a driver extremely far away (Outside 10,000 meters)
        Driver farDriver = new Driver();
        farDriver.setAvailable(true);
        farDriver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(78.0, 29.0)));
        driverRepository.save(farDriver);

        // Create an unavailable driver nearby
        Driver unavailableDriver = new Driver();
        unavailableDriver.setAvailable(false);
        unavailableDriver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(77.02, 28.02)));
        driverRepository.save(unavailableDriver);

        // Act
        List<Driver> nearestDrivers = driverRepository.findTenNearestDrivers(searchLocation);

        // Assert
        assertThat(nearestDrivers).hasSize(1);
        assertThat(nearestDrivers.get(0).getId()).isEqualTo(closeDriver.getId());
    }

    @Test
    void testFindTenNearbyTopRatedDrivers() {
        Point searchLocation = geometryFactory.createPoint(new Coordinate(77.0, 28.0));

        Driver rated4Driver = new Driver();
        rated4Driver.setAvailable(true);
        rated4Driver.setRating(4.0);
        rated4Driver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(77.01, 28.01)));
        driverRepository.save(rated4Driver);

        Driver rated5Driver = new Driver();
        rated5Driver.setAvailable(true);
        rated5Driver.setRating(5.0);
        rated5Driver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(77.01, 28.01)));
        driverRepository.save(rated5Driver);

        // Act
        List<Driver> topRatedDrivers = driverRepository.findTenNearbyTopRatedDrivers(searchLocation);

        // Assert
        assertThat(topRatedDrivers).hasSize(2);
        // The highest rated driver (5.0) should be returned first in the list
        assertThat(topRatedDrivers.get(0).getRating()).isEqualTo(5.0);
        assertThat(topRatedDrivers.get(1).getRating()).isEqualTo(4.0);
    }
}