package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.DriverDto;
import com.project.uber.UberApp.dto.SignupDto;
import com.project.uber.UberApp.dto.UserDto;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.Role;
import com.project.uber.UberApp.exception.RuntimeConflictException;
import com.project.uber.UberApp.repository.UserRepository;
import com.project.uber.UberApp.services.AuthService;
import com.project.uber.UberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if(user != null)
            throw new RuntimeConflictException("Cannot signup. user already exists");

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        riderService.createNewRider(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
