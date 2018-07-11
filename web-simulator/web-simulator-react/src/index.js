import React from 'react';
import ReactDOM from 'react-dom';
import './resources/App.css';
import App from './App';
import InputField from './InputField'
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<App/>, document.getElementById('root'));
ReactDOM.render(<InputField/>, document.getElementById('inputField'));
registerServiceWorker();
