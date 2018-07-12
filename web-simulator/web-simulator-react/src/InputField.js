import React, {Component} from 'react';
import './resources/InputField.css';

class InputField extends Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        // TODO(Adam): implement sending request!

        event.preventDefault();
    }

    render() {
        return (
            <form className = "input-box" onSubmit = {this.handleSubmit}>
                <label>
                    <div>Chat with Agents:</div>
                    <input className = "input-field" type = "text" value = {this.state.value}
                           onChange = {this.handleChange}/>
                </label>
                <input className = "submit-button" type = "submit" value = "Submit"/>
            </form>
        );
    }
}

export default InputField;
