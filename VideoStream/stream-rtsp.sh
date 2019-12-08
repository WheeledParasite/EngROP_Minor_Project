#!/bin/bash
raspivid -o - -t 0 -w 1000 -h 576 -fps 20 -b 250000 | cvlc -vvv stream:///dev/stdin --sout '#rtp{access=udp,sdp=rtsp://:8554/stream}' :demux=h264
