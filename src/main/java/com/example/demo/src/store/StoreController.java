package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.PatchStoreProfileRes;
import com.example.demo.src.store.model.PostStoreNameReq;
import com.example.demo.src.store.model.PatchStoreProfileReq;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreProvider storeProvider;
    private final StoreService storeService;
    private final JwtService jwtService;

    @Autowired
    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    /**
     * 상점명 설정 API
     * [POST] /stores
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> registerStoreName(@RequestBody @Valid PostStoreNameReq store) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        String result = storeService.registerStoreName(userId, store.getName());

        return new BaseResponse<>(result);
    }

    /**
     * 상점 소개 수정 API
     * [POST] /stores
     * @return BaseResponse<PatchStoreProfileReq>
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<PatchStoreProfileRes> modifyStoreProfile(@ModelAttribute @Valid PatchStoreProfileReq storeProfile) throws BaseException{
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        PatchStoreProfileRes result = storeService.modifyStoreProfile(userId, storeProfile);

        return new BaseResponse<>(result);

    }

}
