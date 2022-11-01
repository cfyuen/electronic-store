package com.bullish.store.discount.scheme;

import com.bullish.store.basket.Basket;
import com.bullish.store.discount.DiscountApplied;
import com.bullish.store.discount.DiscountType;
import com.bullish.store.product.ProductRepository;

import java.util.List;
import java.util.UUID;

/**
 * An interface to apply any discount.
 * There are a lot of potential extensions, e.g. mutually exclusive discounts, ordering of discounts etc.
 */
public interface IDiscount {

    /**
     * UUID of the discount.
     */
    UUID getUuid();

    /**
     * Discount type.
     */
    DiscountType getDiscountType();

    /**
     * Validate the discount.
     */
    Boolean valid(ProductRepository productRepository);

    /**
     * Apply this discount to a given basket. Return the price reduction from this discount.
     */
    DiscountApplied calculateDiscount(ProductRepository productRepository, Basket basket, List<DiscountApplied> discountsApplied);
}
