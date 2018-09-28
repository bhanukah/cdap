import React, { Component } from "react";
import AboutComp from "./AboutComp";
import ChatComp from "./ChatComp";

export const Home = () => (
  <div class="container-fluid">
    <br />
    <h5>Areas we support</h5>
    <br />
    <div class="row supportdiv">
      <div class="col" align="center">
        <div class="card">
          <img className="img-acc" alt="Accident" />
          <div align="center">
            <h3>Road accidents</h3>
            <p class="supportpara" align="justify">
              Lawbot is primarily intended for the use of all the Sri Lankan
              citizens who seek legal assistance in the areas of road accidents,
              citizen’s registrations and employment information. Tourists who
              face a legal issue in Sri Lanka can seek assistance from Lawbot.
              With the Lawbot, it reduces so much of the hardships people need
              it go through when they need legal assistance. Lawbot gives people
              an idea about the existing Sri Lankan laws related to different
              legal scenarios. Lawbot is more like booking a human lawyer since
              it’s like you are having a normal conversation with a person. You
              won’t feel like you are talking to a machine. And Lawbot is even
              better than just a lawyer since it is available all 24/7 and will
              give you the answers you are looking for just free of charge.
              Lawbot will provide you with the answers for your legal issue at
              the very instant you asked the question. You don’t have to wait
              for the response. It is much faster.
            </p>
          </div>
        </div>
      </div>
      <div class="col" align="center">
        <div class="card">
          <img className="img-reg" alt="Registration" />
          <div align="center">
            <h3>Citizen registrations</h3>
            <p class="supportpara" align="justify">
              Lawbot is primarily intended for the use of all the Sri Lankan
              citizens who seek legal assistance in the areas of road accidents,
              citizen’s registrations and employment information. Tourists who
              face a legal issue in Sri Lanka can seek assistance from Lawbot.
              With the Lawbot, it reduces so much of the hardships people need
              it go through when they need legal assistance. Lawbot gives people
              an idea about the existing Sri Lankan laws related to different
              legal scenarios. Lawbot is more like booking a human lawyer since
              it’s like you are having a normal conversation with a person. You
              won’t feel like you are talking to a machine. And Lawbot is even
              better than just a lawyer since it is available all 24/7 and will
              give you the answers you are looking for just free of charge.
              Lawbot will provide you with the answers for your legal issue at
              the very instant you asked the question. You don’t have to wait
              for the response. It is much faster.
            </p>
          </div>
        </div>
      </div>
      <div class="col" align="center">
        <div class="card">
          <img className="img-emp" alt="Employment" />
          <div align="center">
            <h3>Employment information</h3>
            <p class="supportpara" align="justify">
              Lawbot is primarily intended for the use of all the Sri Lankan
              citizens who seek legal assistance in the areas of road accidents,
              citizen’s registrations and employment information. Tourists who
              face a legal issue in Sri Lanka can seek assistance from Lawbot.
              With the Lawbot, it reduces so much of the hardships people need
              it go through when they need legal assistance. Lawbot gives people
              an idea about the existing Sri Lankan laws related to different
              legal scenarios. Lawbot is more like booking a human lawyer since
              it’s like you are having a normal conversation with a person. You
              won’t feel like you are talking to a machine. And Lawbot is even
              better than just a lawyer since it is available all 24/7 and will
              give you the answers you are looking for just free of charge.
              Lawbot will provide you with the answers for your legal issue at
              the very instant you asked the question. You don’t have to wait
              for the response. It is much faster.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
);

export const Chatbot = () => (
  <div>
    <ChatComp />
  </div>
);

export const About = () => (
  <div>
    <AboutComp />
  </div>
);
