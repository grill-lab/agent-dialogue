'use strict';

import Immutable from 'immutable';
import {ReduceStore} from 'flux/utils';
import UserMessage from './UserMessage';
import ResponseMessage from './ResponseMessage';
import WebSimulatorActionTypes from './WebSimulatorActionTypes';
import WebSimulatorDispatcher from './WebSimulatorDispatcher';


class WebSimulatorStore extends ReduceStore {
    constructor() {
        super(WebSimulatorStore);
    }

    getInitialState() {
        return Immutable.OrderedMap();
    }

    reduce(state, action) {
        switch (action.type) {
            case WebSimulatorActionTypes.SEND_REQUEST:
                // Request without text won't be sent.
                if (!action.text) {
                    return state;
                }
                // TODO(Adam) call the agent dialogue function
                const id = "to be implemented";
                return state.set(id, new ResponseMessage({
                    id,
                    text: action.text,
                    complete: false,
                }));

            case WebSimulatorActionTypes.SET_LANGUAGE:
                return state.filter(todo => !todo.complete);

            case TodoActionTypes.DELETE_TODO:
                return state.delete(action.id);

                return state.setIn([action.id, 'text'], action.text);

                const areAllComplete = state.every(todo => todo.complete);
                return state.map(todo => todo.set('complete', !areAllComplete));

                return state.update(
                    action.id,
                    todo => todo.set('complete', !todo.complete),
                );

            default:
                return state;
        }
    }
}

export default new TodoStore();
