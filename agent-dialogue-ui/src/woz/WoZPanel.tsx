import React from "react"
import {Button, Form, InputOnChangeData, Segment} from "semantic-ui-react"
import {StringMap} from "../App"
import {ADConnection} from "../common/ADConnection"
import {ChatComponent} from "../components/ChatTranscript"
import {Dialogue} from "../components/DialogueModel"

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

interface WoZParamFormProperties {
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

  constructor(props: IWozPanelProperties) {
    super(props)

    console.log(this.props)

    const params: IWozParams = {
      conversationID: (props.params.conversationID || "").trim(),
      userID: (props.params.userID || "").trim(),
      url: (props.params.url || "").trim(),
    }

    this.state = {
      connection: areParamsValid(params)
                  ? new ADConnection(params.url) : undefined,
      params,
    }
  }
}

class WoZParamForm extends React.Component<WoZParamFormProperties, IWozParams> {

  handleChange = (_e: any, data: InputOnChangeData) => {
    this.setState((prev) => (
        {...prev, [data.name]: data.value.trim()}
    ))
  }

  handleSubmit = () => {
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
        <Button type='submit'>Submit</Button>

      </Form>
    </Segment>
  }

  constructor(props: WoZParamFormProperties) {
    super(props)
    this.state = props.params
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

  private onEnter = (text: string) => {
    this.props.connection.send({
      conversationID: this.props.params.conversationID,
      userID: this.props.params.userID,
      text
    })
    this.setState((prev) => {
      const d = prev.dialogue
      d.append({speaker: this.props.params.userID, text})
      return {dialogue: d}
    })
  }

  public render(): React.ReactNode {
    return <ChatComponent
        dialogue={this.state.dialogue}
        us={this.props.params.userID}
        onEnter={this.onEnter}
    />
  }

  constructor(props: IWoZDialogueProperties) {
    super(props)

    console.log(this.props)

    this.state = {
      dialogue: props.dialogue === undefined
                ? new Dialogue({messages: []}) : props.dialogue,
    }

    this.props.connection.subscribe({
      onResponse: (response => {
        console.log(response.getUserId())
        console.log(response.getInteractionList()[0].getText())
        // this.setState((prev) => {
        //   const d = prev.dialogue
        //   d.append({speaker: this.props.params.userID, text: response.get})
        //   return {dialogue: d}
        // })
      }),
      conversationID: this.props.params.conversationID,
      userID: this.props.params.userID,
    })
  }
}