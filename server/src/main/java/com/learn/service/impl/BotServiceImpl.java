package com.learn.service.impl;

import com.google.gson.Gson;
import com.learn.model.*;
import com.learn.repository.BotMessageRepository;
import com.learn.repository.ChatObjectRepo;
import com.learn.service.BotService;
import org.apache.jena.iri.impl.Main;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;

@Service
public class BotServiceImpl implements BotService {

    private String apikey = "AIzaSyAVQDRXX9IITM9DWv_0PgqbGjb-pUX25AE";
    //private String apikey = "AIzaSyAStMVVXO5z6nI8OcrP-euAqdsgHQrEvfU";

    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private ChatObjectRepo chatObjectRepo;

    @Autowired
    private org.springframework.core.io.ResourceLoader resourceLoader;

    public BotMessage createComment(UserMessage userMessage) {
        System.out.println("User Message - id: "+userMessage.getId()+" message: "+userMessage.getMessage());
        String intent = "";
        String res = "";
        String message;
        boolean isNew = false;
        ChatObject chatObj = chatObjectRepo.findOne(userMessage.getId());

        if (chatObj == null) {
            isNew = true;
            chatObj = new ChatObject();
        }

        try {
            System.out.println("before formatted " + userMessage.getMessage());
            message = nlp2(userMessage.getMessage());
            if(message == null || message == "") {
                message = userMessage.getMessage();
            }
            System.out.println("formatted" + message);
            intent = intentClassifier(message);

            //intent = intentClassifier(userMessage.getMessage());
            System.out.println("intent" + intent);
            res = genOutput(chatObj, intent, userMessage.getMessage());
            //res = googleQ("police%20station", "Dehiwala");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!isNew && chatObj.getContext() == null){
            try {
                chatObjectRepo.delete(chatObj.getId());
            } catch (Exception ex) {
                System.out.println("Delete EX");
            }
        }
        if(chatObj != null){
            chatObj.setId(userMessage.getId());
            chatObjectRepo.save(chatObj);
        }
        System.out.println(res);
        return new BotMessage(userMessage.getId(), res);
        //return botMessageRepository.save(userMessage);
    }

    private String genOutput (ChatObject chatObject, String intent, String mes) {
        String response = "Sorry, I don't understand.";

        if (intent.equals("error") && !chatObject.getDir.equals("true")) {
            return "Sorry, I don't understand.";
        }
        if (intent.equals("greet") && !chatObject.getDir.equals("true")) {
            if (chatObject.getId() != null){
                chatObjectRepo.delete(chatObject.getId());
            }
            return  "Hi, How can I Help you?";
        } else if (intent.equals("goodbye") && !chatObject.getDir.equals("true")) {
            if (chatObject.getId() != null){
                chatObjectRepo.delete(chatObject.getId());
            }
            return  "Bye, have a nice day. :)";
        } else if (chatObject.getId() == null && (intent.equals("affirm") || intent.equals("deny"))){
            return  "Sorry, I don't understand.";
        }

        //first output after intent recognition
        if (chatObject.getContext() == null && !chatObject.getDir.equals("true") && (intent.equals("unclear_epf"))){
            return  "Is it regarding an event of death of a member or completion of age?";
        }
        else if (chatObject.getContext() == null && !chatObject.getDir.equals("true") &&
                (intent.equals("new_nic") || intent.equals("lost_nic") || intent.equals("new_license") || intent.equals("lost_license")
                || intent.equals("epf_death") || intent.equals("epf_age"))) {
            chatObject.setContext(intent);
            chatObject.setApproved("true");
            QueryObject res = gatIntentData(intent);
            String temp = getPreReqMessage(res, chatObject, intent);
            if (temp != null){
                return temp;
            }
            temp = getInfoMessage(res, chatObject, intent);
            if (temp != null){
                return temp;
            }

        }else if (chatObject.getId() != null && chatObject.getContext() != null && (chatObject.getCurrType().equals("PRE") || chatObject.getCurrType().equals("INFO")) &&
                 (intent.equals("affirm") || intent.equals("deny"))) {
            QueryObject res = gatIntentData(chatObject.getContext());

            if (chatObject.getCurrType().equals("PRE") && intent.equals("deny")) {
                chatObject.setApproved("false");
                chatObject = new ChatObject();
                return "You are not eligible.";
            }
            String temp = getPreReqMessage(res, chatObject, intent);
            if (temp != null){
                return temp;
            }

            temp = getInfoMessage(res, chatObject, intent);
            if (temp != null){
                return temp;
            }
        }

        if (chatObject.getId() != null && chatObject.getContext() != null && chatObject.getCurrType().equals("EXT") &&
                (intent.equals("affirm") || intent.equals("deny") || chatObject.getDir.equals("true"))) {
            QueryObject res = gatIntentData(chatObject.getContext());

            String temp = getExtMessage(res, chatObject, intent, mes);
            if (temp != null){
                return temp;
            }
            return null;
        }

        QueryObject res = gatIntentData(chatObject.getContext());
        if (res != null){
            response = getRemainMsg(res, chatObject, intent);
            response = response + getSteps(res) + "\n\n";
            chatObject.setCurrType("EXT");
            response = response + getExtMessage(res, chatObject, intent, mes);
        }

        return response;
    }

