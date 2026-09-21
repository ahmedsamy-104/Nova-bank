package com.Novabank.Customer.Controller;


import com.Novabank.Customer.Dto.RegistrationRequestDto;
import com.Novabank.Customer.Dto.RegistrationResponse;
import com.Novabank.Customer.Service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    @PostMapping("/register")
    public  ResponseEntity<RegistrationResponse> register
            (
                    @Valid
                    @RequestBody
                    RegistrationRequestDto request
            )
    {

        RegistrationResponse response=registrationService.register(request);

                URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/../customers/{id}")
                        .buildAndExpand(response.getCustomerId()).toUri();
                return ResponseEntity.created(location).body(response);

    }
}
