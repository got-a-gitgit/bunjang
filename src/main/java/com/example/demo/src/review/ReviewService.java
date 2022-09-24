package com.example.demo.src.review;

import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewProvider reviewProvider;
    private final ReviewDao reviewDao;
    private final JwtService jwtService;

    @Autowired
    public ReviewService(ReviewProvider reviewProvider, ReviewDao reviewDao, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }

    public void registerService(int userId, int storeId, PostReviewReq postReviewReq){
        try{
            reviewDao.insertReview(userId, storeId, postReviewReq);
        } catch (Exception e){

        }
    }
}
