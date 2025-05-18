## Topics


### Purchase

- Consume from: `<loyaltyCardID>-<shopID>`

### Discount Coupons

- Produce to: `discount_coupons` key: `<loyaltyCard_ID>`

### Cross-Selling Recomendations

- Produce to: `cross_selling_recomendations` key: `<originShopID>`

### Selled Product

- **SelledProductbyCoupon:**
  - Produce to: `selled_product_by_discount_coupon` Key: `discountCoupon_ID`
  ```
  CREATE TABLE SelledProductByCoupon (
    id SERIAL PRIMARY KEY,
    product VARCHAR(255) NOT NULL,
    coupons_used INT NOT NULL,
    discounted_amount DECIMAL(10, 2) NOT NULL
  );
  ```
  - Produce to: `selled_product_by_customerID` Key: `customerID`
  ```
  CREATE TABLE SelledProductByCustomerID (
    id SERIAL PRIMARY KEY,
    customerID VARCHAR(255) NOT NULL,
    product VARCHAR(255) NOT NULL,
    number_of_sales INT NOT NULL,
    total_revenue DECIMAL(10, 2) NOT NULL
  );
  ```
  - Produce to: `selled_product_by_location` Key: `location`
  - Produce to: `selled_product_by_loyaltyCardID` Key: `loyaltyCardID`
  - Produce to: `selled_product_shopID` Key: `shopID`

## Messages JSONs

**Purchase_Event:**

```json
{
  "Purchase_Event": {
    "TimeStamp": "2024-02-09 10:44:07.748",
    "LoyaltyCard_ID": "560987123",
    "Price": "3.21",
    "Product": "Pie",
    "Supplier": "Feathered Friends Pet Haven",
    "Shop": "ArcoCegoLisbon",
    "DiscountCouponID": null
  }
}
```

**LoyaltyCard:**

```json
{
  "LoyaltyCard": {
    "LoyaltyCard_ID": "560987123",
    "CustomerID": "194389210",
    "ShopID": "ArcoCegoLisbon"
  }
}
```

**DiscountCoupon:**

```json
{
  "DiscountCoupon": {
    "Expiration": "2024-02-29 23:59:59.999",
    "LoyaltyCard_ID": "560987123",
    "DiscountType": {
      "Product": "Rice",
      "DiscountPercentage": 10
    }
  }
}
```

**CrossSellingRecomendation:**

```json
{
  "CrossSellingRecomendation": {
    "Expiration": "2024-03-31 23:59:59.999",
    "LoyaltyCard_ID": "560987123",
    "Product": "Beef",
    "OriginShop": "ArcoCegoLisbon",
    "DestinationShop": "PracadeBocage"
  }
}
```

**SelledProductByCustomerID:**

```json
{
  "customerId": "42",
  "totalPurchases": 19,
  "totalSpent": 291.00
}

```