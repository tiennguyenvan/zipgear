package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	// 1. Get all categories
	@GetMapping("/")
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return ResponseEntity.ok(categories);
	}

	// 2. Get a category by ID
	@GetMapping("/{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
		Optional<Category> category = categoryRepository.findById(id);
		return category.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	// 3. Create a new category
	@PostMapping("/")
	public ResponseEntity<?> createCategory(@RequestBody Category category) {
		// Check if the name is provided
		if (!StringUtils.hasText(category.getName())) {
			return ResponseEntity.badRequest().body(Lib.RestMsg("Name is required."));
		}

		// Check if the category name already exists (optional, if names should be
		// unique)
		if (categoryRepository.existsByName(category.getName())) {
			return ResponseEntity.badRequest().body(Lib.RestMsg("Category with this name already exists."));
		}

		// Create and save the new category		
		category.setUpdatedAt(LocalDateTime.now());
		Category savedCategory = categoryRepository.save(category);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
	}

	// 4. Update a category by ID
	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);

		if (categoryOptional.isPresent()) {
			Category existingCategory = categoryOptional.get();
			existingCategory.setName(categoryDetails.getName());
			existingCategory.setUpdatedAt(LocalDateTime.now());

			Category updatedCategory = categoryRepository.save(existingCategory);
			return ResponseEntity.ok(updatedCategory);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// 5. Delete all categories
	@DeleteMapping("/")
	public ResponseEntity<String> deleteAllCategories() {
		categoryRepository.deleteAll();
		return ResponseEntity.ok("All categories deleted successfully.");
	}

	// 6. Delete a category by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
		if (categoryRepository.existsById(id)) {
			categoryRepository.deleteById(id);
			return ResponseEntity.ok("Category deleted successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
		}
	}
}
