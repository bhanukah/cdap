package com.learn.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.learn.model.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learn.model.BotMessage;
import com.learn.repository.BotMessageRepository;
import com.learn.service.BotService;

@Service
public class BotServiceImpl implements BotService {

    @Autowired
    private BotMessageRepository botMessageRepository;

    public BotMessage createComment(UserMessage userMessage) {
        System.out.print("User Message - id: "+userMessage.getId()+" message: "+userMessage.getMessage());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new BotMessage("1231231212", "Hiiiiii");
        //return botMessageRepository.save(userMessage);
    }

    public List<BotMessage> listComments() {

        return botMessageRepository.findAll();
    }

}
