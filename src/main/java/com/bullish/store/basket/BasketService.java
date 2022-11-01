package com.bullish.store.basket;

import com.bullish.store.discount.DiscountApplied;
import com.bullish.store.discount.DiscountRepository;
import com.bullish.store.discount.scheme.IDiscount;
import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BasketService {

    private static Logger logger = LogManager.getLogger(BasketService.class);

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    @Autowired
    public BasketService(BasketRepository basketRepository, ProductRepository productRepository, DiscountRepository discountRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public Basket getBasketById(String customerId) {
        return this.basketRepository.findById(customerId);
    }

    public Basket addProductsToBasket(String customerId, List<UUID> productIds) {
        List<Product> products = getProductListFromId(productIds);
        return this.basketRepository.addProductsToBasket(customerId, products);
    }

    public Basket removeProductsFromBasket(String customerId, List<UUID> productIds) {
        List<Product> products = getProductListFromId(productIds);
        return this.basketRepository.removeProductsFromBasket(customerId, products);
    }

    private List<Product> getProductListFromId(List<UUID> productIds) {
        return productIds.stream().map(
            id -> this.productRepository.findById(id)
        ).toList();
    }

    public BasketReceipt getReceiptById(String customerId) {
        // As a GET call, we don't want to modify the underlying basket
        Basket basket = getBasketById(customerId);
        Basket activeBasket = basket.filterActiveProducts();

        Double totalPrice = activeBasket.calculatePreDiscountPrice();
        List<DiscountApplied> discountsApplied = new LinkedList<>();

        List<IDiscount> discounts = this.discountRepository.findAll();
        ArrayList<IDiscount> sortedDiscounts = new ArrayList<>(discounts);
        // Sort the discount list according to apply order
        sortedDiscounts.sort(Comparator.comparing(IDiscount::getDiscountType));

        for (IDiscount discount : sortedDiscounts) {
            DiscountApplied discountApplied = discount.calculateDiscount(productRepository, activeBasket, discountsApplied);
            if (discountApplied.getReduction() > 0.0) {
                totalPrice -= discountApplied.getReduction();
                discountsApplied.add(discountApplied);
            }
        }
        return new BasketReceipt(activeBasket, discountsApplied, totalPrice);
    }
}
