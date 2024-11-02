package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int ratingStars;

    @Column(length = 500)
    private String ratingDescription;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Rating() {
        this.createdAt = LocalDateTime.now();  // Set createdAt at object creation
    }

    public Rating(Long productId, Long userId, int ratingStars, String ratingDescription) {
        this.productId = productId;
        this.userId = userId;
        this.ratingStars = ratingStars;
        this.ratingDescription = ratingDescription;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(int ratingStars) {
        this.ratingStars = ratingStars;
    }

    public String getRatingDescription() {
        return ratingDescription;
    }

    public void setRatingDescription(String ratingDescription) {
        this.ratingDescription = ratingDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
