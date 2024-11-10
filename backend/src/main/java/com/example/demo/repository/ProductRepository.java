package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    // Find product by exact title
    Product findByTitle(String title);	
    
    // Find products by partial title match, ignoring case, with sorting
    List<Product> findByTitleContainingIgnoreCase(String title, Sort sort);
}
