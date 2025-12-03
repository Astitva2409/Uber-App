package com.project.uber.UberApp.repository;

import com.project.uber.UberApp.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// ST_DISTANCE(point1, point 2)

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

//    @Query("SELECT d.*, ST_Distance(d.current_location, :pickupLocation) AS distance " +
//            "FROM driver as d WHERE available = true AND ST_DWithin(d.current_location, :pickupLocation, 10000) " +
//            "ORDER BY distance " +
//            "LIMIT 10"
//            )

@Query(value = """
        SELECT d.*, ST_Distance(d.current_location, :pickupLocation) AS distance
        FROM driver d
        WHERE d.available = true 
          AND ST_DWithin(d.current_location, :pickupLocation, 10000)
        ORDER BY ST_Distance(d.current_location, :pickupLocation)
        LIMIT 10
        """,
        nativeQuery = true)
    List<Driver> findTenNearestDrivers(Point pickupLocation);
}
