package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)	
	@JsonIgnore
    private List<Rating> ratings = new ArrayList<>();

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)	
	private Cart cart;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
	@PreUpdate
	protected void onUpdate() {
		// Update timestamp automatically before saving changes
		this.updatedAt = LocalDateTime.now();
	}

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

}
