package com.example.demo.utils;

import com.example.demo.config.BaseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.FAIL_SEND_CERTCODE;

@Service
@RequiredArgsConstructor
//참고: https://terianp.tistory.com/119
public class MailService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JavaMailSender emailSender;

    // 인증코드 생성
    private final String certCode = createCertCode();

    // 메일 내용 생성
    public MimeMessage createMessage(String receiver) throws Exception {

        MimeMessage message = emailSender.createMimeMessage();

        // 수신 이메일, 메일 제목
        message.addRecipients(Message.RecipientType.TO, receiver);
        message.setSubject("[번개장터] 이메일 인증");

        // 메일 내용
        // html 문법 사용
        String msg = "";
        msg += "<div style='margin:100px;'>";
        msg += "<h1>이메일 인증코드</h1><br>";
        msg += "<p>안녕하세요. 번개장터입니다.</p>";
        msg += "<p>아래의 인증코드를 입력하시면 로그인이 정상적으로 완료됩니다.</p><br>";
        msg += "<div align='center' style='border: 1px solid black; background-color: #EFEFEF;'><br>";
        msg += "<div><b><span style='font-size: 18pt;'>";
        msg += certCode + "</span></b></div><br>";
        msg += "</div>";

        message.setText(msg, "utf-8", "html");

        // 발신 이메일 정보
        message.setFrom(new InternetAddress("projectformj@naver.com", "번개장터"));

        return message;
    }

    // 랜덤 인증 코드 생성
    public String createCertCode(){

        int certCodeLength = 6;

        StringBuffer key = new StringBuffer();
        Random random = new Random(System.currentTimeMillis());

        int range = (int)Math.pow(10, certCodeLength);
        int trim = (int)Math.pow(10, certCodeLength-1);
        int result = random.nextInt(range) + trim;

        if (result > range){
            result = result = trim;
        }

        return String.valueOf(result);
    }

    @Async
    public String sendCodeMail(String receiver) throws BaseException {

        try {
            MimeMessage message = createMessage(receiver);
            emailSender.send(message);  // 메일 발송
        } catch (Exception e){
            logger.error("MailService Error", e);
            throw new BaseException(FAIL_SEND_CERTCODE);
        }

        return certCode;
    }

}
