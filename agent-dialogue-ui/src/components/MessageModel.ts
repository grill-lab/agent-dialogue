import {PartialBy} from "../common/util"

export interface IMessage {
  text: string
  time: Date
  speaker: string
}

type IMessageArgument = PartialBy<IMessage, "time">

export class Message implements IMessage {
  constructor(model: IMessageArgument) {
    Object.assign(this, {...model, time: model.time || new Date()})
  }

  public readonly text!: string
  public readonly time!: Date
  public readonly speaker!: string
}

