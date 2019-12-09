# Socket Docs: https://docs.python.org/3.3/library/socket.html#socket.socket.listen

import socket
import sys
from subprocess import check_call
from gpiozero import LED

HOST = ''                 # Symbolic name meaning all available interfaces
PORT = 61625

# Create a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port
server_address = (HOST, PORT)
print('starting up on {} port {}'.format(*server_address))
sock.bind(server_address)

led = LED(20)

while True:
	print('\nwaiting to receive message')
	data, address = sock.recvfrom(4096)

	print('received {} bytes from {}'.format(
		len(data), address))
	print(data)

#	if data:
#		sent = sock.sendto(data, address)
#		print('sent {} bytes back to {}'.format(sent, address))
	if data == b'PING':
		print ('ping received')
		sent = sock.sendto(b'ping received', address)
	if data == b'STOP':
		print ('stop received')
		sent = sock.sendto(b'stop received', address)
    if data == b'SHUTDOWN':
        print ('shutting down')
        check_call(['sudo','poweroff'])
