package com.bullish.store.product;

import javax.validation.constraints.*;
import java.util.Objects;
import java.util.UUID;

public class Product {

    private UUID uuid;
    @NotBlank
    private String name;
    @NotNull
    @Min(0)
    private Double price;
    private Boolean isActive;
    // TODO: A list of associated discounts for cleaning up

    public Product(String name, Double price) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.isActive = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Product{" +
            "uuid=" + uuid +
            ", name='" + name + '\'' +
            ", price=" + price +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return uuid.equals(product.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
