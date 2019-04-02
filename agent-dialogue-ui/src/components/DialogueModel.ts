import {IMessage} from "./MessageModel"

export interface IDialogue {
  messages: IMessage[]
}

export class Dialogue implements IDialogue {
  constructor(model: IDialogue) {
    Object.assign(this, model)
  }

  public messages!: IMessage[]
}
