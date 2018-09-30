package com.learn.service.impl;

import com.learn.model.BotMessage;
import com.learn.model.UserMessage;
import com.learn.repository.BotMessageRepository;
import com.learn.service.BotService;
import org.apache.jena.iri.impl.Main;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
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
    private org.springframework.core.io.ResourceLoader resourceLoader;

    public BotMessage createComment(UserMessage userMessage) {
        System.out.print("User Message - id: "+userMessage.getId()+" message: "+userMessage.getMessage());
        String res = "";

        //TimeUnit.SECONDS.sleep(3);


        try {
            res = nlp2(userMessage.getMessage());
            //res = googleQ("police", "Dehiwala");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new BotMessage("1231231212", res);
        //return botMessageRepository.save(userMessage);
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
        return myResponse.getString("status");
    }

    public List<BotMessage> listComments() {
        //return botMessageRepository.findAll();
        return getVehicleData("Car", "Toyota");
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


    List<BotMessage> getVehicleData(String Vehicletype, String brand){
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
                "  myPre:DrivingLicense myPre:NewDrivingLicense ?object\n" +
                "}";
        ResultSet resultSet=sparqlTest(queryString);
        while(resultSet.hasNext()){
            BotMessage vehicle;
            QuerySolution querySolution= resultSet.nextSolution();
            try{
                //vehicle = new BotMessage(querySolution.getResource("transmission").getLocalName(), querySolution.getResource("vehicle").getLocalName());
                vehicle = new BotMessage("jj", querySolution.get("?object").toString());
            }catch(Exception ex){
                System.out.println(ex);
                continue;
            }
            list.add(vehicle);
        }
        return list;
    }

}
