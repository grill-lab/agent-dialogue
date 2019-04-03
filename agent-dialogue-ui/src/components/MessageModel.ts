import {PartialBy} from "../common/util"

export interface IMessage {
  text: string
  time: Date
  speaker?: string
}

export type IMessageArgument = PartialBy<IMessage, "time">

export class Message implements IMessage {
  constructor(model: IMessageArgument) {
    Object.assign(this, {...model, time: model.time || new Date()})
  }

  // noinspection JSUnusedGlobalSymbols
  public readonly text!: string
  // noinspection JSUnusedGlobalSymbols
  public readonly time!: Date
  // noinspection JSUnusedGlobalSymbols
  public readonly speaker?: string
}

