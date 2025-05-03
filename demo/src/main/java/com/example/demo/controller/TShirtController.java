package com.example.demo.controller;

import com.example.demo.model.TShirt;
import com.example.demo.repository.TShirtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tshirts")
public class TShirtController {

    @Autowired
    private TShirtRepository tShirtRepository;

    // Create a new TShirt
    @PostMapping
    public TShirt addTShirt(@RequestBody TShirt tShirt) {
        return tShirtRepository.save(tShirt);
    }

    // Get all TShirts
    @GetMapping
    public List<TShirt> getAllTShirts() {
        return tShirtRepository.findAll();
    }
}
