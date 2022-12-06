package com.example.gccoffee.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private final UUID productId;
    private String productName;
    private Category category;
    private long price;
    private String description;
    private final LocalDateTime createAt;
    private LocalDateTime updateAt;

    public Product(UUID productId, String productName, Category category, long price) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public Product(UUID productId, String productName, Category category, long price, String description, LocalDateTime createAt, LocalDateTime updateAt) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        updateAt = LocalDateTime.now();
    }

    public void setCategory(Category category) {
        this.category = category;
        updateAt = LocalDateTime.now();
    }

    public void setPrice(long price) {
        this.price = price;
        updateAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        updateAt = LocalDateTime.now();
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Category getCategory() {
        return category;
    }

    public long getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }
}
