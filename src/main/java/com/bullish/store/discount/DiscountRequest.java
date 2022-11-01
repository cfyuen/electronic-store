package com.bullish.store.discount;

import com.bullish.store.discount.scheme.IDiscount;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DiscountRequestDeserializer.class)
public class DiscountRequest {
    private DiscountType discountType;
    private IDiscount discountDetails;

    public DiscountRequest(DiscountType discountType, IDiscount discountDetails) {
        this.discountType = discountType;
        this.discountDetails = discountDetails;
    }

    public IDiscount getDiscountDetails() {
        return discountDetails;
    }
}

