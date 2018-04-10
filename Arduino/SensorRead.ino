uint8_t pinA = 2;
uint16_t Ton = 0;
uint16_t Toff = 0;
float freq = 0.0;
uint32_t temp1 = 0; 
uint32_t temp2 = 0;
uint8_t i = 0; 
uint8_t PPR = 210;
uint16_t RPM = 0;
int pwmPin = 11;
int pwmValue = 0;
int divisor = 1;
int rawPWM = 0;
int dutyCycle = 0;
const int analogIn = A0;
int mVperAmp = 66; // use 185 for 5A Module, use 100 for 20A Module and 66 for 30A Module
unsigned int RawValue = 0; // can hold upto 64 10-bit A/D readings
int ACSoffset = 2497;
double Voltage = 0;
double Amps = 0;
uint8_t count = 64;

void setPwmFrequency(int pin, int divisor);

void setup() {
  // put your setup code here, to run once:
  pinMode(pinA,INPUT);
  setPwmFrequency(pwmPin, divisor);
  Serial.begin(115200);
  dutyCycle = 0;  // Change this 0 - 100%
  pwmValue = 255;
  
  if ((pwmValue >= 13) && (pwmValue < 26))
  {
    divisor = 1024;
    setPwmFrequency(pwmPin, divisor);
    analogWrite(pwmPin, pwmValue);
    delay(100);
    
    divisor = 1;
    setPwmFrequency(pwmPin, divisor);
  }
}

void loop() {
  temp1 = 0;
  temp2 = 0;
  RawValue = 0; // reset value
  
  //rawPWM = 255 * dutyCycle / 100;
  //analogWrite(pwmPin, pwmValue);
  
  for(i = 0; i < count; i++)
  {
    Ton = pulseIn(pinA, HIGH); // In microseconds
    temp1 = temp1 + Ton;
    
    Toff = pulseIn(pinA, LOW); // In microseconds
    temp2 = temp2 + Toff;

    RawValue = RawValue + analogRead(analogIn); // add each A/D reading to a total
  }
  Ton = temp1 / count;
  Toff = temp2 / count;
  freq = 1 / (float) (Ton + Toff) * 1000;
  RPM = 60 * freq / PPR * 1000;

  RawValue = RawValue / count;
  Voltage = (RawValue / 1024.0) * 5000; // Gets you mV
  Amps = ((Voltage - ACSoffset) / mVperAmp);

  /*Serial.print("Raw Value = " ); // shows pre-scaled value
  Serial.print(RawValue / 64);
  Serial.print("\t mV = "); // shows the voltage measured
  Serial.print(Voltage, 3); // the '3' after voltage allows you to display 3 digits after decimal point*/
  
  /*Serial.print("Ton: ");
  Serial.print(Ton);
  Serial.print(" uSec");
  
  Serial.print("\tToff: ");
  Serial.print(Toff);
  Serial.print(" uSec");
  
  Serial.print("Freq: ");
  Serial.print(freq);
  Serial.print(" KHz");

  Serial.print("\tDuty Cycle: ");
  Serial.print(dutyCycle);
  Serial.print("\tRaw PWM Value: ");
  Serial.print(rawPWM);
  Serial.print("\tMapped PWM Value: ");
  Serial.print(pwmValue);*/
  
  Serial.print("RPM: ");
  Serial.print(RPM);

  //Serial.print("\tFade: ");
  //Serial.print(fadeValue);
  Serial.print("\t\tAmps = "); // shows the voltage measured
  Serial.println(Amps, 3); // the '3' after voltage allows you to display 3 digits after decimal point
  
  //delay(1000); 
}


void setPwmFrequency(int pin, int divisor)
{
  byte mode;
  if(pin == 5 || pin == 6 || pin == 9 || pin == 10) 
  {
    switch(divisor) 
    {
      case 1: mode = 0x01; break;
      case 8: mode = 0x02; break;
      case 64: mode = 0x03; break;
      case 256: mode = 0x04; break;
      case 1024: mode = 0x05; break;
      default: return;
    }
    if(pin == 5 || pin == 6) 
    {
      TCCR0B = TCCR0B & 0b11111000 | mode;
    } 
    else 
    {
      TCCR1B = TCCR1B & 0b11111000 | mode;
    }
  } 
  
  else if(pin == 3 || pin == 11) 
  {
    switch(divisor) 
    {
      case 1: mode = 0x01; break;
      case 8: mode = 0x02; break;
      case 32: mode = 0x03; break;
      case 64: mode = 0x04; break;
      case 128: mode = 0x05; break;
      case 256: mode = 0x06; break;
      case 1024: mode = 0x07; break;
      default: return;
    }
    TCCR2B = TCCR2B & 0b11111000 | mode;
  }
}

