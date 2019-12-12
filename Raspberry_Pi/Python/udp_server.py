# Author: Hunter Ruebsamen
# Class: Engineering Design ROP
# Teacher: Mr. Crossett

# Socket Docs: https://docs.python.org/3.3/library/socket.html#socket.socket.listen
# Raspberry Pi Zero W Pinout: https://pinout.xyz/
# L298N Dual H-Bridge Motor Driver: http://www.handsontec.com/dataspecs/L298N%20Motor%20Driver.pdf
# GPIO Zero Library Pin Numbers: https://gpiozero.readthedocs.io/en/stable/recipes.html#pin-numbering

# IN1 (LEFT PWM) - BCM 12 
# IN2 (LEFT DIR) - BCM 16
# IN3 (RIGHT PWM) - BCM 20 
# IN4 (RIGHT DIR) - BCM 21


import socket
import sys
from subprocess import check_call
from gpiozero import Robot

HOST = ''                 # Symbolic name meaning all available interfaces
PORT = 61625
IN1 = 12
IN2 = 16
IN3 = 20
IN4 = 21

# Create a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port
server_address = (HOST, PORT)
print('starting up on {} port {}'.format(*server_address))
sock.bind(server_address)

# create a robot
robot = Robot(left=(IN1,IN2), right=(IN3,IN4))

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
	elif data == b'STOP':
		robot.stop()
		print ('stop received')
		sent = sock.sendto(b'stop received', address)
	elif data == b'SHUTDOWN':
		robot.stop()
        sent = sock.sendto(b'Shutting Down Robot', address)
		print ('shutting down')
		check_call(['sudo','poweroff'])
		break  # exit while loop and take no more commands
	else:
		str_data = data.decode("utf-8")
		new_data = str_data.split(':')
		print(new_data[0])
		print(":")
		print(new_data[1])
		forspeed = float(new_data[0])
		rotspeed = float(new_data[1])
		
		# handle glitches!
		if forspeed > 1.0 or forspeed < -1.0:
			forspeed = 0
		if rotspeed > 1.0 or rotspeed < -1.0:
			rotspeed = 0
			
		# handle movement
		if forspeed > 0:
			# move forward
			if rotspeed >= 0:
				# forward - right
				robot.forward(speed=forspeed,curve_right=rotspeed)
			else:
				# forward - left
				robot.forward(speed=forspeed,curve_left=-rotspeed)
		elif forspeed < 0:
			# move backward
			if rotspeed >= 0:
				# backward - right
				robot.backward(speed=-forspeed,curve_right=rotspeed)
			else:
				# backward - left
				robot.backward(speed=-forspeed,curve_left=-rotspeed)
		else:
			# turn in place
			if rotspeed > 0:
				robot.right(rotspeed)
			elif rotspeed < 0:
				robot.left(-rotspeed)
			else:
				robot.stop()

