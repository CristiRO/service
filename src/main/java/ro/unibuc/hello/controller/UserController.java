package ro.unibuc.hello.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.ChangeNameRequest;
import ro.unibuc.hello.dto.CreateUserRequest;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) throws EntityNotFoundException {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable String id,
            @RequestBody ChangeNameRequest request) throws EntityNotFoundException {
        UserEntity user = userService.changeName(id, request.getName());
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserEntity> changeName(
            @PathVariable String id,
            @RequestBody ChangeNameRequest request) throws EntityNotFoundException {
        UserEntity user = userService.changeName(id, request.getName());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) throws EntityNotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserEntity> getUserByEmail(@RequestParam String email)
            throws EntityNotFoundException {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
