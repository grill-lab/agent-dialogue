/* tslint:disable:max-classes-per-file */
import React from "react"
import {Button, Form, InputOnChangeData, Segment} from "semantic-ui-react"
import {StringMap} from "../App"
import {ADConnection} from "../common/ADConnection"
import {ChatComponent} from "../components/ChatComponent"
import {Dialogue} from "../components/DialogueModel"
import {Message} from "../components/MessageModel"

interface IWozPanelState {
  connection?: ADConnection
  params: IWozParams
}

interface IWozPanelProperties {
  params: StringMap
  dialogue?: Dialogue
}

interface IWozParams {
  url: string
  userID: string
  conversationID: string
}

interface IWoZParamFormProperties {
  params: IWozParams
  onSubmit: (params: IWozParams) => void
}

const areParamsValid = (params: IWozParams) => {
  return params.url !== ""
         && params.conversationID !== ""
         && params.userID !== ""
}

export class WoZPanel
    extends React.Component<IWozPanelProperties, IWozPanelState> {

  constructor(props: IWozPanelProperties) {
    super(props)

    // console.log(this.props)

    const params: IWozParams = {
      conversationID: (props.params.conversationID || "").trim(),
      url: (props.params.url || "").trim(),
      userID: (props.params.userID || "").trim(),
    }

    this.state = {
      connection: areParamsValid(params)
                  ? new ADConnection(params.url) : undefined,
      params,
    }
  }

  private onSubmit = (params: IWozParams) => {
    if (areParamsValid(params)) {
      this.setState({
        connection: new ADConnection(params.url),
        params,
      })
    }
  }

  public render(): React.ReactNode {
    return this.state.connection === undefined
           ? <WoZParamForm
               params={this.state.params}
               onSubmit={this.onSubmit}
           />
           : <WoZDialogue
               connection={this.state.connection}
               dialogue={this.props.dialogue}
               params={this.state.params}
           />
  }
}

class WoZParamForm extends React.Component<IWoZParamFormProperties, IWozParams> {

  constructor(props: IWoZParamFormProperties) {
    super(props)
    this.state = props.params
  }

  public handleChange = (_e: any, data: InputOnChangeData) => {
    this.setState((prev) => (
        {...prev, [data.name]: data.value.trim()}
    ))
  }

  public handleSubmit = () => {
    if (areParamsValid(this.state)) {
      this.props.onSubmit(this.state)
    }
  }

  public render(): React.ReactNode {
    const {conversationID, url, userID} = this.state

    return <Segment>
      <Form onSubmit={this.handleSubmit}>
        <Form.Input label="Host URL" name="url" value={url}
                    onChange={this.handleChange}/>
        <Form.Input label="User ID" name="userID" value={userID}
                    onChange={this.handleChange}/>
        <Form.Input label="Conversation ID" name="conversationID"
                    value={conversationID} onChange={this.handleChange}/>
        <Button type="submit">Submit</Button>

      </Form>
    </Segment>
  }
}

interface IWoZDialogueProperties {
  dialogue?: Dialogue
  connection: ADConnection
  params: IWozParams
}

interface IWoZDialogueState {
  dialogue: Dialogue
}

class WoZDialogue
    extends React.Component<IWoZDialogueProperties, IWoZDialogueState> {

  constructor(props: IWoZDialogueProperties) {
    super(props)

    // console.log(this.props)

    this.state = {
      dialogue: props.dialogue === undefined
                ? new Dialogue({messages: []}) : props.dialogue,
    }

    this.props.connection.subscribe({
      conversationID: this.props.params.conversationID,
      onResponse: ((response) => {
        // console.log("response: ", response)
        const reply = response.asTextResponse()
        const message = new Message({...reply, id: reply.responseID})
        this._append(message)
      }),
      userID: this.props.params.userID,
    })
  }

  private onEnter = (text: string) => {
    const message = new Message({userID: this.props.params.userID, text})
    this.props.connection.send(message, {
      conversationID: this.props.params.conversationID,
    })
    this._append(message)
  }

  private _append = (message: Message) => {
    if (message.text.trim().length === 0) { return }

    this.setState((prev) => {
      const d = prev.dialogue

      // if the message with this ID exists, do not add it
      if (undefined !== d.messages.find(
          (existingMessage) => (existingMessage.id === message.id))) {
        return {dialogue: d}
      }

      const time = message.time
      if (d.messages.length !== 0) {
        const durationBetweenDatesInSec = 300
        const lastMessageTime = d.messages[d.messages.length - 1].time
        if (lastMessageTime.getTime()
            < (time.getTime() - durationBetweenDatesInSec * 1000)) {
          const options = {
            day: "numeric",
            hour: "numeric",
            minute: "numeric",
            month: "numeric",
            second: "numeric",
            year: "numeric",
          }
          d.append({
            text: new Intl.DateTimeFormat(undefined, options).format(time)})
        }
      }

      d.appendMessage(message)

      return {dialogue: d}
    })
  }

  public render(): React.ReactNode {
    return <ChatComponent
        dialogue={this.state.dialogue}
        us={this.props.params.userID}
        them={[]}
        onEnter={this.onEnter}
    />
  }
}
