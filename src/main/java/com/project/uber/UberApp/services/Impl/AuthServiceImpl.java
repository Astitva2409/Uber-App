package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.DriverDto;
import com.project.uber.UberApp.dto.SignupDto;
import com.project.uber.UberApp.dto.UserDto;
import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.Role;
import com.project.uber.UberApp.exception.ResourceNotFoundException;
import com.project.uber.UberApp.exception.RuntimeConflictException;
import com.project.uber.UberApp.repository.UserRepository;
import com.project.uber.UberApp.security.JwtService;
import com.project.uber.UberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final DriverService driverService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;

    @Override
    public String[] login(String email, String password) {
        String[] tokens = new String[2];

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokens[0] = accessToken;
        tokens[1] = refreshToken;
        return tokens;
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if(user != null)
            throw new RuntimeConflictException("Cannot signup. user already exists");

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        // Trigger the Welcome Email
        String emailBody = "Hi " + savedUser.getName() + ",\n\n" +
                "Welcome to the Uber App! We are thrilled to have you on board.\n\n" +
                "Best Regards,\n" +
                "The Uber App Team";

        // We use a separate thread/asynchronous approach in production, but calling it directly is fine for now
        emailSenderService.sendEmail(savedUser.getEmail(), "Welcome to Uber App!", emailBody);

        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId, String vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "+userId));

        if(user.getRoles().contains(Role.DRIVER)) {
            throw new RuntimeConflictException("User already is a driver with id "+userId);
        }

        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);

        Driver createDriver = Driver.builder()
                .user(user)
                .available(true)
                .rating(0.0)
                .vehicleId(vehicleId)
                .build();

        Driver createdDriver = driverService.createNewDriver(createDriver);
        return modelMapper.map(createdDriver, DriverDto.class);
    }

    @Override
    public String refresh(String refreshToken) {
//        Session session = sessionService.validateSession(refreshToken);
        Long userId = jwtService.getUserIdFromToken(refreshToken);
//        UserResponse userResponse = userService.getUserById(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "+userId));

//        session.setLastUsedAt(LocalDateTime.now());
//        session.setAccessToken(accessToken);
//        sessionRepository.save(session);
        return jwtService.generateAccessToken(user);
    }
}
