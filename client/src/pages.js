import React, { Component } from 'react';
import AboutComp from './AboutComp'
import ChatComp from './ChatComp'

export const Home = () => (
<div class="container-fluid">
<br/><h5>Areas we support</h5><br/>
  <div class="row">
        <div class="col" align="center">
            <img className="img-acc" alt="Accident" />
        </div>
        <div class="col" align="center">
            <img className="img-reg" alt="Accident" />
        </div>
        <div class="col" align="center">
            <img className="img-emp" alt="Accident" />
        </div>
  </div>
  <div class="row">
    <div class="col" align="center">
      Road accidents
    </div>
    <div class="col" align="center">
      Citizen registrations
    </div>
    <div class="col" align="center">
      Employment information
    </div>
  </div>
</div>
)

export const Chatbot = () => (
    <div>
        <ChatComp/>
    </div>
)

export const About = () => (
    <div>
        <AboutComp/>
    </div>
)
