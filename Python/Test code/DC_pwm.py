# Import required modules
import time
import RPi.GPIO as GPIO

# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD)

# set up GPIO pins
GPIO.setup(7, GPIO.OUT) # Connected to PWMA
GPIO.setup(11, GPIO.OUT) # Connected to AIN2
#GPIO.setup(7, GPIO.OUT) # Connected to AIN1
#GPIO.setup(13, GPIO.OUT) # Connected to STBY

# Drive the motor clockwise
pwm=GPIO.PWM(7,15000) #12000
GPIO.output(11, GPIO.HIGH)
#GPIO.output(7, GPIO.LOW)
pwm.start(99)
#while(1):
for i in range(1, 20):
    time.sleep(1)

GPIO.output(11, GPIO.LOW)
GPIO.cleanup()
#GPIO.output(7, GPIO.LOW)
