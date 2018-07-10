import React, {Component} from 'react';
import '../resources/css/AppCointainer.css';
import Agent_logo from '../resources/images/Agent_logo.jpg'

class AppContainer extends Component {
    render() {
        return (
            <div className = "AppContainer">
                <header className = "App-header">
                    <img src = {Agent_logo} className = "App-logo" alt = "logo"/>
                    <h1 className = "App-title">Agent Dialogue Web Simulator</h1>
                </header>
                <p className = "App-intro">
                    This is a v0.0.1 prototype of Agent Dialogue Web Simulator.
                </p>
            </div>
        );
    }
}

export default AppContainer;
