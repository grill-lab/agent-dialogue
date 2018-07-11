'use strict';

import WebSimulatorActionTypes from './WebSimulatorActionTypes';
import WebSimulatorDispatcher from './WebSimulatorDispatcher';

const Actions = {
    sendRequest(text) {
        WebSimulatorDispatcher.dispatch({
            type: WebSimulatorActionTypes.SEND_REQUEST,
            text,
        });
    },

    setLanguage(text) {
        WebSimulatorDispatcher.dispatch({
            type: WebSimulatorActionTypes.SET_LANGUAGE,
            text,
        });
    },

};

export default Actions;