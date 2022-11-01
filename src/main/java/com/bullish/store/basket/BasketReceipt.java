package com.bullish.store.basket;

import com.bullish.store.discount.DiscountApplied;

import java.util.List;

public class BasketReceipt {

    private Basket basket;
    private List<DiscountApplied> discountsApplied;
    private Double totalPrice;

    public BasketReceipt(Basket basket, List<DiscountApplied> discountsApplied, Double totalPrice) {
        this.basket = basket;
        this.discountsApplied = discountsApplied;
        this.totalPrice = totalPrice;
    }

    public Basket getBasket() {
        return basket;
    }

    public List<DiscountApplied> getDiscountsApplied() {
        return discountsApplied;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
}
