'use strict';

import React from 'react';

import classnames from 'classnames';

function AppView(props) {
    return (
        <div>
            <Header {...props} />
            <Main {...props} />
            <Footer {...props} />
        </div>
    );
}
