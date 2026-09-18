package com.Novabank.common.seed;

import com.Novabank.Customer.entity.Role;
import com.Novabank.Customer.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        List.of("ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_TELLER").forEach(name -> {
            if (!roleRepository.existsByName(name)) {
                Role role = new Role();
                role.setName(name);
                roleRepository.save(role);
            }
        });
    }
}