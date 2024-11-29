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
import com.example.demo.service.Env;

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
			OrderRepository orderRepository) {
		return args -> {
			// Clear existing data for a fresh start
			productRepository.deleteAll();
			categoryRepository.deleteAll();
			userRepository.deleteAll();
			cartRepository.deleteAll();
			ratingRepository.deleteAll();
			orderRepository.deleteAll();

			User adminUser = new User();
			adminUser.setEmail(Env.ADMIN_EMAIL);
			userRepository.save(adminUser);

			User mockUser1 = new User();
			mockUser1.setEmail("chau20nm@gmail.com");			
			User mockUser2 = new User();
			mockUser2.setEmail("eddiechae93@gmail.com");
			User mockUser3 = new User();
			mockUser3.setEmail("tmhoang2906@gmail.com");
			mockUser3.setAddresses(Arrays.asList("700 Royal Ave, New Westminster, BC"));

			// Save the user to the repository
			userRepository.save(mockUser3);

			

			// Category: Laptop
			Category laptopCategory = new Category();
			laptopCategory.setName("Laptop");

			// create products
			Product macbookPro = new Product();
			Product dellXps = new Product();
			Product msSurface = new Product();
			Product iphone16 = new Product();
			Product samsungGalaxy = new Product();
			Product googlePixel = new Product();			
			Product sonyHeadphone = new Product();
			Product AirPodsPro = new Product();
			Product AirPodsMax = new Product();
			
			

			// provide product details			
			macbookPro.setTitle("MacBook Pro M4");
			macbookPro.setDescription("The MacBook Pro M4 offers a balance of performance and portability. Equipped with Apple's M4 chip, it delivers impressive speed and efficiency. The 14.3-inch Liquid Retina display provides vibrant visuals, and its lightweight design makes it ideal for on-the-go use. With a battery life exceeding 25 hours, it's suitable for both professionals and students.");
			macbookPro.setPrice(new BigDecimal("4399"));
			macbookPro.setImageSrcs(List.of("uploads/zipgear-demo-image-macbook-m4-pro-01.png", "uploads/zipgear-demo-image-macbook-m4-pro-02.png"));
			macbookPro.setStock(10);
			macbookPro.setAverageRating(5.0);
			macbookPro.addRating(mockUser3, 5, "Excellent headphone, very rich and reliable.");

			
			dellXps.setTitle("Dell XPS 13");
			dellXps.setDescription("The Dell XPS 13 is renowned for its sleek design and robust performance. The 2024 model features the Snapdragon X Elite chip, offering enhanced speed and efficiency. Its InfinityEdge display provides an immersive viewing experience, and the compact form factor ensures portability. This laptop is a strong contender for users seeking a premium Windows experience.");
			dellXps.setPrice(new BigDecimal("1299"));
			dellXps.setImageSrcs(List.of("uploads/zipgear-demo-image-dell-xps-13-01.png", "uploads/zipgear-demo-image-dell-xps-13-02.png"));
			dellXps.setStock(15);
			// laptop2.setAverageRating(4.0);

			
			msSurface.setTitle("MS Surface Laptop");
			msSurface.setDescription("Microsoft's Surface Laptop combines elegant design with powerful performance. The 2024 edition, powered by the Qualcomm Snapdragon X Elite chip, offers a seamless Windows 11 experience. Available in 14-inch and 15-inch models, it boasts a high-resolution display and impressive battery life, making it suitable for both work and entertainment. ");
			msSurface.setPrice(new BigDecimal("1399"));
			msSurface.setImageSrcs(List.of("uploads/zipgear-demo-image-ms-surface-01.png", "uploads/zipgear-demo-image-ms-surface-02.png"));
			msSurface.setStock(5);
			// laptop3.setAverageRating(4.8);

			

			// Category: Mobile
			Category mobileCategory = new Category();
			mobileCategory.setName("Mobile");
			
			iphone16.setTitle("iPhone 16 Pro");
			iphone16.setDescription("Apple's flagship device features a 6.9-inch Super Retina XDR OLED display, the A18 Pro chipset, and a versatile triple-camera system. It introduces the Camera Control button and integrates Apple Intelligence for enhanced AI capabilities.");
			iphone16.setPrice(new BigDecimal("1199"));
			iphone16.setImageSrcs(List.of("uploads/zipgear-demo-image-iphone-16-pro-01.png", "uploads/zipgear-demo-image-iphone-16-pro-02.png"));
			iphone16.setStock(25);
			// mobile1.setAverageRating(4.3);
			
			samsungGalaxy.setTitle("Samsung Galaxy S24");
			samsungGalaxy.setDescription("Samsung's premium offering boasts a 6.8-inch Dynamic AMOLED 2X display, the Exynos 2400 or Snapdragon 8 Gen 3 processor (depending on the region), and a quad-camera setup with a 200MP main sensor. It features a titanium frame and offers up to seven years of OS updates.");
			samsungGalaxy.setPrice(new BigDecimal("1299.99"));
			samsungGalaxy.setImageSrcs(List.of("uploads/zipgear-demo-image-samsung-galaxy-s24-01.png", "uploads/zipgear-demo-image-samsung-galaxy-s24-02.png"));
			samsungGalaxy.setStock(30);
			// mobile2.setAverageRating(4.7);

			
			googlePixel.setTitle("Google Pixel 9");
			googlePixel.setDescription("Google's latest flagship comes with a 6.8-inch Super Actua LTPO display, the Tensor G4 chipset, and a triple-camera system optimized for AI-enhanced photography. It offers seven years of OS and security updates");
			googlePixel.setPrice(new BigDecimal("1099"));
			googlePixel.setImageSrcs(List.of("uploads/zipgear-demo-image-google-pixel-9-01.png", "uploads/zipgear-demo-image-google-pixel-9-02.png"));
			googlePixel.setStock(20);
			// mobile3.setAverageRating(4.6);

			
			// Category: Headphone
			Category headphoneCategory = new Category();
			headphoneCategory.setName("Headphone");

			
			sonyHeadphone.setTitle("Sony WH-1000XM5");
			sonyHeadphone.setDescription("These over-ear headphones are renowned for their exceptional sound quality and industry-leading noise cancellation. They offer up to 30 hours of battery life and feature intuitive touch controls. Priced at approximately");
			sonyHeadphone.setPrice(new BigDecimal("400"));
			sonyHeadphone.setImageSrcs(
					List.of("uploads/zipgear-demo-image-sony-wh-1000xm5-01.png", "uploads/zipgear-demo-image-sony-wh-1000xm5-02.png"));
			sonyHeadphone.setStock(50);
			// headphone1.setAverageRating(4.2);

			
			AirPodsPro.setTitle("Apple AirPods Pro");
			AirPodsPro.setDescription("These in-ear earphones provide excellent sound quality, active noise cancellation, and seamless integration with Apple devices. They come with a MagSafe charging case and offer up to 6 hours of listening time on a single charge");
			AirPodsPro.setPrice(new BigDecimal("249.99"));
			AirPodsPro.setImageSrcs(
				List.of("uploads/zipgear-demo-image-apple-airpods-pro-01.png", "uploads/zipgear-demo-image-apple-airpods-pro-02.png"));
			AirPodsPro.setStock(40);
			// headphone2.setAverageRating(4.4);

			
			AirPodsMax.setTitle("Apple AirPods Max");
			AirPodsMax.setDescription("The Apple AirPods Max are premium over-ear headphones known for their high-fidelity audio, active noise cancellation, and seamless integration with Apple devices. They feature a custom acoustic design, including a 40mm dynamic driver for rich sound across various frequencies. The headphones offer up to 20 hours of listening time with active noise cancellation or transparency mode enabled");
			AirPodsMax.setPrice(new BigDecimal("549"));
			AirPodsMax.setImageSrcs(
					List.of("uploads/zipgear-demo-image-apple-airpods-max-01.png", "uploads/zipgear-demo-image-apple-airpods-max-02.png"));
			AirPodsMax.setStock(35);
			// headphone3.setAverageRating(5.0);
			

			// Add products to the category
			laptopCategory.addProduct(macbookPro);
			categoryRepository.save(laptopCategory);										
			headphoneCategory.addProduct(AirPodsMax);
			headphoneCategory.addProduct(AirPodsPro);
			categoryRepository.save(headphoneCategory);
			mobileCategory.addProduct(iphone16);
			mobileCategory.addProduct(samsungGalaxy);
			mobileCategory.addProduct(googlePixel);			
			categoryRepository.save(mobileCategory);
			headphoneCategory.addProduct(sonyHeadphone);						
			categoryRepository.save(headphoneCategory);
			laptopCategory.addProduct(dellXps);
			laptopCategory.addProduct(msSurface);		
			categoryRepository.save(laptopCategory);
			
			// Save each category individually
			// categoryRepository.save(laptopCategory);
			// categoryRepository.save(mobileCategory);
			// categoryRepository.save(headphoneCategory);

			Cart cart = new Cart(mockUser3);
			CartItem cartItem1 = new CartItem(cart, macbookPro, 2); // Adding 2 units of Laptop 1
			cart.getItems().add(cartItem1);
			cartRepository.save(cart);

			// Checkout - Create an Order from the first cart
			BigDecimal totalPrice1 = cart.getItems().stream()
					.map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			// String productListJson1 = "[{\"title\": \"Laptop 1\", \"quantity\": 2,
			// \"price\": 799.99}]";
			// Order order1 = new Order(mockUser, productListJson1, "123 Mock St, Mock City,
			// Mock Country", totalPrice1);

			Order order1 = new Order();
			order1.setUser(mockUser3);
			order1.setProductList(cart.getItems());
			order1.setDeliveryAddress(mockUser3.getAddresses().get(0));
			order1.setTotalPrice(totalPrice1);

			order1.setOrderStatus(OrderStatus.DELIVERED);
			orderRepository.save(order1);

			// Second Cart with other products
			cart.getItems().clear();
			CartItem cartItem2 = new CartItem(cart, iphone16, 1); // Adding 1 unit of Mobile 1
			cart.getItems().add(cartItem2);
			// Save the second cart with items
			cartRepository.save(cart);

			;

		};
	}
}
