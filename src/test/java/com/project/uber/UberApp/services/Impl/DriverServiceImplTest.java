package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.DriverDto;
import com.project.uber.UberApp.dto.RideDto;
import com.project.uber.UberApp.dto.RiderDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.Ride;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.RideRequestStatus;
import com.project.uber.UberApp.entities.enums.RideStatus;
import com.project.uber.UberApp.repository.DriverRepository;
import com.project.uber.UberApp.services.PaymentService;
import com.project.uber.UberApp.services.RatingService;
import com.project.uber.UberApp.services.RideRequestService;
import com.project.uber.UberApp.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock private RideRequestService rideRequestService;
    @Mock private DriverRepository driverRepository;
    @Mock private RideService rideService;
    @Mock private ModelMapper modelMapper;
    @Mock private PaymentService paymentService;
    @Mock private RatingService ratingService;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver mockDriver;
    private User mockUser;
    private Ride mockRide;
    private RideRequest mockRideRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockDriver = new Driver();
        mockDriver.setId(1L);
        mockDriver.setUser(mockUser);
        mockDriver.setAvailable(true);

        mockRideRequest = new RideRequest();
        mockRideRequest.setId(1L);
        mockRideRequest.setRideRequestStatus(RideRequestStatus.PENDING);

        mockRide = new Ride();
        mockRide.setId(100L);
        mockRide.setDriver(mockDriver);
        mockRide.setOtp("1234");

        // Mock the Spring Security Context to simulate a logged-in user
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    // --- acceptRide Tests ---

    @Test
    void testAcceptRide_Success() {
        when(rideRequestService.findRideRequestById(1L)).thenReturn(mockRideRequest);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);
        when(rideService.createNewRide(mockRideRequest, mockDriver)).thenReturn(mockRide);
        when(modelMapper.map(mockRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.acceptRide(1L);

        assertThat(result).isNotNull();
        assertThat(mockDriver.getAvailable()).isFalse(); // Verifies availability was set to false
        verify(rideService).createNewRide(mockRideRequest, mockDriver);
    }

    @Test
    void testAcceptRide_ThrowsException_WhenNotPending() {
        // Fix: Changed to CONFIRMED instead of ACCEPTED
        mockRideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);

        when(rideRequestService.findRideRequestById(1L)).thenReturn(mockRideRequest);

        assertThatThrownBy(() -> driverService.acceptRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RideRequest cannot be accepted");
    }

    @Test
    void testAcceptRide_ThrowsException_WhenDriverUnavailable() {
        mockDriver.setAvailable(false); // Driver is offline/busy

        when(rideRequestService.findRideRequestById(1L)).thenReturn(mockRideRequest);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.acceptRide(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Driver cannot accept ride due to unavailability");
    }

    // --- cancelRide Tests ---

    @Test
    void testCancelRide_Success() {
        mockRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(rideService.updateRideStatus(mockRide, RideStatus.CANCELLED)).thenReturn(mockRide);
        when(driverRepository.save(mockDriver)).thenReturn(mockDriver);
        when(modelMapper.map(mockRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.cancelRide(100L);

        assertThat(result).isNotNull();
        assertThat(mockDriver.getAvailable()).isTrue(); // Driver becomes available again
        verify(rideService).updateRideStatus(mockRide, RideStatus.CANCELLED);
    }

    @Test
    void testCancelRide_ThrowsException_WhenWrongDriver() {
        Driver wrongDriver = new Driver();
        wrongDriver.setId(99L);
        mockRide.setDriver(wrongDriver); // A different driver accepted this ride

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.cancelRide(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("This driver has not started this ride");
    }

    @Test
    void testCancelRide_ThrowsException_WhenRideNotConfirmed() {
        mockRide.setRideStatus(RideStatus.ONGOING); // Cannot cancel an ongoing ride

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.cancelRide(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot cancel ongoing ride");
    }

    // --- startRide Tests ---

    @Test
    void testStartRide_Success() {
        mockRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(rideService.updateRideStatus(mockRide, RideStatus.ONGOING)).thenReturn(mockRide);
        when(modelMapper.map(mockRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.startRide(100L, "1234");

        assertThat(result).isNotNull();
        assertThat(mockRide.getStartedAt()).isNotNull();
        verify(paymentService).createNewPayment(mockRide);
        verify(ratingService).cresateNewRating(mockRide); // Preserving your exact method name "cresateNewRating"
    }

    @Test
    void testStartRide_ThrowsException_WhenInvalidOtp() {
        mockRide.setRideStatus(RideStatus.CONFIRMED);

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.startRide(100L, "9999")) // Wrong OTP
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Otp is not valid");
    }

    // --- endRide Tests ---

    @Test
    void testEndRide_Success() {
        mockRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(rideService.updateRideStatus(mockRide, RideStatus.ENDED)).thenReturn(mockRide);
        when(driverRepository.save(mockDriver)).thenReturn(mockDriver);
        when(modelMapper.map(mockRide, RideDto.class)).thenReturn(new RideDto());

        RideDto result = driverService.endRide(100L);

        assertThat(result).isNotNull();
        assertThat(mockRide.getEndedAt()).isNotNull();
        assertThat(mockDriver.getAvailable()).isTrue();
        verify(paymentService).processPayment(mockRide);
    }

    @Test
    void testEndRide_ThrowsException_WhenNotOngoing() {
        mockRide.setRideStatus(RideStatus.CONFIRMED); // Not ongoing yet

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.endRide(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ride status is not ONGOING hence cannot be ended");
    }

    // --- rateRider Tests ---

    @Test
    void testRateRider_Success() {
        mockRide.setRideStatus(RideStatus.ENDED);
        RiderDto mockRiderDto = new RiderDto();

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(ratingService.rateRider(mockRide, 5)).thenReturn(mockRiderDto);

        RiderDto result = driverService.rateRider(100L, 5);

        assertThat(result).isNotNull();
        verify(ratingService).rateRider(mockRide, 5);
    }

    @Test
    void testRateRider_ThrowsException_WhenRideNotEnded() {
        mockRide.setRideStatus(RideStatus.ONGOING);

        when(rideService.getRideById(100L)).thenReturn(mockRide);
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));

        assertThatThrownBy(() -> driverService.rateRider(100L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ride is still not ended");
    }

    // --- Data Retrieval Tests ---

    @Test
    void testGetMyProfile() {
        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(modelMapper.map(mockDriver, DriverDto.class)).thenReturn(new DriverDto());

        DriverDto result = driverService.getMyProfile();

        assertThat(result).isNotNull();
    }

    @Test
    void testGetAllRides() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Ride> ridePage = new PageImpl<>(List.of(mockRide));

        when(driverRepository.findByUser(mockUser)).thenReturn(Optional.of(mockDriver));
        when(rideService.getAllRidesOfDriver(mockDriver, pageRequest)).thenReturn(ridePage);
        when(modelMapper.map(mockRide, RideDto.class)).thenReturn(new RideDto());

        Page<RideDto> result = driverService.getAllRides(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}