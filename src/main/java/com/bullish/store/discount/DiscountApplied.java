package com.bullish.store.discount;

public class DiscountApplied {

    private DiscountType discountType;
    private Double reduction;

    public DiscountApplied(DiscountType discountType, Double reduction) {
        this.discountType = discountType;
        this.reduction = reduction;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public Double getReduction() {
        return reduction;
    }
}
