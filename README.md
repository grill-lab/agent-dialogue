# agent-dialogue

**A Framework Agent Dialog, Experimentation and Analysis**

The proposed projects aims to develop a meta-agent framework for conversational search research (MetaBot).

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
Metabot core will be written in mixture of Java & Kotlin.  We plan to use RxJava (or similar framework) for asynchronous event handling. 

**Metabot Simulator**

A web application interface that allows users to play (and simulate) offline and online conversations. 
This is used to create crowdsourced conversational datasets.
Metabot simulator developed with Kotlin.

**Research Goals**

It will be used as the platform for a new proposed TREC track (government sponsored competition) on conversational agents in 2019.  We will start development of the tasks over the summer.
Proposed tasks: Federated agent ranking (and response ranking)
Informational dialogue - Using Wikipedia and third-parties APIs

