package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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
     * 상점 정보 조회 API
     * [GET] /stores/{store_id}
     * @return BaseResponse<GetStoreInfoRes>
     */
    @ResponseBody
    @GetMapping("/{store_id}")
    public BaseResponse<GetStoreInfoRes> getStoreInfo(@PathVariable("store_id") int storeId) throws BaseException{

        GetStoreInfoRes result = storeProvider.getStoreInfo(storeId);

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

    /**
     * 상점 팔로우 API
     * [POST] /stores/{store-id}/followed
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{store-id}/followed")
    public BaseResponse<String> modifyFollowing(@PathVariable("store-id") int followId) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        // 스스로 팔로우 불가
        if (userId == followId){
            return new BaseResponse<>(INVALID_ACCESS);
        }

        String result = storeService.modifyFollowing(userId, followId);

        return new BaseResponse<>(result);
    }


    @ResponseBody
    @GetMapping("/{store-id}/followers")
    public BaseResponse<List<GetFollowRes>> getFollowers(@PathVariable("store-id") int storeId,
                                                         @RequestParam(value = "id", defaultValue = "0") int lastId) throws BaseException {
        List<GetFollowRes> followers = storeProvider.getFollowers(storeId, lastId);

        return new BaseResponse<>(followers);
    }

    @ResponseBody
    @GetMapping("/{store-id}/followings")
    public BaseResponse<List<GetFollowRes>> getFollowings(@PathVariable("store-id") int storeId,
                                                          @RequestParam(value = "id", defaultValue = "0") int lastId) throws BaseException {
        List<GetFollowRes> followings = storeProvider.getFollowings(storeId, lastId);

        return new BaseResponse<>(followings);

    }

}
