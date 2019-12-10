# Engineering ROP Minor Project
### Hunter Ruebsamen

## Python UDP Server

This is the UDP Server that run's on the Raspberry Pi Zero W and interprets incoming UDP packets from the Android application.
It will listen to incoming UDP packets on port 61625.

The current commands that can be interpreted are:

Command | Action
--------|-------
**PING** | respond with a "ping received"
**STOP** | STOP all motors
**SHUTDOWN** | SHUTDOWN Raspberry Pi
**FF:RR** | FF is forward strength (-1 to 1) and RR is rotation (-1 to 1)