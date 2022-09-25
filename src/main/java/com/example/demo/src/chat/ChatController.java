package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.PostSendMessageReq;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@AllArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatProvider chatProvider;
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;

    /**
     * 채팅 시작하기 & 메세지 보내기 API
     * [POST] /chats
     * @return BaseResponse
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse sendMessage(@RequestBody PostSendMessageReq postSendMessageReq) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            chatService.sendMessage(userId, postSendMessageReq);
            return new BaseResponse<>(SUCCESS_WITH_DATA);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 채팅방 나가기 API
     * [DELETE] /chatrooms/:chatroom-id
     * @return BaseResponse<String>
     */
    //Path-variable
    @ResponseBody
    @DeleteMapping("/{chatroom-id}")
    public BaseResponse leaveChatroom(@PathVariable("chatroom-id") int chatroomId) {
        try{
            //jwt에서 id 추출.
            int userId = jwtService.getUserId();

            //채팅방 나가기
            chatService.leaveChatroom(userId, chatroomId);
            return new BaseResponse<>(DELETE_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}