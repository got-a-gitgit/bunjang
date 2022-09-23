package com.example.demo.src.store.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowRes {
    private int userId;
    private String profileImageUrl;
    private String storeName;
    private String alarm_flag;
    private int follower;
    private int product;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ProductInfo> productInfoList;

    public GetFollowRes(int userId, String profileImageUrl, String storeName, String alarm_flag, int follower, int product) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.storeName = storeName;
        this.alarm_flag = alarm_flag;
        this.follower = follower;
        this.product = product;
        this.productInfoList = null;
    }
}
