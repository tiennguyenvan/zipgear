package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/get-all")
	public ResponseEntity<List<Product>> getAllProducts(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String priceOrder, // "asc" or "desc"
			@RequestParam(required = false) String ratingOrder // "asc" or "desc"
	) {
		Sort sort = Sort.unsorted();

		// Apply sorting based on price or rating if provided
		if ("asc".equalsIgnoreCase(priceOrder)) {
			sort = Sort.by("price").ascending();
		} else if ("desc".equalsIgnoreCase(priceOrder)) {
			sort = Sort.by("price").descending();
		} else if ("asc".equalsIgnoreCase(ratingOrder)) {
			sort = Sort.by("rating").ascending();
		} else if ("desc".equalsIgnoreCase(ratingOrder)) {
			sort = Sort.by("rating").descending();
		}

		// If keyword is provided, apply keyword filtering, otherwise fetch all products
		// with sorting
		List<Product> products;
		if (keyword != null && !keyword.isEmpty()) {
			products = productRepository.findByNameContainingIgnoreCase(keyword, sort);
		} else {
			products = productRepository.findAll(sort);
		}

		return ResponseEntity.ok(products);
	}

	// Create a new product
	@PostMapping("/create")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		if (product.getName() == null || product.getPrice() == null || product.getCategory() == null
				|| product.getImage() == null) {
			return ResponseEntity.badRequest().body(null);
		}

		Product savedProduct = productRepository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
	}

	// Edit an existing product
	@PutMapping("/edit/{id}")
	public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
		Optional<Product> existingProductOptional = productRepository.findById(id);

		if (existingProductOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}

		Product existingProduct = existingProductOptional.get();

		// Update the product details
		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setCategory(updatedProduct.getCategory());
		existingProduct.setImage(updatedProduct.getImage());

		productRepository.save(existingProduct);
		return ResponseEntity.ok(existingProduct);
	}

	// Delete an existing product
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		Optional<Product> product = productRepository.findById(id);

		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}

		productRepository.deleteById(id);
		return ResponseEntity.ok("Product deleted successfully.");
	}
}
