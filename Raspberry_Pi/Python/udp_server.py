import socket
import sys
from gpiozero import LED

# Create a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port
server_address = ('192.168.1.115', 61625)
print('starting up on {} port {}'.format(*server_address))
sock.bind(server_address)

led = LED(20)

while True:
	print('\nwaiting to receive message')
	data, address = sock.recvfrom(4096)

	print('received {} bytes from {}'.format(
		len(data), address))
	print(data)

	if data:
		sent = sock.sendto(data, address)
		print('sent {} bytes back to {}'.format(sent, address))
	if data == b'PING':
		print ('ping received')
		led.on()
	if data == b'OFF':
		print ('off received')
		led.off()
