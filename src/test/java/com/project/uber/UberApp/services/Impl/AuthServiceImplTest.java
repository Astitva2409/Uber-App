package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.dto.SignupDto;
import com.project.uber.UberApp.dto.UserDto;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.Role;
import com.project.uber.UberApp.exception.RuntimeConflictException;
import com.project.uber.UberApp.repository.UserRepository;
import com.project.uber.UberApp.services.EmailSenderService;
import com.project.uber.UberApp.services.RiderService;
import com.project.uber.UberApp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private RiderService riderService;
    @Mock
    private WalletService walletService;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignupDto signupDto;
    private User mappedUser;

    @BeforeEach
    void setUp() {
        signupDto = new SignupDto("John Doe", "john@test.com", "password123");
        mappedUser = new User();
        mappedUser.setName("John Doe");
        mappedUser.setEmail("john@test.com");
        mappedUser.setPassword("password123");
    }

    @Test
    void testSignup_Success() {
        // Arrange
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(Optional.empty());
        when(modelMapper.map(signupDto, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(signupDto.getEmail());
        savedUser.setRoles(Set.of(Role.RIDER));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto userDto = new UserDto();
        userDto.setEmail(signupDto.getEmail());
        when(modelMapper.map(savedUser, UserDto.class)).thenReturn(userDto);

        // Act
        UserDto result = authService.signup(signupDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(signupDto.getEmail());
        verify(emailSenderService, times(1)).sendEmail(eq("john@test.com"), anyString(), anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSignup_ThrowsConflictException_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> authService.signup(signupDto))
                .isInstanceOf(RuntimeConflictException.class)
                .hasMessageContaining("Cannot signup. user already exists");

        verify(userRepository, never()).save(any(User.class));
        verify(emailSenderService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}