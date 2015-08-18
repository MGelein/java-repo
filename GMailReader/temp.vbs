Dim Message, Speak
Message="You have 1 unread message"
Set Speak=CreateObject("sapi.spvoice")
Speak.Speak Message
