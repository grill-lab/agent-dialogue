import React, { Component } from "react"
import { Tab } from "semantic-ui-react"
import css from "./App.module.css"
import {HomePanel} from "./home/HomePanel"
import {RatingsPanel} from "./ratings/RatingsPanel"
import logo from "./resources/img/Agent_logo.jpg"
import {WoZPanel} from "./woz/WoZPanel"

const panes = [
  {
    menuItem: "Home",
    render: () => <Tab.Pane
        className={css.mainTabPane} attached><HomePanel/></Tab.Pane>,
  },
  { menuItem: "Offline MT Ratings",
    render: () => <Tab.Pane
        className={css.mainTabPane} attached><RatingsPanel/></Tab.Pane>,
  },
  { menuItem: "Wizard of Oz",
    render: () => <Tab.Pane
        className={css.mainTabPane} attached><WoZPanel/></Tab.Pane>,
  },
]

class App extends Component {
  public render() {
    return (
      <div className={css.app}>
        <header className={css.appHeader}>
          <img src={logo} className={css.appLogo} alt="logo"/>
          <h1 className={css.appTitle}>Agent Dialogue Web Simulator</h1>
        </header>

        <Tab className={css.mainTab}
             menu={{ color: "orange", inverted: true, attached: true, tabular: false }} panes={panes} />

      </div>
    )
  }
}

export default App
