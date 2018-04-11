import pigpio
import time
import serial
import re
from firebase import firebase
# from scipy.interpolate import interp1d

class Motor():
    #Initialize Motor class
    def __init__(self):
        self.PWM_pin = 12 #GPIO 12 on board pin 32
        self.PWM_frequency = 30000
        self.duty_cycle = 100
        self.input_duty_cycle = self.duty_cycle * 10000
        self.pi = pigpio.pi()

        self.fb = firebase.FirebaseApplication('https://fir-project-3c3bc.firebaseio.com/', None)
        
        #Serial port initialization
        self.connect = serial.Serial()
        self.connect.port = "/dev/ttyS0"
        self.connect.baudrate = 115200
        self.connect.open()

        #GPIO initialization
        self.pi.set_mode(self.PWM_pin, pigpio.OUTPUT)
        self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.input_duty_cycle)
        time.sleep(1)
        oldValue = 625

        
    def mymap(self, x, in_min, in_max, out_min, out_max):
        return (x - in_min)*(out_max - out_min)/(in_max - in_min)+out_min

    def Run(self):
        oldValue = 625
        while True:
            uart_data = self.connect.readline()
            list_of_data = re.findall(r"[-+]?\d*\.\d+|\d+", uart_data)
            encoderRPM = int(list_of_data[0])
            current = float(list_of_data[1])
            power = float(list_of_data[2])
            #print upload_RPM , '\t' , upload_current , '\t' , upload_power
            self.fb.put('/Current_Motor_Readings','Current', current)
            self.fb.put('/Current_Motor_Readings','encoderRPM', encoderRPM)
            self.fb.put('/Current_Motor_Readings','Power', power)
    
            userRPM = self.fb.get('/Current_Motor_Readings/userRPM', None)
            userDutyCycle = int(round(self.mymap(userRPM, 32, 625, 5, 100)))
            userDutyCycle = userDutyCycle * 10000
            print userDutyCycle
            if(oldValue != userRPM):
                self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, userDutyCycle)
                print 'Gayo andar!'
            oldValue = userRPM

if __name__ == "__main__":
    Motor_object = Motor()
    Motor_object.Run()