package com.learn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ChatObject {

    public ChatObject(String id, String context, String approved, int step) {
        this.id = id;
        this.context = context;
        this.approved = approved;
        this.step = step;
    }

    public ChatObject() {

    }

    @Id
    private String id;

    private String context;

    private String approved;

    private String currType;

    private  int step;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getCurrType() {
        return currType;
    }

    public void setCurrType(String currType) {
        this.currType = currType;
    }
}
