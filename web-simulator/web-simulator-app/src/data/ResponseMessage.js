'use strict';

import Immutable from 'immutable';

const ResponseMessage = Immutable.Record({
    id: '',
    time: '',
    received: false,
    text: '...',
});

export default ResponseMessage;