package com.project.uber.UberApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uber.UberApp.dto.*;
import com.project.uber.UberApp.security.JwtService;
import com.project.uber.UberApp.services.RiderService;
import com.project.uber.UberApp.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiderController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypasses security for unit testing endpoints
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RiderService riderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void testRequestRide() throws Exception {
        // 1. Create valid Pickup Point
        PointDto pickup = new PointDto();
        pickup.setCoordinates(new double[]{77.0, 28.0});
        pickup.setType("Point");

        // 2. Create valid Drop-off Point
        PointDto dropOff = new PointDto();
        dropOff.setCoordinates(new double[]{77.1, 28.1});
        dropOff.setType("Point");

        // 3. Attach them to the Request DTO matching your exact variable names
        RideRequestDto requestDto = new RideRequestDto();
        requestDto.setPickUpLocation(pickup);
        requestDto.setDropOffLocation(dropOff);

        RideRequestDto responseDto = new RideRequestDto();

        when(riderService.requestRide(any(RideRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/rider/requestRide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelRide() throws Exception {
        RideDto rideDto = new RideDto();
        when(riderService.cancelRide(1L)).thenReturn(rideDto);

        mockMvc.perform(post("/rider/cancelRide/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testRateDriver() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRideId(1L);
        ratingDto.setRating(5);

        DriverDto driverDto = new DriverDto();
        when(riderService.rateDriver(1L, 5)).thenReturn(driverDto);

        mockMvc.perform(post("/rider/rateDriver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMyProfile() throws Exception {
        RiderDto riderDto = new RiderDto();
        when(riderService.getMyProfile()).thenReturn(riderDto);

        mockMvc.perform(get("/rider/getMyProfile"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMyRides() throws Exception {
        Page<RideDto> pageResponse = new PageImpl<>(List.of(new RideDto()));
        when(riderService.getAllRides(any(PageRequest.class))).thenReturn(pageResponse);

        // Tests default pagination values (pageOffSet=0, pageSize=10)
        mockMvc.perform(get("/rider/getMyRides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
}