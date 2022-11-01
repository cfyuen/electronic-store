package com.bullish.store.basket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/{customerId}/basket")
    public Basket getBasketById(@PathVariable("customerId") String customerId) {
        // TODO: authentication
        return this.basketService.getBasketById(customerId);
    }

    @PostMapping("/{customerId}/basket")
    public Basket addProductsToBasket(@PathVariable("customerId") String customerId, @RequestBody List<UUID> productIds) {
        return this.basketService.addProductsToBasket(customerId, productIds);
    }

    @DeleteMapping("/{customerId}/basket")
    public Basket removeProductsFromBasket(@PathVariable("customerId") String customerId, @RequestBody List<UUID> productIds) {
        // Remove is with best effort basis. If we try to remove a non-exist item, the call is successful but with no effect.
        return this.basketService.removeProductsFromBasket(customerId, productIds);
    }

    @GetMapping("/{customerId}/basket/receipt")
    public BasketReceipt getReceiptById(@PathVariable("customerId") String customerId) {
        return this.basketService.getReceiptById(customerId);
    }

}
