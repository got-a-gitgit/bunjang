package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListElement {
    private int productId;
    private int userId;
    private int price;
    private String image;
    private String name;
    private String safePayment;
    private String wish;
}
