package com.Novabank.Customer.entity;

import com.Novabank.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "roles")


public class Role extends BaseEntity {
    public static final String customer = "ROLE_CUSTOMER";
    public static final String ADMIN    = "ROLE_ADMIN";
    public static final String TELLER   = "ROLE_TELLER";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Roleid;


    @Column(name = "name",nullable = false, length = 20)
    private String name;
}