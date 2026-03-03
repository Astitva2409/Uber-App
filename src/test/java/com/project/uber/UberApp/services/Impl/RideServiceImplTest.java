package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.Ride;
import com.project.uber.UberApp.entities.RideRequest;
import com.project.uber.UberApp.entities.enums.RideStatus;
import com.project.uber.UberApp.exception.ResourceNotFoundException;
import com.project.uber.UberApp.repository.RideRepository;
import com.project.uber.UberApp.services.RideRequestService;
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
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideRequestService rideRequestService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride mockRide;
    private RideRequest mockRideRequest;
    private Driver mockDriver;

    @BeforeEach
    void setUp() {
        mockRideRequest = new RideRequest();
        mockRideRequest.setId(1L);
        mockRideRequest.setFare(500.0);

        mockDriver = new Driver();
        mockDriver.setId(1L);

        mockRide = new Ride();
        mockRide.setId(10L);
        mockRide.setRideStatus(RideStatus.CONFIRMED);
    }

    @Test
    void testGetRideById_Success() {
        // Arrange
        when(rideRepository.findById(10L)).thenReturn(Optional.of(mockRide));

        // Act
        Ride result = rideService.getRideById(10L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(rideRepository).findById(10L);
    }

    @Test
    void testGetRideById_ThrowsException_WhenNotFound() {
        // Arrange
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideService.getRideById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ride not found with id 99");
    }

    @Test
    void testCreateNewRide_Success() {
        // Arrange
        when(modelMapper.map(mockRideRequest, Ride.class)).thenReturn(new Ride());
        when(rideRepository.save(any(Ride.class))).thenReturn(mockRide);

        // Act
        Ride result = rideService.createNewRide(mockRideRequest, mockDriver);

        // Assert
        assertThat(result).isNotNull();
        verify(rideRequestService).update(mockRideRequest);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void testUpdateRideStatus_Success() {
        // Arrange
        when(rideRepository.save(any(Ride.class))).thenReturn(mockRide);

        // Act
        Ride result = rideService.updateRideStatus(mockRide, RideStatus.ONGOING);

        // Assert
        assertThat(result.getRideStatus()).isEqualTo(RideStatus.ONGOING);
        verify(rideRepository).save(mockRide);
    }

    @Test
    void testGetAllRidesOfRider_Success() {
        // Arrange
        com.project.uber.UberApp.entities.Rider rider = new com.project.uber.UberApp.entities.Rider();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Ride> ridePage = new PageImpl<>(List.of(mockRide));

        when(rideRepository.findByRider(rider, pageRequest)).thenReturn(ridePage);

        // Act
        Page<Ride> result = rideService.getAllRidesOfRider(rider, pageRequest);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        verify(rideRepository).findByRider(rider, pageRequest);
    }

    @Test
    void testGetAllRidesOfDriver_Success() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Ride> ridePage = new PageImpl<>(List.of(mockRide));

        when(rideRepository.findByDriver(mockDriver, pageRequest)).thenReturn(ridePage);

        // Act
        Page<Ride> result = rideService.getAllRidesOfDriver(mockDriver, pageRequest);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        verify(rideRepository).findByDriver(mockDriver, pageRequest);
    }
}