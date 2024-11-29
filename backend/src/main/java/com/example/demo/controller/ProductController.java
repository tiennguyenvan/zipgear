package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
// import com.example.demo.service.Lib;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// import jakarta.persistence.criteria.CriteriaBuilder;
// import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
// import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:9999")
@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/products")
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
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	// Create a New Product
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

    private final Path uploadDir = Paths.get("uploads");


	@PostMapping("/products")
    public ResponseEntity<?> createProduct(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,	
			@RequestParam("stock") Integer stock,		
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
			@RequestHeader("X-User-Email") String email,
			@RequestHeader("X-User-Code") String code
			) {

        // Validate user and admin permissions
        User user = Lib.getRequestingUser(email, code, userRepository);
        if (user == null) {
            return Lib.userRestResponseErr;
        }
        if (!Lib.isUserAdmin()) {
            return Lib.RestUnauthorized("No permission to create products.");
        }

        // Check if category exists
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            return Lib.RestBadRequest("Category not found.");
        }

        // Save product details
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(categoryOptional.get());
		product.setStock(stock);
        product = productRepository.save(product);

        // debug uploaded images        
		// System.out.println("Upload directory: " + uploadDir.toAbsolutePath());
		// if (images == null || images.isEmpty()) {
		// 	System.out.println("No images received");
		// 	return Lib.RestBadRequest("At least one feaature image is required!");
		// } else {
		// 	for (MultipartFile image : images) {
		// 		System.out.println("Image received: " + image.getOriginalFilename());
		// 		String filename = product.getProductId() + "_" + image.getOriginalFilename();
		// 		System.out.println("Saving file: " + filename);
		// 	}
		// }
					
		try {
			List<String> imageUrls = saveUploadedImages(images, product.getProductId());
			product.setImageSrcs(imageUrls);			
		} catch (RuntimeException e) {
			return Lib.RestBadRequest("Error uploading images: " + e.getMessage());
		}
        
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,
			@RequestParam("stock") Integer stock,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "removedImages", required = false) List<String> removedImages,
			@RequestHeader("X-User-Email") String email,
			@RequestHeader("X-User-Code") String code
			) {

        // Validate user and admin permissions
        User user = Lib.getRequestingUser(email, code, userRepository);
        if (user == null) {
            return Lib.userRestResponseErr;
        }
        if (!Lib.isUserAdmin()) {
            return Lib.RestUnauthorized("No permission to update products.");
        }

        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            return Lib.RestNotFound("Product not found.");
        }

        // Update product details
        Product product = productOptional.get();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);	
		product.setStock(stock);	

        // Update category
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            return Lib.RestBadRequest("Category not found.");
        }
        product.setCategory(categoryOptional.get());

        // Remove deleted images
        if (removedImages != null && !removedImages.isEmpty()) {
			for (String imageUrl : removedImages) {
				// System.out.println("Image : " +imageUrl);
				deleteFile(imageUrl); // Delete from the file system
				product.getImageSrcs().remove(imageUrl); // Remove from product
			}
		}
		// Handle new uploaded images
		if (images != null && !images.isEmpty()) {
			try {
				List<String> newImageUrls = saveUploadedImages(images, product.getProductId());
				product.getImageSrcs().addAll(newImageUrls);
			} catch (RuntimeException e) {
				return Lib.RestBadRequest("Error uploading images: " + e.getMessage());
			}
		}

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id, @RequestHeader("X-User-Email") String email,
	@RequestHeader("X-User-Code") String code) {
        User user = Lib.getRequestingUser(email, code, userRepository);
        if (user == null) {
            return Lib.userRestResponseErr;
        }
        if (!Lib.isUserAdmin()) {
            return Lib.RestUnauthorized("No permission to delete products.");
        }

        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            return Lib.RestNotFound("Product not found.");
        }

        // Delete associated files
        Product product = productOptional.get();
        

		// Delete associated image files
		if (product.getImageSrcs() != null) {
			for (String imageUrl : product.getImageSrcs()) {
				try {
					deleteFile(imageUrl); // Call deleteFile method
					// System.out.println("Deleted image file: " + imageUrl);
				} catch (Exception e) {
					// System.err.println("Failed to delete image file: " + imageUrl + ". Error: " + e.getMessage());
				}
			}
		}
	

        productRepository.delete(product);
        return Lib.RestOk("Product deleted successfully.");
    }

    private List<String> saveUploadedImages(List<MultipartFile> images, Long productId) {
		List<String> savedImageUrls = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			try {
				if (!Files.exists(uploadDir)) {
					Files.createDirectories(uploadDir); // Ensure the directory exists
					// System.out.println("Created directory: " + uploadDir.toAbsolutePath());
				}
	
				for (MultipartFile image : images) {
					String originalFilename = image.getOriginalFilename();
					if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif)$")) {
						throw new IllegalArgumentException("Unsupported file format: " + originalFilename);
					}
	
					String filename = productId + "_" + originalFilename;
					Path filePath = uploadDir.resolve(filename);
					Files.write(filePath, image.getBytes());
					savedImageUrls.add("uploads/" + filename); // Save as a relative path
					// System.out.println("Saved file: " + filePath.toAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to save images.", e);
			}
		} else {
			// System.out.println("No images to save");
		}
		return savedImageUrls;
	}
	

    private void deleteFile(String filePath) {
		// System.out.println("Deleting /" + filePath + "/");
		if (filePath.startsWith("http") || filePath.contains("zipgear-demo-image")) {
			return;
		}

		try {
			// Ensure the path is correctly resolved
			Path fullPath = filePath.startsWith("uploads/") 
					? uploadDir.resolve(filePath.substring("uploads/".length())) // Remove duplicate "uploads/"
					: uploadDir.resolve(filePath);
	
			if (Files.exists(fullPath)) {
				Files.delete(fullPath); // Delete the file
				// System.out.println("File successfully deleted: " + fullPath.toAbsolutePath());
			} else {
				// System.out.println("File not found, skipping deletion: " + fullPath.toAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to delete file: " + filePath, e);
		}
	}		
}
