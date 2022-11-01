package com.bullish.store.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<Product> getAllProducts(@RequestParam(defaultValue = "true") Boolean activeOnly) {
        return this.productService.getAllProducts(activeOnly);
    }

    @PostMapping("")
    public Product createNewProduct(@Valid @RequestBody Product product) {
        return this.productService.createNewProduct(product);
    }

    @DeleteMapping("/{productId}")
    public void removeProduct(@PathVariable("productId") UUID productId) {
        this.productService.removeProduct(productId);
    }

}
