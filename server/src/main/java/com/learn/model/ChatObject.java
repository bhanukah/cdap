package com.learn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ChatObject {

    public ChatObject(String id, String context, String approved, int step) {
        this.id = id;
        this.context = context;
        this.approved = approved;
        this.setPrestep(step);
        this.setCurrType("PRE");//PRE, INFO, EXT
        this.setExtstep(0);
        this.setInfostep(0);
        setPreRemain(0);
        setInfoRemain(0);
        setExtRemain(0);
        getDir = "false";
    }

    public ChatObject() {
        this.setPrestep(0);
        this.approved = "true";
        this.setCurrType("PRE");//PRE, INFO, EXT
        this.setExtstep(0);
        this.setInfostep(0);
        setPreRemain(0);
        setInfoRemain(0);
        setExtRemain(0);
        getDir = "false";
    }

    @Id
    private String id;

    private String context;

    private String approved;

    private String currType;

    private  int prestep;

    private  int extstep;

    private  int infostep;

    private int preRemain;

    private int extRemain;

    private int infoRemain;

    public String getDir;

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

    public String getCurrType() {
        return currType;
    }

    public void setCurrType(String currType) {
        this.currType = currType;
    }

    public int getPrestep() {
        return prestep;
    }

    public void setPrestep(int prestep) {
        this.prestep = prestep;
    }

    public int getExtstep() {
        return extstep;
    }

    public void setExtstep(int extstep) {
        this.extstep = extstep;
    }

    public int getInfostep() {
        return infostep;
    }

    public void setInfostep(int infostep) {
        this.infostep = infostep;
    }

    public int getPreRemain() {
        return preRemain;
    }

    public void setPreRemain(int preRemain) {
        this.preRemain = preRemain;
    }

    public int getExtRemain() {
        return extRemain;
    }

    public void setExtRemain(int extRemain) {
        this.extRemain = extRemain;
    }

    public int getInfoRemain() {
        return infoRemain;
    }

    public void setInfoRemain(int infoRemain) {
        this.infoRemain = infoRemain;
    }
}
