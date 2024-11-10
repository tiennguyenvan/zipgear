package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ratings")
@JsonIgnoreProperties({"ratings"}) // Ignore ratings to prevent serialization
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int ratingStars;

    @Column(length = 500)
    private String ratingDescription;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
    }

    public Rating(Product product, User user, int ratingStars, String ratingDescription) {
        this.product = product;
        this.user = user;
        this.ratingStars = ratingStars;
        this.ratingDescription = ratingDescription;
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long id) {
        this.ratingId = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
