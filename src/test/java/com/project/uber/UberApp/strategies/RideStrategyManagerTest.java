package com.project.uber.UberApp.strategies;

import org.mockito.MockedStatic;
import java.time.LocalTime;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.entities.Rider;
import com.project.uber.UberApp.strategies.Impl.DriverMatchingHighestRatedStrategy;
import com.project.uber.UberApp.strategies.Impl.DriverMatchingNearestDriverStrategy;
import com.project.uber.UberApp.strategies.Impl.RideFareDefaultFareCalculationStrategy;
import com.project.uber.UberApp.strategies.Impl.RideFareSurgePricingFareCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RideStrategyManagerTest {

    @Mock
    private DriverMatchingHighestRatedStrategy highestRatedStrategy;

    @Mock
    private DriverMatchingNearestDriverStrategy nearestDriverStrategy;

    @Mock
    private RideFareSurgePricingFareCalculationStrategy surgePricingStrategy;

    @Mock
    private RideFareDefaultFareCalculationStrategy defaultFareStrategy;

    @InjectMocks
    private RideStrategyManager rideStrategyManager;

    private RideRequest rideRequest;
    private Rider rider;

    @BeforeEach
    void setUp() {
        rideRequest = new RideRequest();
        rider = new Rider();
        rideRequest.setRider(rider);
    }

    @Test
    void testDriverMatchingStrategy_HighestRated() {
        // Arrange: Rider has a very high rating (e.g., 4.9)
        rider.setRating(4.9);

        // Act
        DriverMatchingStrategy strategy = rideStrategyManager.driverMatchingStrategy(rider.getRating());

        // Assert: Should pick the Highest Rated strategy
        assertThat(strategy).isInstanceOf(DriverMatchingHighestRatedStrategy.class);
    }

    @Test
    void testDriverMatchingStrategy_NearestDriver() {
        // Arrange: Rider has an average/low rating (e.g., 4.5)
        rider.setRating(4.5);

        // Act
        DriverMatchingStrategy strategy = rideStrategyManager.driverMatchingStrategy(rider.getRating());

        // Assert: Should pick the Nearest Driver strategy
        assertThat(strategy).isInstanceOf(DriverMatchingNearestDriverStrategy.class);
    }

    @Test
    void testFareCalculationStrategy_SurgePricing() {
        // Arrange: "Freeze" time at 7:00 PM (19:00) - which is SURGE TIME
        try (MockedStatic<LocalTime> mockedTime = mockStatic(LocalTime.class, CALLS_REAL_METHODS)) {
            LocalTime peakTime = LocalTime.of(19, 0);
            mockedTime.when(LocalTime::now).thenReturn(peakTime);

            // Act: Call the method with 0 arguments
            RideFareCalculationStrategy strategy = rideStrategyManager.rideFareCalculationStrategy();

            // Assert: Should pick Surge Pricing strategy
            assertThat(strategy).isInstanceOf(RideFareSurgePricingFareCalculationStrategy.class);
        }
    }

    @Test
    void testFareCalculationStrategy_DefaultPricing() {
        // Arrange: "Freeze" time at 2:00 PM (14:00) - which is NORMAL TIME
        try (MockedStatic<LocalTime> mockedTime = mockStatic(LocalTime.class, CALLS_REAL_METHODS)) {
            LocalTime normalTime = LocalTime.of(14, 0);
            mockedTime.when(LocalTime::now).thenReturn(normalTime);

            // Act: Call the method with 0 arguments
            RideFareCalculationStrategy strategy = rideStrategyManager.rideFareCalculationStrategy();

            // Assert: Should pick Default Pricing strategy
            assertThat(strategy).isInstanceOf(RideFareDefaultFareCalculationStrategy.class);
        }
    }
}