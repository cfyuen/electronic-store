package com.bullish.store.discount.scheme;

import com.bullish.store.basket.Basket;
import com.bullish.store.discount.DiscountApplied;
import com.bullish.store.discount.DiscountType;
import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * For every {perNumOfItems} items, apply {pctOfOriginal}% to the {perNumOfItems}th item.
 */
public class PerProductPctOff extends AbstractDiscount {

    @NotNull
    private UUID productId;
    @NotNull
    @Min(1)
    private Integer perNumOfItems;
    @NotNull
    @Min(0)
    @Max(1)
    private Double pctOfOriginal;

    public PerProductPctOff(UUID productId, Integer perNumOfItems, Double pctOfOriginal) {
        super();
        this.productId = productId;
        this.perNumOfItems = perNumOfItems;
        this.pctOfOriginal = pctOfOriginal;
    }

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.PER_PRODUCT_PCT_OFF;
    }

    @Override
    public Boolean valid(ProductRepository productRepository) {
        if (productRepository.findById(productId) == null) {
            return false;
        }
        return perNumOfItems != null && pctOfOriginal != null;
    }

    @Override
    public DiscountApplied calculateDiscount(ProductRepository productRepository, Basket basket, List<DiscountApplied> discountsApplied) {
        Product product = productRepository.findById(productId);
        Integer qty = basket.getProductQty(product);
        Integer discountableQty = qty / perNumOfItems;
        Double reduction = discountableQty * (1-pctOfOriginal) * product.getPrice();
        return new DiscountApplied(getDiscountType(), reduction);
    }

    @Override
    public String toString() {
        return "PerProductPctOff{" +
            "productId=" + productId +
            ", perNumOfItems=" + perNumOfItems +
            ", pctOfOriginal=" + pctOfOriginal +
            '}';
    }
}
