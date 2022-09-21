package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProductReq {
    @NotBlank
    private String name;
    @NotNull
    private int userId;

    private int price;
    @NotNull
    private int categoryId;
    @NotNull
    private String shippingFeeIncluded;
    @NotNull
    private String location;
    @NotNull
    @Min(value=1)
    private int amount;
    @NotNull
    private String used;
    @NotNull
    private String safePayment;
    @NotNull
    private String exchangePayment;
    @NotNull
    private String contents;

    private List<String> tags;
}