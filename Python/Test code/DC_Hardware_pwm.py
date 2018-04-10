import pigpio
import time
import os

#class Motor():
    #Initialize Motor class
 #   def __init__(self):
os.system("sudo pigpiod")
pi=pigpio.pi()

duty=5            
final = duty*10000
pin =12


pi.set_mode(pin, pigpio.OUTPUT)
#rint(pi.get_mode(pin))
#r=pi.set_PWM_frequency(pin,500)
#q=pi.set_PWM_dutycycle(pin,127)

q=pi.hardware_PWM(pin, 30000, 1000000)
time.sleep(0.001)
q=pi.hardware_PWM(pin, 30000, final)
z=pi.get_PWM_frequency(pin)
#pi.set_PWM_dutycycle(pin, 64)
print(q)
print(z)
while True:
    #for i in range(1, 10):
    time.sleep(1)

GPIO.cleanup()