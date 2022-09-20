package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostProductReq {
    private String name;
    private int userId;
    private int price;
    private int categoryId;
    private String shippingFeeIncluded;
    private String location;
    private int amount;
    private String used;
    private String safePayment;
    private String exchangePayment;
    private String contents;
    private List<String> tags;
}