import * as React from "react"
import css from "./ChatComponent.module.css"
import {ChatInput, IChatInputProperties} from "./ChatInput"
import {ChatTranscript, IChatTranscriptProperties} from "./ChatTranscript"

type IChatComponentProperties = IChatTranscriptProperties & IChatInputProperties

export class ChatComponent
    extends React.Component<IChatComponentProperties, {}> {

  public render(): React.ReactNode {
    // noinspection JSUnusedLocalSymbols
    const {onEnter, ...transcriptProps} = this.props

    return <div className={css.root}>
      <ChatTranscript {...transcriptProps}/>
      <ChatInput onEnter={onEnter}/>
    </div>
  }
}
