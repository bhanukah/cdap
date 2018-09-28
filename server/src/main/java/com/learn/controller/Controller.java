package com.learn.controller;

import java.util.List;

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

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.POST)
    public BotMessage createComment(@RequestBody UserMessage userMessage) {

        return botService.createComment(userMessage);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BotMessage> listComment() {

        return botService.listComments();
    }
}
