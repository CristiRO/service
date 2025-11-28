package ro.unibuc.hello.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.CreateUserRequest;
import ro.unibuc.hello.exception.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(String id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    public UserEntity createUser(CreateUserRequest request) {
        UserEntity user = new UserEntity(
            UUID.randomUUID().toString(),
            request.name(),
            request.email()
        );
        return userRepository.save(user);
    }

    public UserEntity changeName(String id, String newName) throws EntityNotFoundException {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        UserEntity updated = new UserEntity(id, newName, existing.email());
        return userRepository.save(updated);
    }

    public void deleteUser(String id) throws EntityNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public UserEntity getUserByEmail(String email) throws EntityNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException(email);
        }
        return user;
    }
}
