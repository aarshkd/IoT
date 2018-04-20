import pigpio
import time
import serial #python -m serial.tools.list_ports
import re
from firebase import firebase

class Motor():
    #Initialize Motor class
    def init(self):     
        #Serial port initialization
        self.connect = serial.Serial()
        self.connect.port = "/dev/ttyACM0"
        self.connect.baudrate = 115200
        self.connect.open()
        
        self.dir = self.fb.get('/Current_Motor_Readings/Direction', None)
        if(self.dir):
            self.pi.write(self.IN1_pin,1)
            self.pi.write(self.IN2_pin,0)
        elif(not self.dir):
            self.pi.write(self.IN1_pin,0)
            self.pi.write(self.IN2_pin,1)

        #Motor Initialization 
        self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, 0)
        time.sleep(1)
        print 'Running motor at 100%'
        self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.input_duty_cycle)

        #Finding Maximum RPM
        for tr in range(1,3):
            self.maxRPM = self.connect.readline()
            print self.maxRPM
            self.maxRPM = re.findall(r"[-+]?\d*\.\d+|\d+", self.maxRPM)
            self.maxRPM = int(self.maxRPM[0])
        try:
            self.fb.put('/Current_Motor_Readings','userRPM', self.maxRPM)
        except Exception:
            pass
       
        #PID constants Initialization
        self.p_error = 0
        self.d_error = 0
        self.i_error = 0
        self.feedRPM = 500
        self.kp = 0.25 #0.25
        self.kd = 0.035 #0.04
        self.ki = 0.0004
        
    def mymap(self, x, in_min, in_max, out_min, out_max):
        return ((x - in_min)*(out_max - out_min)/(in_max - in_min)) + out_min

    def Run(self):
        self.PWM_pin = 12 #GPIO 12 on board pin 32
        self.IN1_pin = 17
        self.IN2_pin = 27
        self.PWM_frequency = 20000
        self.duty_cycle = 100
        self.input_duty_cycle = self.duty_cycle * 10000
        self.pi = pigpio.pi()
        self.fb = firebase.FirebaseApplication('https://fir-project-3c3bc.firebaseio.com/', None)
        self.oldSwitch = 0
        self.olddir = 0
        
        #GPIO Initialization
        self.pi.set_mode(self.PWM_pin, pigpio.OUTPUT)   
        self.pi.set_mode(self.IN1_pin, pigpio.OUTPUT)
        self.pi.set_mode(self.IN2_pin, pigpio.OUTPUT)

        # self.pi.write(self.IN1_pin,0)
        # self.pi.write(self.IN2_pin,1)
        
        self.encoderRPM = 0
        self.current = 0
        self.power = 0
        self.userRPM = 0
        self.dir = 1
        try:
            self.fb.put('/Current_Motor_Readings','Current', self.current)
            self.fb.put('/Current_Motor_Readings','encoderRPM', self.encoderRPM)
            self.fb.put('/Current_Motor_Readings','Power', self.power)
            self.fb.put('/Current_Motor_Readings','userRPM', self.userRPM)
            self.fb.put('/Current_Motor_Readings','Direction', self.dir)
        except Exception:
            pass

        while True:
            #Switch on-off routine
            try:
                self.switch = self.fb.get('/Current_Motor_Readings/Switch', None)
                self.dir = self.fb.get('/Current_Motor_Readings/Direction', None)
            except Exception:
                pass

            if(self.olddir != self.dir):
                self.init()
                
            self.olddir = self.dir
            
            if(self.oldSwitch==0 and self.switch==1):
                self.init()
                self.oldValue = self.maxRPM
                                    
            if(self.oldSwitch==1 and self.switch==0):
                self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, 0)
                self.encoderRPM = 0
                self.current = 0
                self.power = 0
                self.userRPM = 0
                try:
                    self.fb.put('/Current_Motor_Readings','Current', self.current)
                    self.fb.put('/Current_Motor_Readings','encoderRPM', self.encoderRPM)
                    self.fb.put('/Current_Motor_Readings','Power', self.power)
                    self.fb.put('/Current_Motor_Readings','userRPM', self.userRPM)
                except Exception:
                    pass               
            self.oldSwitch = self.switch
            
            while self.switch:                
                #Read data from serial port and extract it
                self.uart_data = self.connect.readline()
                self.list_of_data = re.findall(r"[-+]?\d*\.\d+|\d+", self.uart_data)
                self.encoderRPM = int(self.list_of_data[0])
                self.current = float(self.list_of_data[1])
                self.power = float(self.list_of_data[2])

                if(self.olddir != self.dir):
                    self.init()
                
                self.olddir = self.dir
                #Read and write data to and from firebase
                try:
                    self.fb.put('/Current_Motor_Readings','Current', self.current)
                    self.fb.put('/Current_Motor_Readings','encoderRPM', self.encoderRPM)
                    self.fb.put('/Current_Motor_Readings','Power', self.power)
                    self.userRPM = self.fb.get('/Current_Motor_Readings/userRPM', None)
                    self.PID_Status = self.fb.get('/Current_Motor_Readings/PID', None)
                except Exception:
                    pass
                
                #PID off condition
                if not self.PID_Status:
                    self.OpenLoop()

                #PID on condition
                if self.PID_Status:
                    self.CloseLoop()
            
                try:
                    self.switch = self.fb.get('/Current_Motor_Readings/Switch', None)
                    self.dir = self.fb.get('/Current_Motor_Readings/Direction', None)
                except Exception:
                    pass

    def OpenLoop(self):
        #PID off routine
        self.userDutyCycle = int(self.mymap(self.userRPM, 0, self.maxRPM, 0, 100))
        print 'userRPM: ', self.userRPM,'   encoderRPM: ',self.encoderRPM, '   userDutyCycle: ',self.userDutyCycle
        self.userDutyCycle = self.userDutyCycle * 10000
        
        if(self.oldValue != self.userRPM and self.userRPM <= self.maxRPM):
            self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.userDutyCycle)
            print 'PID off : Gayo andar!'
        self.oldValue = self.userRPM

    def CloseLoop(self):
        #PID on routin
        self.p_error = self.userRPM - self.encoderRPM
        self.feedRPM = int(self.feedRPM + (self.p_error*self.kp) + (self.d_error*self.kd) + (self.i_error*self.ki))
        self.feedDutyCycle = int(self.mymap(self.feedRPM, 0, self.maxRPM, 5, 100))
        print 'p_error: ',self.p_error, '   userRPM: ', self.userRPM,'   encoderRPM: ',self.encoderRPM,'   feedRPM: ',self.feedRPM,'   feedDutyCycle: ',self.feedDutyCycle
        self.feedDutyCycle = self.feedDutyCycle * 10000 

        if(self.feedDutyCycle > 0 and self.feedDutyCycle < 1000000 and self.userRPM <= self.maxRPM):
            self.pi.hardware_PWM(self.PWM_pin, self.PWM_frequency, self.feedDutyCycle)
            print 'PID on: Gayo andar!'
        elif(self.feedDutyCycle<=0):
            self.feedDutyCycle = 10
            print 'Minus'
        elif(self.feedDutyCycle >=100):
            self.feedDutyCycle = 90
            print 'plus'
            

        self.d_error = self.p_error
        self.i_error = self.i_error + self.d_error

if __name__ == "__main__":
    Motor_object = Motor()
    Motor_object.Run()