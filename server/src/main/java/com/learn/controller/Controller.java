package com.learn.controller;

import java.util.List;

import com.learn.model.UserMessage;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.iri.impl.Main;
import org.apache.jena.util.FileManager;
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
    public List<BotMessage> listComment() {

        return botService.listComments();
    }
}
