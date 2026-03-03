package com.project.uber.UberApp.repository;

import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ST_DISTANCE(point1, point 2)

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT d.*, ST_Distance(cast(d.current_location as geography), cast(?1 as geography)) AS distance " +
            "FROM driver d " +
            "WHERE d.available = true AND ST_DWithin(cast(d.current_location as geography), cast(?1 as geography), 10000) " +
            "ORDER BY distance " +
            "LIMIT 10", nativeQuery = true)
    List<Driver> findTenNearestDrivers(Point pickupLocation);

    @Query(value = "SELECT d.*, ST_Distance(cast(d.current_location as geography), cast(?1 as geography)) AS distance " +
            "FROM driver d " +
            "WHERE d.available = true AND ST_DWithin(cast(d.current_location as geography), cast(?1 as geography), 10000) " +
            "ORDER BY d.rating DESC, distance ASC " +
            "LIMIT 10", nativeQuery = true)
    List<Driver> findTenNearbyTopRatedDrivers(Point pickupLocation);

    Optional<Driver> findByUser(User user);
}
