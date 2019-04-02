import React from "react"
import {Form, Grid} from "semantic-ui-react"
import {ChatComponent} from "../components/ChatTranscript"
import {
  IDialogue,
  sampleDialogue,
  US,
} from "../components/DialogueModel"
import {Message} from "../components/MessageModel"
import css from "./WoZPanel.module.css"

interface IWozPanelState {
  dialogue: IDialogue
}

interface IWozPanelProperties {
  dialogue?: IDialogue
}

export class WoZPanel extends React.Component<IWozPanelProperties, IWozPanelState> {

  constructor(props: IWozPanelProperties) {
    super(props)

    this.state = {
      dialogue: props.dialogue === undefined
                ? sampleDialogue() : props.dialogue,
    }
  }

  private onEnter = (text: string) => {
    this.setState((prev) => {
      const d = prev.dialogue
      d.messages.push(new Message({speaker: US, text}))
      return {dialogue: d}
    })
  }

  public render(): React.ReactNode {

    const userType: string = "user"

    const handleChange = (): void => {}

    return <Grid className={css.mainGrid}>
    <Grid.Row>
      <Grid.Column width={13}>
        <ChatComponent
            dialogue={this.state.dialogue}
            us={US}
            onEnter={this.onEnter}
        />
      </Grid.Column>

      <Grid.Column width={3}>
        <Form>
          <Form.Input fluid label="User ID"/>
          <Form.Group inline>
            <label>Type</label>
            <Form.Radio
                label="Wizard"
                value="wizard"
                checked={userType === "wizard"}
                onChange={handleChange}
            />
            <Form.Radio
                label="User"
                value="user"
                checked={userType === "user"}
                onChange={handleChange}
            />
          </Form.Group>
          <Form.Input fluid label="Conversation ID"/>
        </Form>
      </Grid.Column>
    </Grid.Row>
    </Grid>
  }
}
