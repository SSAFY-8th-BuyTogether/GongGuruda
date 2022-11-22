package com.ssafy.push.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.push.service.FirebaseCloudMessageDataService;
import com.ssafy.push.service.FirebaseCloudMessageService;

@RestController
@CrossOrigin("*")
public class TokenController {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    FirebaseCloudMessageService service;
    
    @Autowired
    FirebaseCloudMessageDataService dataService;
    
    @PostMapping("/token")
    public String registToken(String token) {
    	logger.info("registToken : token:{}", token);
        service.addToken(token);
        return "'"+token+"'" ;
    }
    
    @PostMapping("/broadcast")
    public Integer broadCast(String title, String body) throws IOException {
    	logger.info("broadCast : title:{}, body:{}", title, body);
    	return service.broadCastMessage(title, body);
    }

    @PostMapping("/sendMessageTo")
    public void sendMessageTo(String token, String title, String body) throws IOException {
    	logger.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
        service.sendMessageTo(token, title, body);
    }
    
    @PostMapping("/fcm")
    public Boolean sendFCM(String token, String title, String body) throws IOException {
    	logger.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
    	return dataService.sendDataMessageTo(token, title, body);
    }
}

