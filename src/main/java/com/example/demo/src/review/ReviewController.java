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
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

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
     * 리뷰 등록 API
     * [POST] /reviews/{store-id}
     * @return BaseResponse<PostProductRes>
     */
    @ResponseBody
    @PostMapping("/{store-id}")
    public void registerReview(PostReviewReq postReviewReq) throws BaseException, BindException {

        //jwt 인증
        int userId= jwtService.getUserId();
    }

    /**
     * 리뷰 목록 조회 API
     * [GET] /reviews/{store-id}?id={id}&date={date}&size={size}
     * @return BaseResponse<GetReviewsRes>
     */
    @ResponseBody
    @GetMapping("/{store-id}")
    public BaseResponse<GetReviewsRes> registerReview(@PathVariable(value = "store-id") int storeId,
                                                      @RequestParam(value = "id", defaultValue = "0") int reviewId,
                                                      @RequestParam(value="date") String date,
                                                      @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {
        GetReviewsRes result = reviewProvider.getReviews(storeId, reviewId, date, size);
        return new BaseResponse<>(result);
    }

}
