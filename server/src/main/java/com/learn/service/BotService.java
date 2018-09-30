package com.learn.service;

import com.learn.model.BotMessage;
import com.learn.model.UserMessage;

public interface BotService {

    BotMessage createComment(UserMessage userMessage);
    
    String listComments();
}
