package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
	List<Product> findByNameContainingIgnoreCase(String name, Sort sort);
    List<Product> findAll(Sort sort);
}
