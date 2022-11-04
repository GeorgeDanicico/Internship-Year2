package com.montran.internship.controller;

import com.montran.internship.model.Profile;
import com.montran.internship.model.Role;
import com.montran.internship.model.enums.ERole;
import com.montran.internship.payload.request.ChangePasswordRequest;
import com.montran.internship.payload.request.SignupRequest;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.repository.ProfileRepository;
import com.montran.internship.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/change_password")
    public ResponseEntity<?> registerUser(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Profile profile = profileService.getLoggedProfile();

        MessageResponse messageResponse = profileService.updateProfilePassword(profile,
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());

        HttpStatus status = messageResponse.getStatus().equals("200") ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(messageResponse, status);
    }
}
