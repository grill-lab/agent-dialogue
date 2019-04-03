import {Struct} from "google-protobuf/google/protobuf/struct_pb"
import * as grpcWeb from "grpc-web"
import {AgentDialogueClient} from "../generated/service_grpc_web_pb"
import {
  InputInteraction,
  InteractionRequest,
  InteractionResponse,
} from "../generated/service_pb"

enum InteractionType {
  // noinspection JSUnusedGlobalSymbols SpellCheckingInspection
  NOTSET = proto.edu.gla.kail.ad.InteractionType.NOTSET,
  TEXT = proto.edu.gla.kail.ad.InteractionType.TEXT,
  AUDIO = proto.edu.gla.kail.ad.InteractionType.AUDIO,
  ACTION = proto.edu.gla.kail.ad.InteractionType.ACTION,
}

enum ClientId {
  // noinspection JSUnusedGlobalSymbols SpellCheckingInspection
  NONSET = proto.edu.gla.kail.ad.ClientId.NONSET,
  EXTERNAL_APPLICATION = proto.edu.gla.kail.ad.ClientId.EXTERNAL_APPLICATION,
  LOG_REPLAYER = proto.edu.gla.kail.ad.ClientId.LOG_REPLAYER,
  WEB_SIMULATOR = proto.edu.gla.kail.ad.ClientId.WEB_SIMULATOR,
}

export interface IInputInteractionArguments {
  languageCode?: string
  text?: string
  type?: InteractionType
}

export interface IRequestArguments extends IInputInteractionArguments {
  chosenAgentList?: string[]
  clientID?: proto.edu.gla.kail.ad.ClientId
  conversationID?: string
  userID: string
}

export interface ISubscribeArguments extends IRequestArguments {
  onResponse: (response: InteractionResponse) => void
  onError?: (error: grpcWeb.Error) => void
  onStatus?: (error: grpcWeb.Status) => void
  onEnd?: () => void
}

export interface ISubscription {
  invalidate: () => void
}

interface IConcreteSubscription {
  readonly call: grpcWeb.ClientReadableStream<InteractionResponse>
  readonly client: ADConnection
  readonly request: ISubscribeArguments
}

class ConcreteSubscription implements IConcreteSubscription, ISubscription {
  constructor(args: IConcreteSubscription) {
    Object.assign(this, args)
  }

  readonly client!: ADConnection
  readonly call!: grpcWeb.ClientReadableStream<InteractionResponse>
  readonly request!: ISubscribeArguments

  // noinspection JSUnusedGlobalSymbols
  invalidate = () => {
    this.call.cancel()
    this.client.remove(this)
  }
}

export class ADConnection {

  constructor(host: string) {
    this._hostURL = host
    this._subscriptions = []
  }

  private _hostURL: string
  private _client?: AgentDialogueClient
  private _subscriptions: ConcreteSubscription[]

  private _makeInputInteraction = (args: IInputInteractionArguments)
      : InputInteraction => {
    const input = new InputInteraction()
    input.setText(args.text || "")
    input.setLanguageCode(args.languageCode || "en-US")
    input.setType(args.type || InteractionType.TEXT)
    return input
  }

  private _makeInteractionRequest = (args: IRequestArguments)
      : InteractionRequest => {
    const input = this._makeInputInteraction(args)

    const request = new InteractionRequest()
    request.setClientId(args.clientID || ClientId.WEB_SIMULATOR)
    request.setInteraction(input)
    request.setUserId(args.userID)
    request.setChosenAgentsList(args.chosenAgentList || ["WizardOfOz"])
    if (args.conversationID !== undefined) {
      request.setAgentRequestParameters(Struct.fromJavaScript({
        conversationId: args.conversationID,
      }) as any)
    }

    return request
  }

  // noinspection JSUnusedGlobalSymbols
  private _subscribe = (args: ISubscribeArguments): ConcreteSubscription => {
    const request = this._makeInteractionRequest(args)

    const call = this.getClient().listResponses(
        request, {})

    call.on("data", args.onResponse)

    call.on("error", args.onError || ((error: grpcWeb.Error) => {
      console.error(error)
    }))

    call.on("status", args.onStatus || ((status: grpcWeb.Status) => {
      console.debug(status)
    }))

    call.on("end", args.onEnd || (() => {
      console.debug("stream closed connection")
    }))

    return new ConcreteSubscription({request: args, call, client: this})
  }

  remove = (sub: ConcreteSubscription) => {
    const index = this._subscriptions.indexOf(sub)
    if (index < 0) { return }
    this._subscriptions.splice(index, 1)
  }

  private getClient = (): AgentDialogueClient => {
    if (this._client !== undefined) { return this._client }
    // noinspection SpellCheckingInspection
    return this._client = new AgentDialogueClient(
        this._hostURL, null, {suppressCorsPreflight : false})
  }

  // noinspection JSUnusedGlobalSymbols
  public get hostURL(): string {
    return this._hostURL
  }

  // noinspection JSUnusedGlobalSymbols
  public set hostURL(url: string) {
    if (url === this._hostURL) { return }
    this._subscriptions.forEach((sub) => {sub.call.cancel()})
    this._hostURL = url
    this._client = undefined
    this._subscriptions = this._subscriptions.map(
        (sub) => { return this._subscribe(sub.request) })
  }

  // noinspection JSUnusedGlobalSymbols
  public send = (args: IRequestArguments) => {
    const request = this._makeInteractionRequest(args)

    this.getClient().getResponseFromAgents(
        request, {})
  }

  // noinspection JSUnusedGlobalSymbols
  public subscribe = (args: ISubscribeArguments): ISubscription => {
    const sub = this._subscribe(args)
    this._subscriptions.push(sub)
    return sub
  }

  // noinspection JSUnusedGlobalSymbols
  public terminate = () => {
    this._subscriptions.forEach((sub) => {sub.call.cancel()})
    this._subscriptions = []
  }
}