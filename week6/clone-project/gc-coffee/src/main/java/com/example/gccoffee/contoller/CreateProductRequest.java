package com.example.gccoffee.contoller;

import com.example.gccoffee.model.Category;

public record CreateProductRequest(String productName, Category category, long price, String description) {

}
