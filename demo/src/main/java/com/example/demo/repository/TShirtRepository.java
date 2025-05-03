package com.example.demo.repository;

import com.example.demo.model.TShirt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TShirtRepository extends JpaRepository<TShirt, Long> {
    // Custom queries can go here if needed
}
