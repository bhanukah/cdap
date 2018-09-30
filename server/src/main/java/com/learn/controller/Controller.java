package com.learn.controller;

import com.learn.model.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.learn.model.BotMessage;
import com.learn.service.BotService;

@RestController
@RequestMapping("/bot")
public class Controller {

    @Autowired
    private BotService botService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public BotMessage createComment(@RequestBody UserMessage userMessage) {

        return botService.createComment(userMessage);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public String listComment() {
        return botService.listComments();
    }
}
