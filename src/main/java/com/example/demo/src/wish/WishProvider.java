package com.example.demo.src.wish;


import com.example.demo.utils.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class WishProvider {

    private final WishDao wishDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WishProvider(WishDao wishDao, JwtService jwtService) {
        this.wishDao = wishDao;
        this.jwtService = jwtService;
    }


}
