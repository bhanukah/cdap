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
        <div class="card supcard">
          <img className="img-acc" alt="Accident" />
          <div align="center">
            <h3>Road accidents</h3>
            <p class="supportpara" align="justify">
              This category includes all the legal matters related to pedestrian
              and vehicle accidents. This is the second top most category where
              people frequently ask questions about.
            </p>
          </div>
        </div>
      </div>
      <div class="col" align="center">
        <div class="card supcard">
          <img className="img-reg" alt="Registration" />
          <div align="center">
            <h3>Citizen registrations</h3>
            <p class="supportpara" align="justify">
              This category includes all the legal matters related to
              'Citizenship', 'Birth', 'Marriage', 'Death' and 'Voter
              Registrations'. Obtaining a new identity card, apply for dual
              citizenship, obtaining residence visa, obtaining a certified copy
              of certificate of birth/marriage/death, translating the
              certificate of birth/marriage/death are few examples which falls
              under Citizen's Registrations. This is the top most category,
              where people frequently ask questions about.
            </p>
          </div>
        </div>
      </div>
      <div class="col" align="center">
        <div class="card supcard">
          <img className="img-emp" alt="Employment" />
          <div align="center">
            <h3>Employment information</h3>
            <p class="supportpara" align="justify">
              This category includes information regarding both government and
              private employees, about employment opportunities, foreign
              employment, pensions, EPF, ETF, career guidance programs and about
              seminars, workshops and training programs conducted for employees.
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
