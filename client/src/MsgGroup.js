import React, {Component} from 'react';

const Message = (props) => (
	<div
		className="message"
		style={props.isImageHidden ? {margin: 0} : {}}>
		{props.text === null ? <TypingAnimation/> : props.text.includes("http") ? <a href={props.text} target="_blank">{props.text}</a> : <p>{props.text}</p>}
	</div>
);

const TypingAnimation = (props) => (
	<div id="wave">
		<span className="dot"></span>
		<span className="dot"></span>
		<span className="dot"></span>
	</div>
);

export const UserImage = (props) => (
	<img className="img-user" alt="User" />
);

export const BotImage = (props) => (
	<img className="img-bot" alt="Bot" />
);

class MsgGroup extends Component {
	render() {
		const image = this.props.group.isUser ? this.props.isUserHidden ? <div/> : <UserImage/> : <BotImage/>;
		const messages = this.props.group.messages.map((text, i) => (
			<Message
				key={i}
				text={text}
				isImageHidden={this.props.group.isUser && this.props.isUserHidden} />
		));

		return (
			<div className={`group group-${this.props.group.isUser ? 'user' : 'bot'}`}>
				{image}
				{messages}
			</div>
		);
	}
}
export default MsgGroup;
