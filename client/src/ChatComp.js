import React, { Component } from 'react';
import ChatBox from './ChatBox';
import Input from './Input';

const BOT_DELAY = 4000;
const BOT_SPEED = 0.03;
const BOT_MAX_CHARS = 150;

function getBotDelay(msg, isQuick = false) {
  let delay = isQuick ? BOT_DELAY / 2 : BOT_DELAY;
  let speed = isQuick ? BOT_SPEED * 2 : BOT_SPEED;
  return msg.length > BOT_MAX_CHARS ? delay : Math.floor(msg.length / speed);
}

class ChatComp extends Component {
    constructor(props) {
      super(props);
      if (props.dialogflow) {
        this.dialogflow = new Object();
      }
      this.botQueue = [];
      this.isProcessingQueue = false;
      this.state = {
        title: props.title || 'React Bot UI',
        messages: [],
        isBotTyping: false,
        isOpen: props.isOpen !== undefined ? props.isOpen : true,
        isVisible: props.isVisible !== undefined ? props.isVisible : true
      };
  
      this.appendMessage = this.appendMessage.bind(this);
      this.processBotQueue = this.processBotQueue.bind(this);
      this.processResponse = this.processResponse.bind(this);
      this.getResponse = this.getResponse.bind(this);
      this.handleResize = this.handleResize.bind(this);
      this.handleSubmitText = this.handleSubmitText.bind(this);
      this.handleToggle = this.handleToggle.bind(this);
    }
  
    appendMessage(text, isUser = false, next = () => {}) {
      let messages = this.state.messages.slice();
      messages.push({isUser, text});
      this.setState({messages, isBotTyping: this.botQueue.length > 0}, next);
    }
  
    processBotQueue(isQuick = false) {
      if (!this.isProcessingQueue && this.botQueue.length) {
        this.isProcessingQueue = true;
        const nextMsg = this.botQueue.shift();
        setTimeout(() => {
          this.isProcessingQueue = false;
          this.appendMessage(nextMsg, false, this.processBotQueue);
        }, getBotDelay(nextMsg, isQuick));
      }
    }
  
    processResponse(text) {
      const messages = text
        .match(/[^.!?]+[.!?]*/g)
        .map(str => str.trim());
      this.botQueue = this.botQueue.concat(messages);
  
      // start processing bot queue
      const isQuick = !this.state.isBotTyping;
      this.setState({isBotTyping: true}, () => this.processBotQueue(isQuick));
    }
  
    getResponse(text) {
      return this.dialogflow.textRequest(text)
        .then(data => data.result.fulfillment.speech);
    }
  
    handleSubmitText(text) {
  
      // append user text
      this.appendMessage(text, true);
  
      // fetch bot text, process as queue
      if (this.dialogflow) {
        this.getResponse(text)
          .then(this.processResponse);
      } else if (this.props.getResponse) {
        this.props.getResponse(text)
          .then(this.processResponse);
      } else {
        this.processResponse('Sorry, I\'m not configured to respond. :\'(')
      }
    }
  
    handleResize(e) {
      const window = e.target || e;
      const y = window.innerHeight - 200;
      const header = document.querySelector('.container header');
      const input = document.querySelector('.container .text-form');
      let dialogHeight = y - 200 - 10;
      if (dialogHeight < 0 || !dialogHeight) {
        dialogHeight = 0;
      } else if (this.props.dialogHeightMax && dialogHeight > this.props.dialogHeightMax) {
        dialogHeight = this.props.dialogHeightMax;
      }
      this.setState({dialogHeight});
    }
    
    handleToggle() {
      if (this.state.isVisible) {
        this.setState({isOpen: !this.state.isOpen});
      } else {
        this.setState({isVisible: true});
      }
    }
  
    componentDidMount() {
      window.addEventListener('resize', this.handleResize);
      this.handleResize(window);
    }
  
    componentWillUnmount() {
      //window.removeEventListener('resize');
    }
  
    render() {
      return (
        <div className="container" style={this.state.isVisible ? {display: 'block'} : {display: 'none'}}>
          <div style={this.state.isOpen ? {minHeight: `${this.state.dialogHeight}px`} : {maxHeight: 0, overflow: 'hidden'}}>
            <ChatBox messages={this.state.messages}
                    isBotTyping={this.state.isBotTyping}
                    isUserHidden={this.props.isUserHidden}
                    dialogHeight={this.state.dialogHeight} />
            <Input onSubmit={this.handleSubmitText} />
          </div>
        </div>
      );
    }
}

export default ChatComp;