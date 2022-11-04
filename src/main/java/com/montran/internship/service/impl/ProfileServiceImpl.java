package com.montran.internship.service.impl;

import com.montran.internship.model.Profile;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.repository.ProfileRepository;
import com.montran.internship.security.services.UserDetailsImpl;
import com.montran.internship.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Long getLoggedUserId() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @Override
    public Profile getLoggedProfile() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return profileRepository.findById(user.getId()).orElse(null);
    }

    @Override
    public MessageResponse updateProfilePassword(Profile profile, String oldPassword, String newPassword) {

        String message = "", status = "";
        if (encoder.matches(oldPassword, profile.getPassword())) {
            profile.setPassword(encoder.encode(newPassword));
            profileRepository.save(profile);

            message = "Password has been changed.";
            status = "200";
        } else {
            message = "Old password is not correct.";
            status = "400";
        }
        return new MessageResponse(message, status);
    }
}
