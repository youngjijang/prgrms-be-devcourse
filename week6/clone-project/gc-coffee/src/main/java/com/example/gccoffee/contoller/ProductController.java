package com.example.gccoffee.contoller;

import com.example.gccoffee.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller //REST API 를 만든는 controller가 아니라 웹페이지에 접속하기위한 view를 반환해주는 controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productsPage(Model model){
        var products = productService.getAllProducts();
        model.addAttribute("products",products);
        return "product-list";
    }

    @GetMapping("new-product")
    public String newProductPage(Model model){
        return "new-product";
    }

    @PostMapping("/products")
    public String newProduct(CreateProductRequest createProductRequest){
        productService.createProduct(createProductRequest.productName(),createProductRequest.category(),
                createProductRequest.price(),createProductRequest.description());
        return "redirect:/products";
    }
}
