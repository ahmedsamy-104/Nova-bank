package com.Novabank.Customer.Service;

import com.Novabank.Customer.Dto.RegistrationRequestDto;
import com.Novabank.Customer.Dto.RegistrationResponse;
import com.Novabank.Customer.entity.Role;
import com.Novabank.Customer.entity.customer;
import com.Novabank.Customer.entity.customerstatus;
import com.Novabank.Customer.repository.CustomerRepository;
import com.Novabank.Customer.repository.RoleRepository;
import com.Novabank.common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RegistrationService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(CustomerRepository customerRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrationResponse register(RegistrationRequestDto request) {

        String email = request.getEmail().trim();
        String mobileNumber = request.getMobileNumber().trim();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new GlobalExceptionHandler.DuplicateResourceException(
                    "Email already registered", "EMAIL_ALREADY_REGISTERED");
        }
        if (customerRepository.existsByMobileNumberIgnoreCase(mobileNumber)) {
            throw new GlobalExceptionHandler.DuplicateResourceException(
                    "Mobile number already registered", "MOBILE_ALREADY_REGISTERED");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());


        Role role = new Role();
        role.setName(Role.customer);
        roleRepository.save(role);

        customer newCustomer = new customer();
        newCustomer.setFirstname(request.getFirstName().trim());
        newCustomer.setLastname(request.getLastName().trim());
        newCustomer.setEmail(email);
        newCustomer.setMobileNumber(mobileNumber);
        newCustomer.setPasswordHash(passwordHash.getBytes(StandardCharsets.UTF_8));
        newCustomer.setStatus(customerstatus.PENDING_VERIFICATION);
        newCustomer.setRole(role);

        customer savedCustomer = customerRepository.save(newCustomer);

        log.info("New customer registered: email={}", maskEmail(savedCustomer.getEmail()));

        return toResponseDto(savedCustomer);
    }

    private RegistrationResponse toResponseDto(customer saved) {
        return RegistrationResponse.builder()
                .customerId(saved.getId())
                .firstName(saved.getFirstname())
                .lastName(saved.getLastname())
                .email(saved.getEmail())

                .status(saved.getStatus())
                .registeredAt(saved.getCreatedAt())
                .build();
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}