package com.ssafy.push;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.push.service.FirebaseCloudMessageDataService;
import com.ssafy.push.service.FirebaseCloudMessageService;

@SpringBootTest
class MobileFirebaseServerApplicationTests {

    @Autowired
    FirebaseCloudMessageService service;

    @Autowired
    FirebaseCloudMessageDataService dataService;

    //기본적으로 data를 보내면 foreground에 있을때만 받을 수 있다. 
    //background에서도 대응하기 위해서는 data에 담아서 전달해야 하는데, 아래 dataMessage로 호출해야 한다.  
    @Test
    void sendMessage() throws IOException {
    	
        String token = "cdwgUJ07SFSx-3Zuqdb4ap:APA91bGeNVTnIWPnuGnQIWPD79vLZQ2l-ZnIZDyQcguDskMQz_v3JIJmMwFf90nk_hn3Qn-0C7l6DTCOpuVW0yEHdRprL9noGx83a_sP22Rb0dEsZRx7yKJWnwIgeol2Uxa0od4lGg5V";
//        String token1 = "cHI9bL4QQTKQI_nJVtoUt7:APA91bF-VKnKY0NxeikBXORc0FhmS3iRHFYZgH5yBF34GNt-tYaUCvABVal1CSibhAEWkj9DDcmpZWlD4lA481JoC_UjVnykSYhyd5OHi3-91E-kVlX4YjEEK2v0fTQlqoW35yjNnlrd";
//		한건 메시지
        service.sendMessageTo(token, "from 사무국", "싸피 여러분 화이팅!!");
        
//		멀티 메시지        
//        service.addToken(token);
//        service.addToken(token1);
        
//        service.broadCastMessage("from 사무국1", "싸피 여러분 화이팅!!!!!!!!");
    }

    
    //Notification아니라 Data에 데이터 담아서 전송함. 
    @Test
    void sendDataMessage() throws IOException {
    	
        String token = "coU1aGJUSkmOJm3RUC9h8p:APA91bFeBRFdJkqEJS7XtKWIUVV6l2EBd9TzsBc39OEtOd_Fmd-pETSBHgOLdrl7XAzVgIzY9VyhGuQ2lax8oZbWsPNs_miTa-5dxwr8oxiJdEiQlyo2t-QRU2x3ullFmHT3-tsGszsN";
//        String token1 = "cHI9bL4QQTKQI_nJVtoUt7:APA91bF-VKnKY0NxeikBXORc0FhmS3iRHFYZgH5yBF34GNt-tYaUCvABVal1CSibhAEWkj9DDcmpZWlD4lA481JoC_UjVnykSYhyd5OHi3-91E-kVlX4YjEEK2v0fTQlqoW35yjNnlrd";

//		한건 메시지
        dataService.sendDataMessageTo(token, "from 사무국", "싸피 여러분 화이팅!!");
        
//		멀티 메시지        
//        dataService.addToken(token);
//        service.addToken(token1);
        
//        service.broadCastMessage("from 사무국1", "싸피 여러분 화이팅!!!!!!!!");
    }
}
