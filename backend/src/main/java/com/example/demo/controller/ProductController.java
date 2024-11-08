package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import jakarta.persistence.criteria.CriteriaBuilder;
// import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
// import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/")
	public ResponseEntity<List<Product>> getAllProducts(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String priceOrder, // "asc" or "desc"
			@RequestParam(required = false) String ratingOrder, // "asc" or "desc"
			@RequestParam(required = false) BigDecimal minPrice, // minimum price filter
			@RequestParam(required = false) BigDecimal maxPrice, // maximum price filter
			@RequestParam(required = false) Double minRating, // minimum rating filter
			@RequestParam(required = false) Double maxRating, // maximum rating filter
			@RequestParam(required = false) Boolean inStock, // true for in-stock, false for out-of-stock
			@RequestParam(required = false) Long categoryId // category filter
	) {
		// Initialize sorting based on price or rating
		Sort sort = Sort.unsorted();

		if ("asc".equalsIgnoreCase(priceOrder)) {
			sort = Sort.by("price").ascending();
		} else if ("desc".equalsIgnoreCase(priceOrder)) {
			sort = Sort.by("price").descending();
		} else if ("asc".equalsIgnoreCase(ratingOrder)) {
			sort = Sort.by("averageRating").ascending();
		} else if ("desc".equalsIgnoreCase(ratingOrder)) {
			sort = Sort.by("averageRating").descending();
		}

		// Fetch products with applied filters and sorting
		List<Product> products = productRepository.findAll((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Keyword filter
			if (keyword != null && !keyword.isEmpty()) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
			}

			// Price range filter
			if (minPrice != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
			}

			// Rating range filter
			if (minRating != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), minRating));
			}
			if (maxRating != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("averageRating"), maxRating));
			}

			// Stock filter
			if (inStock != null) {
				if (inStock) {
					predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0)); // in-stock if stock > 0
				} else {
					predicates.add(criteriaBuilder.equal(root.get("stock"), 0)); // out-of-stock if stock == 0
				}
			}

			// Category filter
			if (categoryId != null) {
				predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId));
			}

			// Combine all predicates
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		}, sort);

		return ResponseEntity.ok(products);
	}

	// Get Product by ID
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	// Create a New Product
	@PostMapping("/")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		Product savedProduct = productRepository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
	}

	// Update Product by ID
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
		Optional<Product> productOptional = productRepository.findById(id);

		if (productOptional.isPresent()) {
			Product existingProduct = productOptional.get();
			existingProduct.setTitle(productDetails.getTitle());
			existingProduct.setDescription(productDetails.getDescription());
			existingProduct.setPrice(productDetails.getPrice());
			existingProduct.setImageSrcs(productDetails.getImageSrcs());
			existingProduct.setAverageRating(productDetails.getAverageRating());

			// Set the category by using the provided Category object
			existingProduct.setCategory(productDetails.getCategory());

			existingProduct.setStock(productDetails.getStock());

			// The updatedAt field will be set automatically with @PreUpdate
			Product updatedProduct = productRepository.save(existingProduct);
			return ResponseEntity.ok(updatedProduct);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// Delete All Products
	@DeleteMapping("/")
	public ResponseEntity<String> deleteAllProducts() {
		productRepository.deleteAll();
		return ResponseEntity.ok("All products deleted successfully.");
	}

	// Delete Product by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return ResponseEntity.ok("Product deleted successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
	}

}
