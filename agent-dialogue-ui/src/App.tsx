import React, { Component } from "react"
import { Divider, Tab } from "semantic-ui-react"
import "./App.css"
import {HomePanel} from "./home/HomePanel"
import {RatingsPanel} from "./ratings/RatingsPanel"
import logo from "./resources/img/Agent_logo.jpg"
import {WoZPanel} from "./woz/WoZPanel"

const panes = [
  { menuItem: "Home", render: () => <Tab.Pane attached><HomePanel/></Tab.Pane> },
  { menuItem: "Offline MT Ratings", render: () => <Tab.Pane attached><RatingsPanel/></Tab.Pane> },
  { menuItem: "Wizard of Oz", render: () => <Tab.Pane attached><WoZPanel/></Tab.Pane> },
]

class App extends Component {
  public render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <h1 className="App-title">Agent Dialogue Web Simulator</h1>
        </header>

        <Tab menu={{ color: "orange", inverted: true, attached: true, tabular: false }} panes={panes} />

      </div>
    )
  }
}

export default App
