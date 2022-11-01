# Electronic store checkout system

## Command to start the app
`mvn spring-boot:run`

## Command to run the test
`mvn test`

## Endpoints
Product
```
# Query all products
GET /api/v1/product
# Create a new product
POST /api/v1/product
# Remove a product
DELETE /api/v1/product/{productId}
```

Discount
```
# Query all discounts
GET /api/v1/discount
# Create a new discount
POST /api/v1/discount
# Remove a discount
DELETE /api/v1/discount/{discountId}
```

Basket
```
# Query all products in a basket
GET /api/v1/{customerId}/basket
# Add products to a basket
POST /api/v1/{customerId}/basket
# Remove products from a basket
DELETE /api/v1/{customerId}/basket
# Calculate the receipt of the basket
GET /api/v1/{customerId}/basket/receipt
```

## Sample API calls

```
curl -X GET localhost:8080/api/v1/product
curl -X POST localhost:8080/api/v1/product -H 'Content-type:application/json' -d '{"name": "Orange", "price": "147.0"}'
curl -X DELETE localhost:8080/api/v1/product/3e1d2195-7bf5-42ed-99b5-ff69d853b7c5
curl -X POST localhost:8080/api/v1/discount -H 'Content-type:application/json' -d '{"discountType": "PER_PRODUCT_PCT_OFF", "productId":"7ae0acb4-19f7-4918-b377-454e78b07a3c", perNumOfItems": 2, "pctOfOriginal": 0.7}'
curl -X GET localhost:8080/api/v1/john/basket
curl -X POST localhost:8080/api/v1/john/basket -H 'Content-type:application/json' -d '["7ae0acb4-19f7-4918-b377-454e78b07a3c"]'
curl -X DELETE localhost:8080/api/v1/john/basket -H 'Content-type:application/json' -d '["7ae0acb4-19f7-4918-b377-454e78b07a3c"]'
curl -X GET localhost:8080/api/v1/john/basket/receipt
```


## Assumptions
Due to time constraints, authentication is not yet implemented.

