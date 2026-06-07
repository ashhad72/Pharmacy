package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

}