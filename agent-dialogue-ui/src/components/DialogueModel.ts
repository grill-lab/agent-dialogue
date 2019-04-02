import {IMessage, Message} from "./MessageModel"

export interface IDialogue {
  messages: IMessage[]
}

export class Dialogue implements IDialogue {
  constructor(model: IDialogue) {
    Object.assign(this, model)
  }

  public messages!: IMessage[]
}

export const US = "us"
export const THEM = "them"

// noinspection SpellCheckingInspection
export const sampleDialogue = () => new Dialogue({messages: [
    new Message({speaker: "them", text: "Hello"}),
    new Message({speaker: US, text: "How are you"}),
    new Message({speaker: THEM, text: "I'm well"}),
    new Message({speaker: US,
      text: "Your bones don't break, mine do. That's clear. Your cells "
            + "react to bacteria and viruses differently than mine. "
            + "You don't get sick, I do. That's also clear. But for some "
            + "reason, you and I react the exact same way to water. We "
            + "swallow it too fast, we choke. We get some in our lungs, "
            + "we drown. However unreal it may seem, we are connected, "
            + "you and I. We're on the same curve, just on opposite ends."}),
    new Message({speaker: THEM,
      text: "You think water moves fast? You should see ice. It moves like "
            + "it has a mind. Like it knows it killed the world once and "
            + "got a taste for murder. After the avalanche, it took us a "
            + "week to climb out. Now, I don't know exactly when we turned "
            + "on each other, but I know that seven of us survived the "
            + "slide... and only five made it out. Now we took an oath, "
            + "that I'm breaking now. We said we'd say it was the snow "
            + "that killed the other two, but it wasn't. Nature is lethal "
            + "but it doesn't hold a candle to man.\n"}),
    new Message({speaker: US,
      text: "Look, just because I don't be givin' no man a foot massage "
            + "don't make it right for Marsellus to throw Antwone into a "
            + "glass motherfuckin' house, fuckin' up the way the nigger "
            + "talks. Motherfucker do that shit to me, he better paralyze "
            + "my ass, 'cause I'll kill the motherfucker, know what I'm "
            + "sayin'?\n"}),
    new Message({speaker: US,
      text: "Your bones don't break, mine do. That's clear. Your cells "
            + "react to bacteria and viruses differently than mine. "
            + "You don't get sick, I do. That's also clear. But for some "
            + "reason, you and I react the exact same way to water. We "
            + "swallow it too fast, we choke. We get some in our lungs, "
            + "we drown. However unreal it may seem, we are connected, "
            + "you and I. We're on the same curve, just on opposite ends."}),
    new Message({speaker: THEM,
      text: "You think water moves fast? You should see ice. It moves like "
            + "it has a mind. Like it knows it killed the world once and "
            + "got a taste for murder. After the avalanche, it took us a "
            + "week to climb out. Now, I don't know exactly when we turned "
            + "on each other, but I know that seven of us survived the "
            + "slide... and only five made it out. Now we took an oath, "
            + "that I'm breaking now. We said we'd say it was the snow "
            + "that killed the other two, but it wasn't. Nature is lethal "
            + "but it doesn't hold a candle to man.\n"}),
    new Message({speaker: US,
      text: "Look, just because I don't be givin' no man a foot massage "
            + "don't make it right for Marsellus to throw Antwone into a "
            + "glass motherfuckin' house, fuckin' up the way the nigger "
            + "talks. Motherfucker do that shit to me, he better paralyze "
            + "my ass, 'cause I'll kill the motherfucker, know what I'm "
            + "sayin'?\n"}),
  ]})
