package com.Novabank.Customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "roles")


public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Roleid;
    @Setter

    @Column(name = "name",nullable = false, unique = true, length = 20)
    private String name;
}