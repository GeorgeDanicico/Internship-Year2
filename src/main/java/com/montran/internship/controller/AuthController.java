package com.montran.internship.controller;

import com.montran.internship.model.Profile;
import com.montran.internship.model.Role;
import com.montran.internship.model.enums.ERole;
import com.montran.internship.payload.request.LoginRequest;
import com.montran.internship.payload.request.SignupRequest;
import com.montran.internship.payload.response.JwtResponse;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.repository.*;
import com.montran.internship.security.jwt.JwtUtils;
import com.montran.internship.payload.response.*;
import com.montran.internship.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Profile profile = profileRepository.getById(userDetails.getId());

        try {
            UserResponse userResponse = new UserResponse(profile.getId(), profile.getUsername(),
                profile.getEmail(), profile.getFirstName(), profile.getLastName(), profile.getPersonalNumericalCode());
            return ResponseEntity.ok(new JwtResponse(jwt, userResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileRepository.getById(userDetails.getId());

        UserResponse userResponse = new UserResponse(profile.getId(), profile.getUsername(),
                profile.getEmail(), profile.getFirstName(), profile.getLastName(), profile.getPersonalNumericalCode());

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(profileRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken."));
        }

        if(profileRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken."));
        }

        if(profileRepository.existsByPersonalNumericalCode(signupRequest.getPersonalNumericalCode())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid Personal Numerical Code."));
        }

        Profile profile = new Profile(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getPersonalNumericalCode(),
                encoder.encode(signupRequest.getPassword()),
                null
        );
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        profile.setRoles(roles);
        profileRepository.save(profile);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
