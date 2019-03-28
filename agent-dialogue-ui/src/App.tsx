import React, { Component } from "react"
import "./App.css"
import logo from "./resources/img/Agent_logo.jpg"

class App extends Component {
  public render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <h1 className="App-title">Agent Dialogue Web Simulator</h1>
        </header>
      </div>
    )
  }
}

export default App
