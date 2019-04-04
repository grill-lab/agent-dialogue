import * as React from "react"
import {Icon} from "semantic-ui-react"
import css from "./ChatTranscript.module.css"
import {IDialogue} from "./DialogueModel"
import {ControlledInput} from "./ValueInput"

interface IChatTranscriptProperties {
  dialogue: IDialogue
  us: string
}

class ChatTranscript
    extends React.Component<IChatTranscriptProperties, {}> {

  constructor(props: IChatTranscriptProperties) {
    super(props)
  }

  private messageList?: HTMLDivElement

  private scrollToBottom() {
    if (this.messageList === undefined) { return }
    const scrollHeight = this.messageList.scrollHeight
    const height = this.messageList.clientHeight
    const maxScrollTop = scrollHeight - height
    this.messageList.scrollTop = maxScrollTop > 0 ? maxScrollTop : 0
  }

  // noinspection JSUnusedGlobalSymbols
  public componentDidUpdate = () => {
    this.scrollToBottom()
  }

  // noinspection JSUnusedGlobalSymbols
  public componentDidMount = () => {
    this.scrollToBottom()
  }

  public render(): React.ReactNode {

    const rows = this.props.dialogue.messages.map((message, index) => {
      const cellClass = message.speaker === undefined
      ? css.systemCell
      : message.speaker === this.props.us
        ? css.ourCell
        : css.theirCell
      const rowClass = message.speaker == undefined
      ? css.systemRow
      : message.speaker === this.props.us
        ? css.ourRow
        : css.theirRow
      return <div className={css.row + " " + rowClass} key={index}>
        <div className={css.cell + " " + cellClass}>{message.text}</div>
      </div>
    })

    return <div className={css.transcript}>
        <div
            className={css.scrollable}
            ref={(div) => {this.messageList = div || undefined}}>{rows}</div>
      </div>
  }
}

interface IChatInputProperties {
  onEnter: (text: string) => void
}

interface IChatInputState {
  value: string
}

class ChatInput
    extends React.Component<IChatInputProperties, IChatInputState> {

  constructor(props: IChatInputProperties) {
    super(props)
    this.state = { value: "" }
  }

  private onCommit = () => {
    const value = this.state.value.trim()
    if (value.length !== 0) {
      this.props.onEnter(value)
    }
    this.onRevert()
  }

  private onRevert = () => {
    this.setState({value: ""})
  }

  private onChange = (text: string) => {
    this.setState({value: text})
  }

  public render(): React.ReactNode {
    return <div className={css.entry}>
        <ControlledInput
            value={this.state.value}
            fluid
            onCommit={this.onCommit}
            onRevert={this.onRevert}
            onUpdate={this.onChange}
            icon={<Icon
                name="arrow up" inverted circular link
                className={css.enterButton}
                disabled={this.state.value.trim().length === 0}
                onClick={this.onCommit}
            />}
        />
      </div>
  }
}

type IChatComponentProperties = IChatTranscriptProperties & IChatInputProperties

export class ChatComponent
    extends React.Component<IChatComponentProperties, {}> {

  constructor(props: IChatComponentProperties) {
    super(props)
  }

  public render(): React.ReactNode {
    // noinspection JSUnusedLocalSymbols
    const {onEnter, ...transcriptProps} = this.props

    return <div className={css.root}>
      <ChatTranscript {...transcriptProps}/>
      <ChatInput onEnter={onEnter}/>
    </div>
  }
}
