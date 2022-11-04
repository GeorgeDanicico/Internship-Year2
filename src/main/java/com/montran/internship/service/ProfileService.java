package com.montran.internship.service;

import com.montran.internship.model.Profile;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.repository.ProfileRepository;
import com.montran.internship.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface ProfileService {
    Long getLoggedUserId();
    Profile getLoggedProfile();
    MessageResponse updateProfilePassword(Profile profile, String oldPassword, String newPassword);
}
