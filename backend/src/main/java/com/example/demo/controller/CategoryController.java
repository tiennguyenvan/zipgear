package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
// @fixme: only admin can use category CRUD
@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	// Get all categories
	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return ResponseEntity.ok(categories);
	}

	// Get a category by ID
	@GetMapping("/categories/{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
		Optional<Category> category = categoryRepository.findById(id);
		return category.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	// Create a new category
	@PostMapping("/categories")
	public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		String name = (String) request.get("name");
		if (name.isEmpty()) {
			return Lib.RestBadRequest("Name is required");
		}

		if (!Lib.isUserAdmin()) {
			return Lib.RestUnauthorized("No permission to create categories");
		}
		
		if (categoryRepository.existsByName(name)) {
			return Lib.RestBadRequest("Category with this name already exists.");
		}
		
		Category category = new Category(name);		
		category.setUpdatedAt(LocalDateTime.now());
		Category savedCategory = categoryRepository.save(category);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);		
	}

	// Update a category by ID
	@PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User user = Lib.getRequestingUser(request, userRepository);
        if (user == null) {
            return Lib.userRestResponseErr;
        }

        String newName = (String) request.get("name");
        if (newName == null || newName.isEmpty()) {
            return Lib.RestBadRequest("Name is required.");
        }

        if (!Lib.isUserAdmin()) {
            return Lib.RestUnauthorized("No permission to update categories.");
        }

        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return Lib.RestNotFound("Category not found.");
        }

        Category existingCategory = categoryOptional.get();
        existingCategory.setName(newName);
        existingCategory.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return ResponseEntity.ok(updatedCategory);
    }

	// Delete a category by ID
	@DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id, @RequestHeader("X-User-Email") String email,
	@RequestHeader("X-User-Code") String code) {
        User user = Lib.getRequestingUser(email, code, userRepository);
        if (user == null) {
            return Lib.userRestResponseErr;
        }

        if (!Lib.isUserAdmin()) {
            return Lib.RestUnauthorized("No permission to delete categories.");
        }

        if (!categoryRepository.existsById(id)) {
            return Lib.RestNotFound("Category not found.");
        }

        categoryRepository.deleteById(id);
        return Lib.RestOk("Category deleted successfully.");
    }
}
