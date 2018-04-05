# Import required modules
import time
import RPi.GPIO as GPIO

# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD)

# set up GPIO pins
GPIO.setup(7, GPIO.OUT) # Connected to PWMA
GPIO.setup(11, GPIO.OUT) # Connected to AIN2
GPIO.setup(12, GPIO.OUT) # Connected to AIN1
#GPIO.setup(13, GPIO.OUT) # Connected to STBY

# Drive the motor clockwise
#pwm=GPIO.PWM(7,25)
#while(1):
#for i in range(1, 20):
GPIO.output(11, GPIO.HIGH)
GPIO.output(12, GPIO.LOW)
    #pwm.start(55)
    #time.sleep(1)

