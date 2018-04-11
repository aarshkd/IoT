from firebase import firebase
firebase = firebase.FirebaseApplication('https://fir-project-3c3bc.firebaseio.com/', None)
# new_user = 'Ozgur Vatansever'

# result = firebase.put('/Motor_Details',
# '06',{'MotorNumber':'1213','RPM_Value':'154654'})#, {'X_FANCY_HEADER': 'VERY FANCY'})
# print result
from time import sleep  # Library will let us put in delays
import RPi.GPIO as GPIO # Import the RPi Library for GPIO pin control
GPIO.setmode(GPIO.BOARD)# We want to use the physical pin number scheme

LED1=22

GPIO.setup(LED1,GPIO.OUT) # LED1 will be an output pin
pwm1=GPIO.PWM(LED1,100000)
pwm1.start(0)

#result = firebase.post('/users/1', new_user)#, {'print': 'silent'}, {'X_FANCY_HEADER': 'VERY FANCY'})
# print result == None
while(1):
    LED_Status = firebase.get('/Current_Motor_Details/RPM_Value', None)
    LED_Status_Print = firebase.get('/Current_Motor_Details/MotorNumber', None)
    pwm1.ChangeDutyCycle(float(LED_Status))