package com.Novabank.Customer.Dto;

import com.Novabank.Customer.entity.customerstatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RegistrationResponse {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private customerstatus status;
    private LocalDateTime registeredAt;
}