'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import './resources/css/index.css';
import AppContainer from './containers/AppContainer';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<AppContainer />, document.getElementById('root'));
ReactDOM.render(<div>Web-Simulator</div>, document.getElementById('web-simulator'));
//registerServiceWorker();
