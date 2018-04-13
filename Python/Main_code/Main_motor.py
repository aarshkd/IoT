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
        self.debug_pin = 4
        self.PWM_frequency = 20000
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
        self.pi.set_mode(self.debug_pin, pigpio.OUTPUT)
        self.pi.write(self.debug_pin, 0)
        self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, 0)
        time.sleep(1)
        print 'Starting at 100%'
        self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.input_duty_cycle)

        for tr in range(1,3):
            self.maxRPM = self.connect.readline()
            self.maxRPM = re.findall(r"[-+]?\d*\.\d+|\d+", self.maxRPM)
            self.maxRPM = int(self.maxRPM[0])
        print 'Ready to go!!!'
        self.pi.write(self.debug_pin, 1)
        self.d_error = 0
        self.i_error = 0
        self.feedRPM = 0
        self.kp = 0.05
        self.kd = 0.03
        self.ki = 0.0000
        
    def mymap(self, x, in_min, in_max, out_min, out_max):
        return (x - in_min)*(out_max - out_min)/(in_max - in_min)+out_min

    def Run(self):
        self.oldValue = self.maxRPM
        #while True:
            #self.switch = self.fb.get('/Current_Motor_Readings/Switch', None)
            #print self.switch
        while True:
            #Read data from serial port and extract it
            self.uart_data = self.connect.readline()
            print self.uart_data
            self.list_of_data = re.findall(r"[-+]?\d*\.\d+|\d+", self.uart_data)
            self.encoderRPM = int(self.list_of_data[0])
            self.current = float(self.list_of_data[1])
            self.power = float(self.list_of_data[2])

            #Read and write data to and from firebase
            self.fb.put('/Current_Motor_Readings','Current', self.current)
            self.fb.put('/Current_Motor_Readings','encoderRPM', self.encoderRPM)
            self.fb.put('/Current_Motor_Readings','Power', self.power)
            self.userRPM = self.fb.get('/Current_Motor_Readings/userRPM', None)
            self.PID_Status = self.fb.get('/Current_Motor_Readings/PID', None)

            #Debugging
            print 'Min: 0' + ' Max: ', self.maxRPM

            self.userDutyCycle = int(round(self.mymap(self.userRPM, 0, self.maxRPM, 5, 100)))
            
            #PID off condition
            if not self.PID_Status:
                self.OpenLoop()

            #PID on condition
            if self.PID_Status:
                self.CloseLoop()
                
            #self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, 0)

    def OpenLoop(self):
        self.userDutyCycle = self.userDutyCycle * 10000
        if(self.oldValue != self.userRPM):
            self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.userDutyCycle)
            print 'PID off : Gayo andar!'
        self.oldValue = self.userRPM

    def CloseLoop(self):
        self.p_error = self.userRPM - self.encoderRPM
        self.feedRPM = self.feedRPM + (self.p_error*self.kp) + (self.d_error*self.kd) + (self.i_error*self.ki)
        self.feedDutyCycle = int(round(self.mymap(self.feedRPM, 0, self.maxRPM, 5, 100)))
        
        self.feedDutyCycle = self.feedDutyCycle * 10000 + self.userDutyCycle * 10000
        print 'feed dutycycle: ',  self.feedDutyCycle/10000, 'FeedRPM : ', self.feedRPM
        if(self.feedDutyCycle > 0 and self.feedDutyCycle < 1000000):
            self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.feedDutyCycle)
            print 'PID on: Gayo andar!'

        self.d_error = self.p_error
        self.i_error = self.i_error + self.d_error

if __name__ == "__main__":
    Motor_object = Motor()
    Motor_object.Run()