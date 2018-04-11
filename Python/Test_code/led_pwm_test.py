from time import sleep  # Library will let us put in delays
import RPi.GPIO as GPIO # Import the RPi Library for GPIO pin control
GPIO.setmode(GPIO.BOARD)# We want to use the physical pin number scheme

LED1=7

GPIO.setup(LED1,GPIO.OUT) # LED1 will be an output pin
#pwm1=GPIO.PWM(LED1,1)  # We need to activate PWM on LED1 so we can dim, use 1000 Hz 

while(1):
    GPIO.output(LED1, GPIO.HIGH)
    sleep(1)
    GPIO.output(LED1, GPIO.LOW)
    sleep(1)
    #pwm1.start(50)              # Start PWM at 0% duty cycle (off) 
    #sleep(0.5)            
    #bright=1                   # Set initial brightness to 1%
    #change = 1
#while(1):                  # Loop Forever
   # bright=bright+change               # Set brightness to half
   # pwm1.ChangeDutyCycle(50)   # Apply new brightness
    #sleep(1)                     # Briefly Pause
    
    # if bright==100 or bright == 0:                 # Keep Brightness at or below 100%
   #     change=-change