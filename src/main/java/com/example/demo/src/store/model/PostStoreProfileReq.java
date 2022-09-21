package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
public class PostStoreProfileReq {

    private MultipartFile profileImageFile;
    private String profileImageUrl;

    @Valid
    private StoreProfile storeProfile;

}
