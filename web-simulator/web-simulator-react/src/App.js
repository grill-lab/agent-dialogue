import React, {Component} from 'react';
import Agent_logo from './resources/Agent_logo.jpg'
import './resources/App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={Agent_logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Agent Dialogue Web Simulator</h1>
        </header>
      </div>
    );
  }
}

export default App;
