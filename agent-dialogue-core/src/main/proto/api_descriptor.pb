
�/
google/protobuf/timestamp.protogoogle.protobuf";
	Timestamp
seconds (Rseconds
nanos (RnanosB~
com.google.protobufBTimestampProtoPZ+github.com/golang/protobuf/ptypes/timestamp��GPB�Google.Protobuf.WellKnownTypesJ�-
 �
�
 2� Protocol Buffers - Google's data interchange format
 Copyright 2008 Google Inc.  All rights reserved.
 https://developers.google.com/protocol-buffers/

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:

     * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following disclaimer
 in the documentation and/or other materials provided with the
 distribution.
     * Neither the name of Google Inc. nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


 

" ;
	
%" ;

# 
	
# 

$ B
	
$ B

% ,
	
% ,

& /
	
& /

' "
	

' "

( !
	
$( !
�
 z �� A Timestamp represents a point in time independent of any time zone
 or calendar, represented as seconds and fractions of seconds at
 nanosecond resolution in UTC Epoch time. It is encoded using the
 Proleptic Gregorian Calendar which extends the Gregorian calendar
 backwards to year one. It is encoded assuming all minutes are 60
 seconds long, i.e. leap seconds are "smeared" so that no leap second
 table is needed for interpretation. Range is from
 0001-01-01T00:00:00Z to 9999-12-31T23:59:59.999999999Z.
 By restricting to that range, we ensure that we can convert to
 and from  RFC 3339 date strings.
 See [https://www.ietf.org/rfc/rfc3339.txt](https://www.ietf.org/rfc/rfc3339.txt).

 # Examples

 Example 1: Compute Timestamp from POSIX `time()`.

     Timestamp timestamp;
     timestamp.set_seconds(time(NULL));
     timestamp.set_nanos(0);

 Example 2: Compute Timestamp from POSIX `gettimeofday()`.

     struct timeval tv;
     gettimeofday(&tv, NULL);

     Timestamp timestamp;
     timestamp.set_seconds(tv.tv_sec);
     timestamp.set_nanos(tv.tv_usec * 1000);

 Example 3: Compute Timestamp from Win32 `GetSystemTimeAsFileTime()`.

     FILETIME ft;
     GetSystemTimeAsFileTime(&ft);
     UINT64 ticks = (((UINT64)ft.dwHighDateTime) << 32) | ft.dwLowDateTime;

     // A Windows tick is 100 nanoseconds. Windows epoch 1601-01-01T00:00:00Z
     // is 11644473600 seconds before Unix epoch 1970-01-01T00:00:00Z.
     Timestamp timestamp;
     timestamp.set_seconds((INT64) ((ticks / 10000000) - 11644473600LL));
     timestamp.set_nanos((INT32) ((ticks % 10000000) * 100));

 Example 4: Compute Timestamp from Java `System.currentTimeMillis()`.

     long millis = System.currentTimeMillis();

     Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
         .setNanos((int) ((millis % 1000) * 1000000)).build();


 Example 5: Compute Timestamp from current time in Python.

     timestamp = Timestamp()
     timestamp.GetCurrentTime()

 # JSON Mapping

 In JSON format, the Timestamp type is encoded as a string in the
 [RFC 3339](https://www.ietf.org/rfc/rfc3339.txt) format. That is, the
 format is "{year}-{month}-{day}T{hour}:{min}:{sec}[.{frac_sec}]Z"
 where {year} is always expressed using four digits while {month}, {day},
 {hour}, {min}, and {sec} are zero-padded to two digits each. The fractional
 seconds, which can go up to 9 digits (i.e. up to 1 nanosecond resolution),
 are optional. The "Z" suffix indicates the timezone ("UTC"); the timezone
 is required. A proto3 JSON serializer should always use UTC (as indicated by
 "Z") when printing the Timestamp type and a proto3 JSON parser should be
 able to accept both UTC and other timezones (as indicated by an offset).

 For example, "2017-01-15T01:30:15.01Z" encodes 15.01 seconds past
 01:30 UTC on January 15, 2017.

 In JavaScript, one can convert a Date object to this format using the
 standard [toISOString()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString]
 method. In Python, a standard `datetime.datetime` object can be converted
 to this format using [`strftime`](https://docs.python.org/2/library/time.html#time.strftime)
 with the time format spec '%Y-%m-%dT%H:%M:%S.%fZ'. Likewise, in Java, one
 can use the Joda Time's [`ISODateTimeFormat.dateTime()`](
 http://www.joda.org/joda-time/apidocs/org/joda/time/format/ISODateTimeFormat.html#dateTime--
 ) to obtain a formatter capable of generating timestamps in this format.





 z
�
  � Represents seconds of UTC time since Unix epoch
 1970-01-01T00:00:00Z. Must be from 0001-01-01T00:00:00Z to
 9999-12-31T23:59:59Z inclusive.


  z

  

  

  
�
 �� Non-negative fractions of a second at nanosecond resolution. Negative
 second values with fractions must still have non-negative nanos values
 that count forward in time. Must be from 0 to 999,999,999
 inclusive.


 �

 �

 �

 �bproto3
�#
google/protobuf/struct.protogoogle.protobuf"�
Struct;
fields (2#.google.protobuf.Struct.FieldsEntryRfieldsQ
FieldsEntry
key (	Rkey,
value (2.google.protobuf.ValueRvalue:8"�
Value;

null_value (2.google.protobuf.NullValueH R	nullValue#
number_value (H RnumberValue#
string_value (	H RstringValue

bool_value (H R	boolValue<
struct_value (2.google.protobuf.StructH RstructValue;

list_value (2.google.protobuf.ListValueH R	listValueB
kind";
	ListValue.
values (2.google.protobuf.ValueRvalues*
	NullValue

NULL_VALUE B�
com.google.protobufBStructProtoPZ1github.com/golang/protobuf/ptypes/struct;structpb��GPB�Google.Protobuf.WellKnownTypesJ�
 _
�
 2� Protocol Buffers - Google's data interchange format
 Copyright 2008 Google Inc.  All rights reserved.
 https://developers.google.com/protocol-buffers/

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:

     * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following disclaimer
 in the documentation and/or other materials provided with the
 distribution.
     * Neither the name of Google Inc. nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


 

" ;
	
%" ;

# 
	
# 

$ H
	
$ H

% ,
	
% ,

& ,
	
& ,

' "
	

' "

( !
	
$( !
�
 3 6� `Struct` represents a structured data value, consisting of fields
 which map to dynamically typed values. In some languages, `Struct`
 might be supported by a native representation. For example, in
 scripting languages like JS a struct is represented as an
 object. The details of that representation are described together
 with the proto support for the language.

 The JSON representation for `Struct` is JSON object.



 3
9
  5 , Unordered map of dynamically typed values.


  53

  5

  5

  5
�
> N� `Value` represents a dynamically typed value which can be either
 null, a number, a string, a boolean, a recursive struct value, or a
 list of values. A producer of value is expected to set one of that
 variants, absence of any variant indicates an error.

 The JSON representation for `Value` is JSON value.



>
"
 @M The kind of value.


 @
'
 B Represents a null value.


 B

 B

 B
)
D Represents a double value.


D


D

D
)
F Represents a string value.


F


F

F
*
H Represents a boolean value.


H

H	

H
-
J  Represents a structured value.


J


J

J
-
L  Represents a repeated `Value`.


L

L

L
�
 T W� `NullValue` is a singleton enumeration to represent the null value for the
 `Value` type union.

  The JSON representation for `NullValue` is JSON `null`.



 T

  V Null value.


  V

  V
�
\ _v `ListValue` is a wrapper around a repeated field of values.

 The JSON representation for `ListValue` is JSON array.



\
:
 ^- Repeated field of dynamically typed values.


 ^


 ^

 ^

 ^bproto3
�2
client.protoedu.gla.kail.adgoogle/protobuf/timestamp.protogoogle/protobuf/struct.proto"E
ClientConversation/
turn (2.edu.gla.kail.ad.ClientTurnRturn"�

ClientTurnT
interaction_request (2#.edu.gla.kail.ad.InteractionRequestRinteractionRequestU
interction_response (2$.edu.gla.kail.ad.InteractionResponseRinterctionResponse"�
InteractionRequest.
time (2.google.protobuf.TimestampRtime6
	client_id (2.edu.gla.kail.ad.ClientIdRclientIdC
interaction (2!.edu.gla.kail.ad.InputInteractionRinteraction
user_id (	RuserIdQ
agent_request_parameters (2.google.protobuf.StructRagentRequestParameters#
chosen_agents (	RchosenAgents"�
InteractionResponse
response_id (	R
responseId.
time (2.google.protobuf.TimestampRtime6
	client_id (2.edu.gla.kail.ad.ClientIdRclientIdD
interaction (2".edu.gla.kail.ad.OutputInteractionRinteraction_
message_status (28.edu.gla.kail.ad.InteractionResponse.ClientMessageStatusRmessageStatus#
error_message (	RerrorMessage
user_id (	RuserId

session_id (	R	sessionId"<
ClientMessageStatus

NONSET 

SUCCESSFUL	
ERROR"�
InputInteraction
text (	Rtext
audio_bytes (	R
audioBytes
action (	Raction4
type (2 .edu.gla.kail.ad.InteractionTypeRtype
device_type (	R
deviceType#
language_code (	RlanguageCode"�
OutputInteraction
text (	Rtext
audio_bytes (	R
audioBytes
action (	Raction4
type (2 .edu.gla.kail.ad.InteractionTypeRtype*U
ClientId

NONSET 
EXTERNAL_APPLICATION
LOG_REPLAYER
WEB_SIMULATOR*>
InteractionType

NOTSET 
TEXT	
AUDIO

ACTIONB
edu.gla.kail.adJ�%
  T

  



 (
	
 (
	
 (
	
%
�
  = Store entire conversation held within a particular session.
2� This protobuffer is used for storing the data that is being obtained from the client
 and that is being sent back to the client.



 

  !

  

  

  

   
F
 : Store request from the user and response from the agent.





 /

 

 

 *

 -.

0

/



+

./
<
  0 The ID of the client the request is sent from.



 

  

  


  

 

 

 

 

 

 

 

 

 
�
  (� The message that is being passed through gRPC calls, from client to core, containing the information about the request and the request itself.



 
6
 !'") The time of the creation of the request


 ! 

 !

 !"

 !%&
=
""0 The ID of the client the request is sent from.


"!'

"

"

"

#%

#"

#

# 

##$
<
$"/ A unique ID of the user who sent the request.


$#%

$


$

$
�
&8a A struct representing a json configuration object containing agent-specific request parameters.
"v The Structure unique for every Agent type (ServiceProvider), which contains data used by the instance of that Agent.


&$

&

&3

&67
L
'&"? The list of agents ID, that are "asked" to provide the reply.


'

'

'!

'$%
�
+ :� The message that is being passed through gRPC calls, from Core to Client, containing the information about the response and the response itself.



+
P
 -1B The status of the message received from the agent-dialogue-core.


 -	

  .

  .

  .

 /

 /

 /

 0

 0

 0
,
 2" The ID assigned by the Agent.


 21

 2


 2

 2
8
3'"+ The time of the creation of the response.


32

3

3"

3%&
=
4"0 The ID of the client the request is sent from.


43'

4

4

4

5/

5

5

5*

5-.
O
6+"B The status of the message received from the agent-dialogue-core.


65/

6

6&

6)*
\
7"O The error message informing the user about the particular error that occured.


76+

7


7

7
F
8"9 The unique ID of the user that the response is sent to.


87

8


8

8
M
9"@ The unique ID of the session that the response is sent within.


98

9


9

9
*
= B The type of the interaction.



=
l
 >"_ Proto3 doesn't distinguish between field that is empty or is set to it's default (value = 0).


 >


 >

?

?

?

@

@	

@

A

A


A
(
E L Message sent to the agent.



E
8
 F"+ The text that is being sent to the Agent.


 FE

 F


 F

 F
@
G"3 The audio bytes that are being sent to the Agent.


GF

G


G

G
D
H"7 The requested interaction (e.g. "click on a result").


H

H

H

H

I

IH

I

I

I
?
J"2 The type of the device the request is sent from.


JI

J


J

J
/
K"" The language code, e.g. "en-US".


KJ

K


K

K
)
O T Message sent back by agent.



O
H
 P"; The text that is being sent as the response by the Agent.


 PO

 P


 P

 P
P
Q"C The audio bytes that are being sent as the response by the Agent.


QP

Q


Q

Q
D
R"7 The requested interaction (e.g. "click on a result").


R

R

R

R

S

SR

S

S

Sbproto3
�
service.protoedu.gla.kail.ad.servicegoogle/protobuf/timestamp.protoclient.proto"G
UserID
user_id (	RuserId$
activeSession (RactiveSession2�
AgentDialogued
GetResponseFromAgents#.edu.gla.kail.ad.InteractionRequest$.edu.gla.kail.ad.InteractionResponse" P

EndSession.edu.gla.kail.ad.service.UserID.edu.gla.kail.ad.service.UserID" B
edu.gla.kail.ad.servicePJ�
  

  
�
� Package for server and client must be identical! Otherwise connection won't be made and the UNIMPLEMENTED error will be raised.


 0
	
 0

 "
	

 "
	
 (
	
	


  


 
X
  J Send the response from agents using when InteractionRequest is received.


  

  1

  <O
:
 , End current session for a particular user.


 

 

 %+


  


 

  

  

  


  

  
f
 "Y True - the user has an active session; false - the user doesn't have an active session.


 

 

 	

 bproto3