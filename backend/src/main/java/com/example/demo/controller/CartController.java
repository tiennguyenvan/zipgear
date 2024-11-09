package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Get cart by user ID
    @GetMapping("/carts/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Cart> cart = cartRepository.findByUser(user.get());
            return cart.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    // Create or retrieve a cart for a user
    @PostMapping("/carts/user/{userId}")
    public ResponseEntity<Cart> createOrGetCart(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Cart cart = cartRepository.findByUser(user.get())
                    .orElseGet(() -> cartRepository.save(new Cart(user.get())));
            return ResponseEntity.ok(cart);
        }
        return ResponseEntity.notFound().build();
    }

    // Add item to cart with stock check and quantity validation
    @PostMapping("/carts/{cartId}/items")
    public ResponseEntity<String> addItemToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam Integer quantity) {
        // Validate quantity is positive
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be positive.");
        }

        Optional<Cart> cart = cartRepository.findById(cartId);
        Optional<Product> product = productRepository.findById(productId);

        if (cart.isPresent() && product.isPresent()) {
            Product foundProduct = product.get();
            
            // Check stock availability
            if (quantity > foundProduct.getStock()) {
                return ResponseEntity.badRequest().body("Insufficient stock. Available: " + foundProduct.getStock());
            }

            // Check if the item already exists in the cart
            Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart.get(), foundProduct);
            if (existingCartItem.isPresent()) {
                // Update the existing cart item
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
                return ResponseEntity.ok("Item quantity updated.");
            } else {
                // Create new cart item
                CartItem cartItem = new CartItem(cart.get(), foundProduct, quantity);
                cartItemRepository.save(cartItem);
                return ResponseEntity.ok("Item added to cart.");
            }
        }
        return ResponseEntity.badRequest().body("Cart or product not found.");
    }

    // Update item quantity in cart with stock check and quantity validation
    @PutMapping("/carts/{cartId}/items/{itemId}")
    public ResponseEntity<String> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long itemId, @RequestParam Integer quantity) {
        // Validate quantity is positive
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be positive.");
        }

        Optional<CartItem> cartItem = cartItemRepository.findById(itemId);
        if (cartItem.isPresent() && cartItem.get().getCart().getCartId().equals(cartId)) {
            Product product = cartItem.get().getProduct();
            
            // Check stock availability
            if (quantity > product.getStock()) {
                return ResponseEntity.badRequest().body("Insufficient stock. Available: " + product.getStock());
            }

            // Update the item quantity
            cartItem.get().setQuantity(quantity);
            cartItemRepository.save(cartItem.get());
            return ResponseEntity.ok("Item quantity updated.");
        }
        return ResponseEntity.notFound().build();
    }

    // Delete item from cart
    @DeleteMapping("/carts/{cartId}/items/{itemId}")
    public ResponseEntity<Void> deleteItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        Optional<CartItem> cartItem = cartItemRepository.findById(itemId);
        if (cartItem.isPresent() && cartItem.get().getCart().getCartId().equals(cartId)) {
            cartItemRepository.delete(cartItem.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
