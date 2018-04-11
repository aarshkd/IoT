import pigpio
import time
import serial
import re
from firebase import firebase

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
    
    def Run(self):
        while True:
            uart_data = self.connect.readline()
            list_of_data = re.findall(r"[-+]?\d*\.\d+|\d+", uart_data)
            upload_RPM = int(list_of_data[0])
            upload_current = float(list_of_data[1])
            result = self.fb.put('/Current_Motor_Details','Current', upload_current)
            result = self.fb.put('/Current_Motor_Details','RPM', upload_RPM)

if __name__ == "__main__":
    Motor_object = Motor()
    Motor_object.Run()