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
**L:XX** | Turn on Left motor to XX speed
**R:XX** | Turn on Right motor to XX speed