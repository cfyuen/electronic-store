package com.bullish.store.discount.scheme;

import java.util.UUID;

public abstract class AbstractDiscount implements IDiscount {

    private UUID uuid;

    public AbstractDiscount() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

}