package com.project.uber.UberApp.repository;

import com.project.uber.UberApp.TestContainerConfig;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Force it to use Testcontainers, not H2
class UserRepositoryTest extends TestContainerConfig {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@test.com");
        user.setPassword("secret");
        user.setRoles(Set.of(Role.RIDER));
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("alice@test.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Alice");
    }

    @Test
    void testFindByEmail_WhenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("ghost@test.com");

        // Assert
        assertThat(foundUser).isNotPresent();
    }
}