package com.learn.service;

import java.util.List;

import com.learn.model.BotMessage;
import com.learn.model.UserMessage;

public interface BotService {

    BotMessage createComment(UserMessage userMessage);
    
    List<BotMessage> listComments();
}
