package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.CreateUserRequest;
import ro.unibuc.hello.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<UserEntity> users = Arrays.asList(
            new UserEntity("1", "Alice", "alice@example.com"),
            new UserEntity("2", "Bob", "bob@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserEntity> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).name());
        assertEquals("Bob", result.get(1).name());
    }

    @Test
    void testGetUserById_ExistingUser() throws EntityNotFoundException {
        // Arrange
        UserEntity user = new UserEntity("1", "Alice", "alice@example.com");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.getUserById("1");

        // Assert
        assertNotNull(result);
        assertEquals("Alice", result.name());
        assertEquals("alice@example.com", result.email());
    }

    @Test
    void testGetUserById_NonExistingUser() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("999"));
    }

    @Test
    void testCreateUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Alice", result.name());
        assertEquals("alice@example.com", result.email());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testChangeName_ExistingUser() throws EntityNotFoundException {
        // Arrange
        UserEntity existing = new UserEntity("1", "Alice", "alice@example.com");
        when(userRepository.findById("1")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity result = userService.changeName("1", "Alicia");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("Alicia", result.name());
        assertEquals("alice@example.com", result.email());
    }

    @Test
    void testChangeName_NonExistingUser() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.changeName("999", "NewName"));
    }

    @Test
    void testDeleteUser_ExistingUser() throws EntityNotFoundException {
        // Arrange
        when(userRepository.existsById("1")).thenReturn(true);

        // Act
        userService.deleteUser("1");

        // Assert
        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteUser_NonExistingUser() {
        // Arrange
        when(userRepository.existsById("999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser("999"));
    }
}
