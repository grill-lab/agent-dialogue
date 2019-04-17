import * as uuid from "uuid"
import {PartialBy} from "../common/util"

export interface IMessage {
  id: string
  text: string
  time: Date
  userID?: string
}

export type IMessageArgument = PartialBy<IMessage, "time" | "id">

export class Message implements IMessage {
  constructor(model: IMessageArgument) {
    Object.assign(this, {
      id: uuid.v4(),
      time: new Date(),
      ...model,
    })
  }

  // noinspection JSUnusedGlobalSymbols
  public readonly id!: string
  // noinspection JSUnusedGlobalSymbols
  public readonly text!: string
  // noinspection JSUnusedGlobalSymbols
  public readonly time!: Date
  // noinspection JSUnusedGlobalSymbols
  public readonly userID?: string
}

