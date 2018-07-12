import React, {Component} from 'react';
import './resources/InputField.css';
import axios from 'axios';

class InputField extends Component {
    constructor(props) {
        super(props);
        this.state = {
            textInput: '',
            language: 'en-US'
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({textInput: event.target.value});
    }

    handleSubmit(event) {
        // TODO(Adam): implement sending request!
        const utteranceRequest = {
            textInput: this.state.textInput,
            language: this.state.language // TODO(Adam): implement this one.
        };


        axios.post(`JavaScriptLinker`, {utteranceRequest}).then(function (response) {
            alert('aiiiiii111: ' + (response) + document.URL.toString());
        }).catch(function (error) {
            alert('aiiiiii: ' + (error) + document.URL.toString());
        });



        alert('After: ' + this.state.language);

        event.preventDefault();
    }

    render() {
        return (
            <form className = "input-box" onSubmit = {this.handleSubmit}>
                <label>
                    <div>Chat with Agents:</div>
                    <input className = "input-field" type = "text" value = {this.state.textInput}
                           onChange = {this.handleChange}/>
                </label>
                <input className = "submit-button" type = "submit" value = "Submit"/>
            </form>
        );
    }
}

export default InputField;
