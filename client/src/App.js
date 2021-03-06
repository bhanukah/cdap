import React, { Component } from 'react';
import { Switch, Route, Link } from 'react-router-dom'; // import the react-router-dom components
import { Home, Chatbot, About } from './pages' 
import './App.css';

const Main = () => (
  <main>
    <div className="chatback">
    <Switch>
      <Route exact path='/' component={Home} />
      <Route exact path='/1' component={Chatbot}/>
      <Route exact path='/2' component={About} />
    </Switch>
    </div>
    <div className="footermain">
      <p></p>
    </div>
  </main>
)

class Header extends Component {

  constructor(props) {
    super(props);
    this.state = {
      links: [
        {path: "/", text: "Home", isActive: true},
        {path: "/1", text: "Chatbot", isActive: false},
        {path: "/2", text: "About", isActive: false}
      ]
    }
  }

  handleClick(i) {
    const links = this.state.links.slice(); 
    for (const j in links) {
      links[j].isActive = i == j ;
    }
    this.setState({links: links});
  }


  render() {
    return (
      <div>
        <nav className="navbar navbar-expand-lg navbar-light navbarmain bg-light">
          <ul className="navbar-nav">
            {this.state.links.map((link, i) => 
              <NavLink 
                path={link.path} 
                text={link.text} 
                isActive={link.isActive}
                key={link.path} 
                onClick={() => this.handleClick(i)}
              /> 
              )}
          </ul>
        </nav>
      </div>
    );
  }
}

class NavLink extends Component {

  render() {
      return (
        <li className={"nav-item " + (this.props.isActive ? "active": "")}>
        <a>
                  <Link 
                    className="nav-link" 
                    to={this.props.path}
                    onClick={() => this.props.onClick()}
                  >
              {this.props.text}</Link>
              </a>
        </li>
      );
  }
}

const App = () => (
  <div>
    <div class="mainbanner jumbotron jumbotron-fluid lawbanner">
      <div class="bannertext">
        <h1 class="bannerhead">Law Bot</h1>
        <p class="lead">Artificially Inteligent Legal Information Assistant.</p>
      </div>
    </div>
    <Header />
    <Main />
  </div>
)

export default App;
