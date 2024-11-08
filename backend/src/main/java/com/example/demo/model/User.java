package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email")) // Ensure email is unique
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate UserID
	private Long userId;

	@Column(nullable = false, unique = true) // Email should be unique
	private String email;

	// Embeds the list of addresses in a separate join table
	// we have to do this because the address could have the "," commas
	@ElementCollection 
	@CollectionTable(name = "user_addresses", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "addresses")
	private List<String> addresses;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// Constructors
	public User() {
		// Initialize timestamps when creating a user
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public User(String email, List<String> addresses) {
		this.email = email;
		this.addresses = addresses;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	// Method triggered before an update
	@PreUpdate
	protected void onUpdate() {
		// Update timestamp automatically before saving changes
		this.updatedAt = LocalDateTime.now();
	}
}
