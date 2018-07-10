'use strict';

import WebSimulatorActionTypes from './WebSimulatorActionTypes';

import TodoDispatcher from './TodoDispatcher'; //TODO

const Actions = {
    sendRequest(text) {
        TodoDispatcher.dispatch({ //TODO
            type: WebSimulatorActionTypes.SEND_REQUEST,
            text,
        });
    },

    setLanguage(text) {
        TodoDispatcher.dispatch({ //TODO
            type: WebSimulatorActionTypes.SET_LANGUAGE,
            text,
        });
    },

};

export default Actions;