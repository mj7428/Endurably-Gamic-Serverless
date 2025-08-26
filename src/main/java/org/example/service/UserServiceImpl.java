package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Entity.Users;
import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;
import org.example.dto.UserCreateRequestDto;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Users createUser(UserCreateRequestDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use.");
        }
        // Using the builder pattern for consistency
        Users newUser = Users.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .avatar(userDto.getAvatar())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role("USER") // Explicitly set default role
                .isEnabled(true)
                .build();

        return userRepository.save(newUser);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication."));

        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(String id) { // ID is now a String
        return userRepository.findById(id);
    }

    public Optional<Users> updateUser(String id, UserCreateRequestDto userDetails) { // ID is now a String
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userDetails.getName());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setAvatar(userDetails.getAvatar());
                    return userRepository.save(existingUser);
                });
    }

    public boolean deleteUser(String userId) { // ID is now a String
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEnabled(false);
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }
}
