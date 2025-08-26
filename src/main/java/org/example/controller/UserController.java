package org.example.controller;

import jakarta.validation.Valid;
import org.example.Entity.Users;
import org.example.dto.UserCreateRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // Helper method to convert Entity to DTO
    private UserResponseDto toResponseDto(Users user) {
        return new UserResponseDto(
                user.getId(), // ID is now a String
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatar()
        );
    }

    // CREATE a new User
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto userDto) {
        Users newUser = userService.createUser(userDto);
        return new ResponseEntity<>(toResponseDto(newUser), HttpStatus.CREATED);
    }

    // READ all Users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> userDtos = userService.getAllUsers().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    // READ a single User by ID
    @GetMapping("/{id}")
    // The security expression now correctly compares Strings
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) { // ID is now a String
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(toResponseDto(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE a User
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @Valid @RequestBody UserCreateRequestDto userDetails) { // ID is now a String
        return userService.updateUser(id, userDetails)
                .map(updatedUser -> new ResponseEntity<>(toResponseDto(updatedUser), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE a User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) { // ID is now a String
        boolean deleted = userService.deleteUser(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
