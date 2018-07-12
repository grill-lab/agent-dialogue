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
        alert('Before: ' + this.state.textInput);
        axios.post(`javascriptlinker`, {utteranceRequest});

        $.ajax({
            type: 'POST',
            url: '/javascriptlinker',
            data: utteranceRequest
        })
            .done(function(utteranceRequest) {
                self.clearForm()
            })
            .fail(function(jqXhr) {
                console.log('failed to register');
            });

        alert('After: ' + this.state.language);
        /* .then(TODO(Adam): do the magic with handling the response.
        res => {
            console.log(res);
            console.log(res.data);
            })*/
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
