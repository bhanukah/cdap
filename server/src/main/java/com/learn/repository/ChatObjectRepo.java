package com.learn.repository;
import com.learn.model.ChatObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatObjectRepo extends MongoRepository<ChatObject, String>  {
}
