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

        ChatObject chatObj = chatObjectRepo.findOne(userMessage.getId());

        if (chatObj == null) {
            chatObj = new ChatObject();
        }

        try {
            System.out.println("before formatted" + userMessage.getMessage());
            message = nlp2(userMessage.getMessage());
            if(message == null) {
                message = userMessage.getMessage();
            }
            System.out.println("formatted" + message);
            intent = intentClassifier(message);

            //intent = intentClassifier(userMessage.getMessage());
            System.out.println("intent" + intent);
            res = genOutput(chatObj, intent);
            //res = googleQ("police%20station", "Dehiwala");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(chatObj != null){
            chatObj.setId(userMessage.getId());
            chatObjectRepo.save(chatObj);
        }
        System.out.println(res);
        return new BotMessage(userMessage.getId(), res);
        //return botMessageRepository.save(userMessage);
    }

    private String genOutput (ChatObject chatObject, String intent) {
        String response = "Sorry, I don't understand.";

        if (intent.equals("error")) {
            return "Sorry, I don't understand.";
        }
        if (intent.equals("greet")) {
            if (chatObject.getId() != null){
                chatObjectRepo.delete(chatObject.getId());
            }
            return  "Hi, How can I Help you?";
        } else if (intent.equals("goodbye")) {
            if (chatObject.getId() != null){
                chatObjectRepo.delete(chatObject.getId());
            }
            return  "Bye, have a nice day. :)";
        } else if (chatObject.getId() == null && (intent.equals("affirm") || intent.equals("deny"))){
            return  "Sorry, I don't understand.";
        }
        if (chatObject.getContext() == null &&
                (intent.equals("new_nic") || intent.equals("lost_nic") || intent.equals("new_license") || intent.equals("lost_license"))) {
            chatObject.setContext(intent);
            chatObject.setApproved("true");
            QueryObject res = gatIntentData(intent);
            String temp = getNextMessage(res, chatObject);
            if (temp != null){
                return temp;
            }

        }else if (chatObject.getId() != null && chatObject.getContext() != null &&
                 (intent.equals("affirm") || intent.equals("deny"))) {
            QueryObject res = gatIntentData(chatObject.getContext());
            String temp = getNextMessage(res, chatObject);
            if (intent.equals("deny")) {
                chatObject.setApproved("false");
            }

            if (temp != null){
                return temp;
            }
        }

        QueryObject res = gatIntentData(chatObject.getContext());
        if (res != null){
            response = getSteps(res);
            chatObject.setContext(null);
        }

        return response;
    }

    private QueryObject gatIntentData(String intent){
        QueryObject res = null;
        if (intent.equals("new_nic")) {
            res = getData("NIC", "NewNIC");
        }else if (intent.equals("lost_nic")) {
            res = getData("NIC", "MissingNIC");
        }else if (intent.equals("new_license")) {
            res = getData("DrivingLicense", "NewDrivingLicense");
        }else if (intent.equals("lost_license")) {
            res = getData("DrivingLicense", "MissingDrivingLicense");
        }
        return res;
    }

    private String getNextMessage(QueryObject queryObject, ChatObject chatObject) {
        String res = "";
        if (chatObject.getStep() != 0 && queryObject.prereq.size() == chatObject.getStep()){
            return null;
        }else if (queryObject.prereq.size() > 0){
            res = queryObject.prereq.get(chatObject.getStep()).instruction;
            chatObject.setStep(chatObject.getStep() + 1);
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

    private String googleQ (String type, String area) throws IOException {
        String q = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+type
                +"%20"+area+"&inputtype=textquery&fields=formatted_address,name,opening_hours,geometry&key="+apikey;
        String ret = "";

        URL obj = new URL(q);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject myResponse = new JSONObject(response.toString());

        //return response.toString();
        String satatus = myResponse.getString("status");
        if (satatus.equals("OK")){
            JSONArray resArray= myResponse.getJSONArray("candidates");
            if (resArray.length()>0){
                JSONObject resAddress = resArray.getJSONObject(0);
                return resAddress.getString("formatted_address");
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

    private ResultSet sparqlTest(String queryString) {
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Resource res = resourceLoader.getResource("classpath:newuvgalk.owl");
        Model model = FileManager.get().loadModel(res.getFilename());
        Query query= QueryFactory.create(queryString);
        QueryExecution queryExecution= QueryExecutionFactory.create(query,model);
        ResultSet resultSet=queryExecution.execSelect();
        return resultSet;
    }


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
