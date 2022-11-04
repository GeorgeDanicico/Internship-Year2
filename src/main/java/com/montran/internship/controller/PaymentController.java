package com.montran.internship.controller;

import com.montran.internship.model.Consent;
import com.montran.internship.model.Payment;
import com.montran.internship.model.Profile;
import com.montran.internship.payload.dto.PaymentAuthDTO;
import com.montran.internship.payload.dto.PaymentDTO;
import com.montran.internship.payload.dto.PaymentsDTO;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.request.PaymentRequest;
import com.montran.internship.service.ConsentService;
import com.montran.internship.service.PaymentService;
import com.montran.internship.service.ProfileService;
import com.montran.internship.service.oauth.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private ConsentService consentService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<?> submitPayment(@Valid @RequestBody PaymentRequest paymentRequest) {

        Profile profile = profileService.getLoggedProfile();
        PaymentAuthDTO paymentDTO = paymentService.submitPayment(profile, paymentRequest);

        if (paymentDTO == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        if (paymentDTO.getStatus() != null && paymentDTO.getStatus().equals("Insufficient funds")) {
            return new ResponseEntity("Insufficient funds", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(paymentDTO,
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/all/{accountId}")
    public ResponseEntity<?> getPaymentsForAccount(@Valid @PathVariable String accountId) {
        Profile profile = profileService.getLoggedProfile();
        List<PaymentDTO> payments = paymentService.getAllPayments(profile, accountId);

        if (payments == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(new PaymentsDTO(payments), HttpStatus.ACCEPTED);
    }

    @PostMapping("/auth/token")
    public ResponseEntity<?> getOauthToken(@Valid @RequestParam String code,
                                           @RequestParam String bankName,
                                           @RequestParam String redirectURI,
                                           @RequestParam String paymentId) {
        Profile profile = profileService.getLoggedProfile();
        Consent consent = consentService.getConsent(profile, bankName);

        TokenDTO tokenDto = authorizationService.paymentAuth(consent, redirectURI, code, false);
        paymentService.updatePaymentStatus(consent, tokenDto.getAccessToken(), paymentId );

        if (tokenDto == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(tokenDto, HttpStatus.ACCEPTED);
    }
}
