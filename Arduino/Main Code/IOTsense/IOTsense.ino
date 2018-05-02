#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_INA219.h>

Adafruit_INA219 ina219;
SoftwareSerial mySerial(10, 11); // RX, TX

uint8_t pinA = 2;
uint8_t pinB = 3;
uint16_t Ton = 0;
uint16_t Toff = 0;
uint32_t temp1 = 0; 
uint32_t temp2 = 0;
float freq = 0.00;
uint8_t PPR = 210;
uint32_t RPM = 0;
uint8_t count = 30;

void setup() {
  // put your setup code here, to run once:
  pinMode(pinA,INPUT);
  pinMode(pinB,INPUT);
  mySerial.begin(115200);
  Serial.begin(115200);
  uint32_t currentFrequency;

  ina219.begin();

  // To use a slightly lower 32V, 1A range (higher precision on amps):
  ina219.setCalibration_32V_1A();
  // Or to use a lower 16V, 400mA range (higher precision on volts and amps):
  //ina219.setCalibration_16V_400mA();
}

void loop() {
  float current_mA = 0;
  float power_mW = 0;
  
  temp1 = 0;
  temp2 = 0;
  
  for(uint8_t i = 0; i < count; i++)
  {
    Ton = pulseIn(pinA, HIGH); // In microseconds
    Toff = pulseIn(pinB, LOW); // In microseconds
    temp1 = temp1 + Ton;
    temp2 = temp2 + Toff;

    current_mA = current_mA + ina219.getCurrent_mA();
    power_mW = power_mW + ina219.getPower_mW();
    delay(2);
  }
  
  Ton = temp1 / count;
  Toff = temp2 / count;
  freq = 1 / (float) (Ton + Toff) * 1000;
  RPM = 60 * freq / PPR * 1000;
  temp1 = RPM;

  current_mA = current_mA / count;
  power_mW = power_mW / count;
  
  //if(flagVal == 1){
  if (RPM > 300){
    RPM = temp1 - (temp1 * temp1 / 10000);}
      
  mySerial.print("RPM: ");
  mySerial.print(RPM);

  Serial.print("RPM: ");
  Serial.print(RPM);

  mySerial.print("\tCurrent(mA): "); // shows the voltage measured
  mySerial.print(current_mA, 3); // the '3' after voltage allows you to display 3 digits after decimal point

  Serial.print("\tCurrent(mA): "); // shows the voltage measured
  Serial.println(current_mA, 3); // the '3' after voltage allows you to display 3 digits after decimal point
  
  mySerial.print("\tPower(W): ");
  mySerial.println(power_mW/1000);

  //Serial.print("\tRawAnalog: ");
  //Serial.println(analogRead(analogIn));
  delay(1500);
}
