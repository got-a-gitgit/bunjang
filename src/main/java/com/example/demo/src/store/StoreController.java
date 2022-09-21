package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.PostStoreNameReq;
import com.example.demo.src.store.model.PostStoreProfileReq;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.demo.config.BaseResponseStatus.INSERT_SUCCESS;

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

        storeService.registerStoreName(userId, store.getName());

        return new BaseResponse<>(INSERT_SUCCESS);
    }

    @ResponseBody
    @PutMapping("")
    public String modifyStoreProfile(@ModelAttribute PostStoreProfileReq storeProfile) throws BaseException{
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        return "";

    }

}