    private String getRemainMsg(QueryObject queryObject, ChatObject chatObject, String intent) {
        String ret = "";
        if (chatObject.getPreRemain() != 0){
            chatObject.setPreRemain(0);
        } else if (chatObject.getInfoRemain() != 0) {
            if (intent.equals("affirm")){
                ret = queryObject.info.get(chatObject.getInfoRemain() - 1).affrim + "\n";
            } else if (intent.equals("deny")){
                ret = queryObject.info.get(chatObject.getInfoRemain() - 1).deny + "\n";
            }
            chatObject.setInfoRemain(0);
        }
        return ret;
    }
// generating query
    private QueryObject gatIntentData(String intent){
        QueryObject res = null;
        if (intent == null) {
            return null;
        }
        if (intent.equals("new_nic")) {
            res = getData("NIC", "NewNIC");
        }else if (intent.equals("lost_nic")) {
            res = getData("NIC", "MissingNIC");
        }else if (intent.equals("new_license")) {
            res = getData("DrivingLicense", "NewDrivingLicense");
        }else if (intent.equals("lost_license")) {
            res = getData("DrivingLicense", "MissingDrivingLicense");
        }else if (intent.equals("epf_death")) {
            res = getData("EPF", "Death");
        }else if (intent.equals("epf_age")) {
            res = getData("EPF", "Age");
        }
        return res;
    }
    // generating pre requisist
    private String getPreReqMessage(QueryObject queryObject, ChatObject chatObject, String intent) {
        String res = null;
        if (chatObject.getCurrType().equals("PRE") && queryObject.prereq.size() != chatObject.getPrestep()){
            res = queryObject.prereq.get(chatObject.getPrestep()).instruction;
            chatObject.setPrestep(chatObject.getPrestep() + 1);
        } else if (chatObject.getCurrType().equals("PRE") && queryObject.prereq.size() == chatObject.getPrestep()){
            chatObject.setCurrType("INFO");
        }

        return res;
    }
    // generating info messages
    private String getInfoMessage(QueryObject queryObject, ChatObject chatObject, String intent) {
        String res = null;
        if (chatObject.getCurrType().equals("INFO") && queryObject.info.size() != chatObject.getInfostep()){
            if (chatObject.getInfostep() > 0) {
                if (intent.equals("affirm")){
                    res = queryObject.info.get(chatObject.getInfostep() - 1).affrim + "\n";
                } else if (intent.equals("deny")){
                    res = queryObject.info.get(chatObject.getInfostep()  - 1).deny + "\n";
                }
            } else {
                res = "";
            }
            res = res + queryObject.info.get(chatObject.getInfostep()).question;
            chatObject.setInfoRemain(chatObject.getInfostep() + 1);
            chatObject.setInfostep(chatObject.getInfostep() + 1);
        } else if (chatObject.getCurrType().equals("INFO") && queryObject.info.size() == chatObject.getInfostep()){
            chatObject.setCurrType("STEP");
        }

        return res;
    }
    // generating extra content
    private String getExtMessage(QueryObject queryObject, ChatObject chatObject, String intent, String message) {
        String res = null;
        if (chatObject.getCurrType().equals("EXT") && queryObject.extra.size() != chatObject.getExtstep()){
            if (chatObject.getExtstep() > 0) {
                if (chatObject.getDir.equals("true")){
                    res = googleQ(queryObject.extra.get(chatObject.getExtstep() - 1).answer, message);
                    //res = message;
                    chatObject.getDir = "false";
                }
                else if (intent.equals("affirm")){
                    if (queryObject.extra.get(chatObject.getExtstep() - 1).type.equals("direction")){
                        res = "What is the name of your city?\n";
                        chatObject.getDir = "true";
                    } else {
                        res = queryObject.extra.get(chatObject.getExtstep() - 1).answer + "\n";
                    }
                } else if (intent.equals("deny")){
                    res = "";
                }
            } else {
                res = "";
            }
            if (!chatObject.getDir.equals("true")){
                res = res + queryObject.extra.get(chatObject.getExtstep()).question;
                chatObject.setExtRemain(chatObject.getExtstep() + 1);
                chatObject.setExtstep(chatObject.getExtstep() + 1);
            }

        } else if (chatObject.getExtRemain() != 0){
            if (chatObject.getDir.equals("true")){
                res = googleQ(queryObject.extra.get(chatObject.getExtstep() - 1).answer, message);
                //res = message;
                chatObject.getDir = "false";
            }
            else if (intent.equals("affirm")){
                if (queryObject.extra.get(chatObject.getExtstep() - 1).type.equals("direction")){
                    res = "What is the name of your city?\n";
                    chatObject.getDir = "true";
                } else {
                    res = queryObject.extra.get(chatObject.getExtstep() - 1).answer + "\n";
                }
            } else if (intent.equals("deny")){
                res = "That's all";
            }

            if (chatObject.getDir.equals("false")){
                chatObject.setContext(null);
                chatObject.setInfoRemain(0);
            }
        } else if (chatObject.getCurrType().equals("EXT") && queryObject.extra.size() == chatObject.getExtstep()){
            chatObject.setContext(null);
        }

        return res;
    }

