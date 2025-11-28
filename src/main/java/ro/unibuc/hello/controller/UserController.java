package ro.unibuc.hello.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.ChangeNameRequest;
import ro.unibuc.hello.dto.CreateUserRequest;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable String id) throws EntityNotFoundException {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserEntity createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/{id}/name")
    public UserEntity changeName(@PathVariable String id, @RequestBody ChangeNameRequest request) throws EntityNotFoundException {
        return userService.changeName(id, request.name());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) throws EntityNotFoundException {
        userService.deleteUser(id);
    }

    @GetMapping("/by-email")
    public UserEntity getUserByEmail(@RequestParam String email) throws EntityNotFoundException {
        return userService.getUserByEmail(email);
    }
}
