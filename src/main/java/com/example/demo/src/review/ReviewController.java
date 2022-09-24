package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.demo.config.BaseResponseStatus.INSERT_SUCCESS;
import static com.example.demo.config.BaseResponseStatus.INVALID_REVIEWER;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;


    /**
     * 거래후기 등록 API
     * [POST] /reviews
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> registerReview(@RequestBody @Valid PostReviewReq postReviewReq) throws BaseException {
        // jwt 인증
        int userId= jwtService.getUserId();

        // 거래 후기 작성 권한 확인
        int targetId;
        if (userId == postReviewReq.getSellerId()) {
            targetId = postReviewReq.getBuyerId();
        } else if (userId == postReviewReq.getBuyerId()){
            targetId = postReviewReq.getSellerId();
        } else {
            throw new BaseException(INVALID_REVIEWER);
        }

        reviewService.registerReview(userId, targetId, postReviewReq);

        return new BaseResponse<>(INSERT_SUCCESS);
    }

    /**
     * 거래후기 목록 조회 API
     * [GET] /reviews/{store-id}?id={id}&date={date}&size={size}
     * @return BaseResponse<GetReviewsRes>
     */
    @ResponseBody
    @GetMapping("/{store-id}")
    public BaseResponse<GetReviewsRes> registerReview(@PathVariable("store-id") int storeId,
                                                      @RequestParam(value = "id", defaultValue = "0") int reviewId,
                                                      @RequestParam(value="date") String date,
                                                      @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {
        GetReviewsRes result = reviewProvider.getReviews(storeId, reviewId, date, size);
        return new BaseResponse<>(result);
    }

}
