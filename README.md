# Agent Dialogue

## Project Overview

**A Framework Agent Dialog, Experimentation and Analysis**

The proposed project aims to develop a meta-agent framework for conversational search research (MetaBot).

Metabot will allow researchers to plug-in existing bots for evaluation and building conversational training / test collections. It addresses fundamental research problems on agent system design.

**Metabot key features**

Allow testing bots from a variety of frameworks (end-to-end ML bots, rule-based systems, and existing Google DialogFlow / Alexa Skills bots). 
Logging and analytics of conversations
Dialogue simulation and crowdsourcing using a web simulator 


Metabot core is the main dialogue system. It will support flexible third-party APIs for major components  -- speech recognition, natural language understanding, dialogue management, and language generation (and TTS). 

Metabot core is a federated agent system.  It runs and coordinates multiple internal and external agent systems. 
Agent intent routing - Determines what agents are relevant to the current step in the conversation
Agent response ranking - Perform relevance ranking of the returned agents
Conversation state management - Keeps a version of the current state of the conversation 
Metabot core will be written in a mixture of Java & Kotlin.  We plan to use RxJava (or a similar framework) for asynchronous event handling. 

**Metabot Simulator**

A web application interface that allows users to play (and simulate) offline and online conversations. 
This is used to create crowdsourced conversational datasets.
Metabot simulator developed with Kotlin.

**Research Goals**

It will be used as the platform for a new proposed TREC track (government-sponsored competition) on conversational agents in 2019.  We will start the development of the tasks over the summer.
Proposed tasks: Federated agent ranking (and response ranking)
Informational dialogue - Using Wikipedia and third-parties APIs

## Local Installation 

In this section, it will be described how to install and run the agent-dialogue system on a local machine. As an example, the system will be connected to the [Wizard of Oz Webapp](https://github.com/USC-ICT/WoZ) but any other supported agent integration would follow a very similar approach 

### Requirements 

In order to run the system locally, it is necessary to have the following programs installed on the local machine 

* [Docker](https://docs.docker.com/get-docker/): The system will create local images for both the gRPC server and the Envoy proxy 
* [Minikube](https://minikube.sigs.k8s.io/docs/start/): Used in order to orchestrate the deployment of the different services
* [Node.js](https://nodejs.org/en/download/): In order to run the web apps for both the chat and Wizard of Oz interfaces.  

### Additional Configurations 

#### Firebase 

In order to store effectively the interaction between the user and the WoZ it is required to configure [Firebase](https://console.firebase.google.com/). To be more specific, it is necessary to create a Firestore database and define an empty collection (the program should create all the required documents and collections automatically when functioning). 

* In this context, it is also very important to remember to set the rules (by selecting the **Rules** tab on the Firestore Database interface) in order to specify the correct read/write permissions. 
* Moreover, under project settings -> service account, we need to create a Private key. This will be required in order to allow our app to interact with Firebase. The key needs to be stored (it will be used later on) and has the following format 

```json 
{
  "type": "service_account",
  "project_id": "<project_id>",
  "private_key_id": "<private_key_id>",
  "private_key": "<private_key>",
  "client_email": "<client_email>",
  "client_id": "<client_id>",
  "auth_uri": "<auth_uri>",
  "token_uri": "<token_uri>",
  "auth_provider_x509_cert_url": "<auth_provider_x509_cert_url>"
}
```

This private key should be stored in the folder `agent-dialogue-core`

#### Files Structure 

The main folders used in order to run the project are the following: 

**Agent Dialogue**

* `agent-dialogue-core`: This is the core folder in which agents and gRPC servers are defined. 
* `agent-dialogue-ui`: The main chat UI that users can use to interact with agents/Woz


#### Configuration File

We do need a configuration file in order to specify some configuration settings of our agent. The configuration file must be a JSON file stored online (Cloud Storage or [JSONBIN.io](https://jsonbin.io/login)) as we need a publicly accessible URL. 

The file must have this format 

```json
{
  "grpc_server_port": "8070",
  "agents": [
    {
      "service_provider": "WIZARD",
      "project_id": "WizardOfOz",
      "configuration_file_URL": "<configuration_file_NAME>"
    }
  ]
}
```

The  **configuration\_file_URL** is the name of the JSON Firebase private key previously defined (which should be stored in the `agent-dialogue-core` folder). 

#### Update agent-dialogue-core Dockerfile

We need to update the `Dockerfile ` in the `agent-dialogue-core` in order to tell it where to find the configuration file. The URL mapping to the configuration file has to be specified in this line in the `Dockerfile`

```yaml
CMD ["java", "-jar", "target/agent-dialogue-core-0.1-SNAPSHOT.jar",
 "<URL_CONFIG_FILE>"]
``` 

#### Local Deployment 

If everything has been configured correctly it is possible to deploy the system. This is a 3 steps process: 

1. Run the script `agent_dialogue_deployment.sh`. This will take care of building the required docker images and managing Minikube deployments. Eventually, the script should open a browser window exposing the public IP that can be used to access the gRPC server. 
2. From withing `agent-dialogue-ui` run `npm start` to start the chat interface (or build from the Dockerfile)
3. From withing `WoZ` run `npm start` to start the Wizard of Oz interface

#### Using the agent-dialogue system

Both web apps should prompt us with login interfaces. Here we should specify the following: 

* **Host URL:** This is the public URL resulting from running `agent_dialogue_deployment.sh`
* **User ID:** Any user ID of choice. This has to be the same one used in both web apps. The only difference is that the WoZ must have as username `ADWizard<USER_ID>`
* **Conversation ID:** This has to be the same one for both interfaces (so that the two apps can communicate)

If the process has been successful, we should be able to interact with the two apps, see real-time updates on both interfaces and the Firestore Database.  

#### Notes on online deployment 

The system can be easily deployed on any cloud-based system. As a general note, when deploying on a server, remember to comment out `imagePullPolicy: Never`from any Kubernetes configuration files.




