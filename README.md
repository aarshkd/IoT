# IoT
Industrial IoT concept using PID controller on DC motor

## System is divided into three parts
1. Arduino - reads data from the sensor and sends them to raspberry pi
2. Raspberry pi - controls the motor using hardware pwm and communicates with firebase
3. Android app - uses accelerometer sensor from android phone and communicates with firebase 

### Arduino:


### Raspberry pi:
pySerial module
```
python -m pip install pyserial 
```
pigpiod module - Refer pin layout for GPIO numbers.
```
sudo apt-get update
sudo apt-get install pigpio
```
pigpiod demoen service is reqired to be ON before using the module, run following command to auto start on boot
```
sudo systemctl enable pigpiod
sudo systemctl start pigpiod
```
To enable Serial0(Hardware pin 8-Tx, 10-Rx) in raspberry pi, goto
```
sudo raspi-config
```
Advanced Options/Serial > set No to “Would you like a login shell to be accessible over serial?”, valid and reboot

then type 
```
sudo nano /boot/cmdline.txt
```
and add at the bottom
```
enable_uart=1
```

### Android 