    private String getSteps(QueryObject queryObject) {
        String res = "";
        for (InstObject x: queryObject.steps){
            if (x != null) {
                res += x.name + "\n" + x.instruction + "\n";
            }
        }
        return res;
    }
    // calling text simplification script
    private String nlp2 (String message) throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        String result = "";
        String path  = classLoader.getResource("NLTK_CDAP.py").getPath();
        String command = "cmd /c python "+path.substring(1)+ " \""+message+"\"";
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = bri.readLine()) != null) {
            //System.out.println(line);
            result = line;
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        p.waitFor();
        System.out.println("Done.");

        p.destroy();
        return result;
    }
    // calling classification script
    private String intentClassifier (String message) throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        String result;
        boolean isError = false;
        String path  = classLoader.getResource("predict.py").getPath();
        String command = "cmd /c python -W ignore "+path.substring(1)+ " \""+message+"\"";
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = bri.readLine()) != null) {
            response.append(line);
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
            isError = true;
        }
        bre.close();
        p.waitFor();
        System.out.println("Done.");
        p.destroy();

        if (isError) {
            return "error";
        }
        try {
            JSONObject myResponse = new JSONObject(response.toString());
            JSONObject intent = myResponse.getJSONObject("intent");
            float conf = intent.getFloat("confidence");
            System.out.println("Confidence : " + conf);
            if (conf > 0.4) {
                result = intent.getString("name");
            }else {
                result = "error";
            }
        } catch (Exception e) {
            result = "error";
        }
        return result;
    }
// get google map api output
    private String googleQ (String type, String area) {

        String q = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+type.replace(" ", "%20")
                +"%20"+area.replace(" ", "%20")+"&inputtype=textquery&fields=formatted_address,name,opening_hours,place_id&key="+apikey;
        String ret = "";
        StringBuffer response = new StringBuffer();
        URL obj = null;
        try {
            obj = new URL(q);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject myResponse = new JSONObject(response.toString());

        //return response.toString();
        String satatus = myResponse.getString("status");
        if (satatus.equals("OK")){
            JSONArray resArray= myResponse.getJSONArray("candidates");
            if (resArray.length()>0){
                JSONObject resAddress = resArray.getJSONObject(0);
                return resAddress.getString("name") + "\n" + "https://www.google.com/maps/place/?q=place_id:" + resAddress.getString("place_id");
            } else {
                return "no results found.";
            }
        }

        return myResponse.getString("status");
    }

    public String listComments() {
        //return botMessageRepository.findAll();
        QueryObject res = getData("NIC", "NewNIC");
        return res.steps.get(0).instruction;
    }
// ontology query
    private ResultSet sparqlTest(String queryString) {
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Resource res = resourceLoader.getResource("classpath:newuvgalk.owl");
        Model model = FileManager.get().loadModel(res.getFilename());
        Query query= QueryFactory.create(queryString);
        QueryExecution queryExecution= QueryExecutionFactory.create(query,model);
        ResultSet resultSet=queryExecution.execSelect();
        return resultSet;
    }

// get ontology data
    QueryObject getData(String act1, String act2){
        QueryObject queryObject = null;
        System.out.println("event fires");
        List<BotMessage> list= new java.util.ArrayList<BotMessage>();
        String queryString ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX myPre: <http://www.semanticweb.org/uvgalk/ontologies/2018/8/untitled-ontology-19#>\n" +
                "\n" +
                "SELECT *\n" +
                "WHERE {\n" +
                "  myPre:"+act1+" myPre:"+act2+" ?object\n" +
                "}";
        ResultSet resultSet=sparqlTest(queryString);
        while(resultSet.hasNext()){
            QuerySolution querySolution= resultSet.nextSolution();
            try{
                String qqq = querySolution.get("?object").toString();
                qqq = qqq.replaceAll("\n", "");
                qqq = qqq.replaceAll("\\\\", "");
                JSONObject myResponse = new JSONObject(qqq);
                Gson gson=new Gson();
                queryObject = gson.fromJson(qqq, QueryObject.class);
                qqq= qqq;
            }catch(Exception ex){
                System.out.println(ex);
                continue;
            }
        }
        return queryObject;
    }

}
