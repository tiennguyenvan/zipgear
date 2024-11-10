package com.example.demo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.config.OrderStatus;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Category;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.UserRepository;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ApplicationRunner init(
		CategoryRepository categoryRepository, 
		ProductRepository productRepository,
        UserRepository userRepository,
		CartRepository cartRepository,
        RatingRepository ratingRepository,
        OrderRepository orderRepository        
		) {
        return args -> {
            // Clear existing data for a fresh start
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
			cartRepository.deleteAll();
			ratingRepository.deleteAll();
			orderRepository.deleteAll();


            User mockUser = new User();
            mockUser.setEmail("nguyentien.jobs@gmail.com");
            mockUser.setAddresses(Arrays.asList("123 Mock St, Mock City, Mock Country"));

            // Save the user to the repository
            userRepository.save(mockUser);

            // System.out.println("Mock user created: " + mockUser.getEmail());
            // Create categories and products directly, using addProduct to set
            // relationships

            // Category: Laptop
            Category laptopCategory = new Category();
            laptopCategory.setName("Laptop");

            Product laptop1 = new Product();
            laptop1.setTitle("Laptop 1");
            laptop1.setDescription("Description for Laptop 1");
            laptop1.setPrice(new BigDecimal("799.99"));
            laptop1.setImageSrcs(List.of("https://picsum.photos/200?random=1", "https://picsum.photos/200?random=2"));
            laptop1.setStock(10);
            laptop1.setAverageRating(4.5);
			laptop1.addRating(mockUser, 5, "Excellent laptop, very fast and reliable.");			

            Product laptop2 = new Product();
            laptop2.setTitle("Laptop 2");
            laptop2.setDescription("Description for Laptop 2");
            laptop2.setPrice(new BigDecimal("899.99"));
            laptop2.setImageSrcs(List.of("https://picsum.photos/200?random=3", "https://picsum.photos/200?random=4"));
            laptop2.setStock(15);
            laptop2.setAverageRating(4.0);
			

            Product laptop3 = new Product();
            laptop3.setTitle("Laptop 3");
            laptop3.setDescription("Description for Laptop 3");
            laptop3.setPrice(new BigDecimal("999.99"));
            laptop3.setImageSrcs(List.of("https://picsum.photos/200?random=5", "https://picsum.photos/200?random=6"));
            laptop3.setStock(5);
            laptop3.setAverageRating(4.8);
			

            // Add products to the category
            laptopCategory.addProduct(laptop1);
            laptopCategory.addProduct(laptop2);
            laptopCategory.addProduct(laptop3);

            // Category: Mobile
            Category mobileCategory = new Category();
            mobileCategory.setName("Mobile");

            Product mobile1 = new Product();
            mobile1.setTitle("Mobile 1");
            mobile1.setDescription("Description for Mobile 1");
            mobile1.setPrice(new BigDecimal("499.99"));
            mobile1.setImageSrcs(List.of("https://picsum.photos/200?random=7", "https://picsum.photos/200?random=8"));
            mobile1.setStock(25);
            mobile1.setAverageRating(4.3);

            Product mobile2 = new Product();
            mobile2.setTitle("Mobile 2");
            mobile2.setDescription("Description for Mobile 2");
            mobile2.setPrice(new BigDecimal("599.99"));
            mobile2.setImageSrcs(List.of("https://picsum.photos/200?random=9", "https://picsum.photos/200?random=10"));
            mobile2.setStock(30);
            mobile2.setAverageRating(4.7);

            Product mobile3 = new Product();
            mobile3.setTitle("Mobile 3");
            mobile3.setDescription("Description for Mobile 3");
            mobile3.setPrice(new BigDecimal("699.99"));
            mobile3.setImageSrcs(List.of("https://picsum.photos/200?random=11", "https://picsum.photos/200?random=12"));
            mobile3.setStock(20);
            mobile3.setAverageRating(4.6);

            // Add products to the category
            mobileCategory.addProduct(mobile1);
            mobileCategory.addProduct(mobile2);
            mobileCategory.addProduct(mobile3);

            // Category: Headphone
            Category headphoneCategory = new Category();
            headphoneCategory.setName("Headphone");

            Product headphone1 = new Product();
            headphone1.setTitle("Headphone 1");
            headphone1.setDescription("Description for Headphone 1");
            headphone1.setPrice(new BigDecimal("99.99"));
            headphone1.setImageSrcs(
                    List.of("https://picsum.photos/200?random=13", "https://picsum.photos/200?random=14"));
            headphone1.setStock(50);
            headphone1.setAverageRating(4.2);

            Product headphone2 = new Product();
            headphone2.setTitle("Headphone 2");
            headphone2.setDescription("Description for Headphone 2");
            headphone2.setPrice(new BigDecimal("149.99"));
            headphone2.setImageSrcs(
                    List.of("https://picsum.photos/200?random=15", "https://picsum.photos/200?random=16"));
            headphone2.setStock(40);
            headphone2.setAverageRating(4.4);

            Product headphone3 = new Product();
            headphone3.setTitle("Headphone 3");
            headphone3.setDescription("Description for Headphone 3");
            headphone3.setPrice(new BigDecimal("199.99"));
            headphone3.setImageSrcs(
                    List.of("https://picsum.photos/200?random=17", "https://picsum.photos/200?random=18"));
            headphone3.setStock(35);
            headphone3.setAverageRating(4.1);

            // Add products to the category
            headphoneCategory.addProduct(headphone1);
            headphoneCategory.addProduct(headphone2);
            headphoneCategory.addProduct(headphone3);

            // Save each category individually
            categoryRepository.save(laptopCategory);
            categoryRepository.save(mobileCategory);
            categoryRepository.save(headphoneCategory);

			Cart cart = new Cart(mockUser);
            CartItem cartItem1 = new CartItem(cart, laptop1, 2); // Adding 2 units of Laptop 1
            cart.getItems().add(cartItem1);
            cartRepository.save(cart);

			// Checkout - Create an Order from the first cart
            BigDecimal totalPrice1 = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            String productListJson1 = "[{\"title\": \"Laptop 1\", \"quantity\": 2, \"price\": 799.99}]";
            Order order1 = new Order(mockUser, productListJson1, "123 Mock St, Mock City, Mock Country", totalPrice1);
            order1.setOrderStatus(OrderStatus.PROCESSING);
            orderRepository.save(order1);

			// Second Cart with other products            
			cart.getItems().clear();
            CartItem cartItem2 = new CartItem(cart, mobile1, 1); // Adding 1 unit of Mobile 1
            cart.getItems().add(cartItem2);
            // Save the second cart with items
            cartRepository.save(cart);

			;

        };
    }
}
