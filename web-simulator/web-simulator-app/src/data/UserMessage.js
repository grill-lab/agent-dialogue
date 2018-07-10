'use strict';

import Immutable from 'immutable';

const UserMessage = Immutable.Record({
    id: '',
    time: '',
    text: '',
});

export default UserMessage;