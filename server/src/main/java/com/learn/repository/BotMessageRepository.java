package com.learn.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.learn.model.BotMessage;

@Repository
public interface BotMessageRepository extends MongoRepository<BotMessage, String> {

}
