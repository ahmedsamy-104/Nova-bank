package com.Novabank.Customer.repository;
import com.Novabank.Customer.entity.customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<customer, Long>
{
 public boolean existsByEmailIgnoreCase(String email);
    public boolean existsByMobileNumberIgnoreCase(String MobileNumber);
}
