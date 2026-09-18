package com.Novabank.Customer.Service;

import com.Novabank.Customer.Dto.RegistrationRequestDto;
import com.Novabank.Customer.Dto.RegistrationResponse;
import com.Novabank.Customer.entity.customer;
import com.Novabank.Customer.entity.Role;
import com.Novabank.Customer.repository.CustomerRepository;
import com.Novabank.Customer.repository.RoleRepository;
import com.Novabank.common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
            throw new GlobalExceptionHandler.DuplicateResourceException("Email already registered", "EMAIL_ALREADY_REGISTERED");
        }
        if (customerRepository.existsByMobileNumberIgnoreCase(mobileNumber)) {
            throw new GlobalExceptionHandler.DuplicateResourceException("Mobile number already registered", "MOBILE_ALREADY_REGISTERED");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role not seeded in roles table"));

        customer customer = new customer();
        customer.setFirstname(request.getFirstName().trim());
        customer.setLastname(request.getLastName().trim());
        customer.setEmail(email);
        customer.setMobileNumber(mobileNumber);
        customer.setPasswordHash(passwordHash.getBytes());
        customer.setRoles(Set.of(customerRole));

        customer savedCustomer = customerRepository.save(customer);

        log.info("New customer registered: email={}", maskEmail(savedCustomer.getEmail()));

        return toResponseDto(savedCustomer);

    }

    private RegistrationResponse toResponseDto(customer customer) {
        RegistrationResponse response = new RegistrationResponse();
        response.setCustomerId(customer.getId());
        response.setFirstName(customer.getFirstname());
        response.setLastName(customer.getLastname());
        response.setEmail(customer.getEmail());
        response.setStatus(customer.getStatus());
        response.setRegisteredAt(customer.getCreatedAt());
        return response;
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}