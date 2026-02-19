package ro.unibuc.hello.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.request.CreateUserRequest;
import ro.unibuc.hello.response.UserResponse;
import ro.unibuc.hello.exception.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(String id) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return toResponse(user);
    }

    public UserEntity getUserEntityById(String id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }
        UserEntity user = new UserEntity(
            UUID.randomUUID().toString(),
            request.name(),
            request.email()
        );
        UserEntity saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse changeName(String id, String newName) throws EntityNotFoundException {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        UserEntity updated = new UserEntity(existing.id(), newName, existing.email());
        UserEntity saved = userRepository.save(updated);
        return toResponse(saved);
    }

    public void deleteUser(String id) throws EntityNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse getUserByEmail(String email) throws EntityNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email));
        return toResponse(user);
    }

    public UserEntity getUserEntityByEmail(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email));
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(
            user.id(),
            user.name(),
            user.email()
        );
    }
}
