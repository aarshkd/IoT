uint8_t pinA = 2;
uint8_t flag = 4;
uint8_t flagVal = 0;
uint16_t Ton = 0;
uint16_t Toff = 0;
uint32_t temp1 = 0; 
uint32_t temp2 = 0;
float freq = 0.0;

uint8_t PPR = 210;
uint32_t RPM = 0;
const int analogIn = A0;
int mVperAmp = 66; // use 185 for 5A Module, use 100 for 20A Module and 66 for 30A Module
unsigned int RawValue = 0; // can hold upto 64 10-bit A/D readings
int ACSoffset = 2497;
double Voltage = 0;
double Amps = 0;
uint8_t count = 30;
float power = 0;
float motorVin = 11.5;

void setPwmFrequency(int pin, int divisor);

void setup() {
  // put your setup code here, to run once:
  pinMode(pinA,INPUT);
  pinMode(flag,INPUT);
  digitalWrite(flag,LOW);
  Serial.begin(115200);
  /*
  RawValue = 0.0;
  for(uint8_t i = 0; i < 10; i++)
  {
    RawValue = RawValue + analogRead(analogIn);
  }
  RawValue = RawValue / 10;
  ACSoffset = (RawValue / 1024.0) * 5000; // Gets you mV*/
}

void loop() {
  temp1 = 0;
  temp2 = 0;
  RawValue = 0; // reset value
  
  for(uint8_t i = 0; i < count; i++)
  {
    Ton = pulseIn(pinA, HIGH); // In microseconds
    Toff = pulseIn(pinA, LOW); // In microseconds
    temp1 = temp1 + Ton;
    temp2 = temp2 + Toff;

    RawValue = RawValue + analogRead(analogIn); // add each A/D reading to a total
  }
  Ton = temp1 / count;
  Toff = temp2 / count;
  freq = 1 / (float) (Ton + Toff) * 1000;
  RPM = 60 * freq / PPR * 1000;
  temp1 = RPM;
  
  RawValue = RawValue / count;
  Voltage = (RawValue / 1024.0) * 5000; // Gets you mV
  Amps = ((Voltage - ACSoffset) / mVperAmp);
  power = motorVin * Amps;

  flagVal = digitalRead(flag);
  if(flagVal == 1){
    if (RPM > 300){  
      RPM = temp1 - (temp1 * temp1 / 10000);}}
    
  Serial.print("RPM: ");
  Serial.print(RPM);

  Serial.print("\tAmps = "); // shows the voltage measured
  Serial.print(Amps, 3); // the '3' after voltage allows you to display 3 digits after decimal point
  
  Serial.print("\tPower: ");
  Serial.println(power);
  delay(2000); 
}
