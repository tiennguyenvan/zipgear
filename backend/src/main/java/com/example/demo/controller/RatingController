package com.example.demo.controller;

import com.zipgear.entity.Rating;
import com.zipgear.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getRatingsByProduct(@PathVariable Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable Long userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return ResponseEntity.ok(ratings);
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        Rating savedRating = ratingRepository.save(rating);
        return ResponseEntity.ok(savedRating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long id, @RequestBody Rating ratingDetails) {
        Optional<Rating> optionalRating = ratingRepository.findById(id);
        if (optionalRating.isPresent()) {
            Rating rating = optionalRating.get();
            rating.setRatingStars(ratingDetails.getRatingStars());
            rating.setRatingDescription(ratingDetails.getRatingDescription());
            ratingRepository.save(rating);
            return ResponseEntity.ok(rating);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        if (ratingRepository.existsById(id)) {
            ratingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
