import React, { Component } from 'react';
import AboutComp from './AboutComp'
import ChatComp from './ChatComp'

export const Home = () => (
    <div>
        <h1>Home</h1>
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
