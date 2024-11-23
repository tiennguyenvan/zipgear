package com.example.demo.repository;

import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Find all ratings by User
    List<Rating> findByUser(User user);    
	List<Rating> findByProduct(Product product);
    List<Rating> findByUserAndProduct(User user, Product product);

    // Delete all ratings for a specific user
    @Transactional
    void deleteByUser(User user);

    // Delete a specific rating by User and Product
    @Transactional
    void deleteByUserAndProduct(User user, Product product);
}
