package com.learn.service.impl;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import com.learn.model.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.learn.model.BotMessage;
import com.learn.repository.BotMessageRepository;
import com.learn.service.BotService;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.iri.impl.Main;
import org.apache.jena.util.FileManager;

@Service
public class BotServiceImpl implements BotService {

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

    public List<BotMessage> listComments() {
        //return botMessageRepository.findAll();
        return getVehicleData("Car", "Toyota");
    }

    private ResultSet sparqlTest(String queryString) {
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Resource res = resourceLoader.getResource("classpath:newestvehicleold.owl");
        Model model = FileManager.get().loadModel(res.getFilename());
        Query query= QueryFactory.create(queryString);
        QueryExecution queryExecution= QueryExecutionFactory.create(query,model);
        ResultSet resultSet=queryExecution.execSelect();
        return resultSet;
    }


    List<BotMessage> getVehicleData(String Vehicletype, String brand){
        System.out.println("event fires");
        List<BotMessage> list= new java.util.ArrayList<BotMessage>();
        String queryString ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX : <http://www.example.com/vehicles/> " +
                "SELECT ?vehicle ?transmission ?sheet ?engine ?end ?start " +
                "WHERE {?sub rdfs:subClassOf :"+Vehicletype+" . " +
                "?vehicle a  ?sub. " +
                "?vehicle :hasTransmissionType ?transmission. " +
                "?vehicle :numberOfSheets ?sheet . " +
                "?vehicle :hasEngineCapacity ?engine. " +
                "?vehicle :hasManufacturer :"+brand+" . " +
                "?vehicle :hasPrice ?p ." +
                "?p :startPrice ?start ." +
                "?p :endPrice ?end}";
        ResultSet resultSet=sparqlTest(queryString);
        while(resultSet.hasNext()){
            BotMessage vehicle;
            QuerySolution querySolution= resultSet.nextSolution();
            try{
                vehicle = new BotMessage(querySolution.getResource("transmission").getLocalName(), querySolution.getResource("vehicle").getLocalName());
            }catch(Exception ex){
                System.out.println(ex);
                continue;
            }
            list.add(vehicle);
        }
        return list;
    }

}
