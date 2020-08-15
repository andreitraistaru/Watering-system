#include <DHT.h>

int led = 2;

unsigned int soilHumidity;
unsigned int soilHumidityH;
unsigned int soilHumidityL;

int enablePump = 3;
int pumpIn1 = 4;
int pumpIn2 = 5;

unsigned int airTemperature = 0;
unsigned int airHumidity = 0;
DHT dht(13, DHT11);

unsigned int code;

void setup() {
  Serial.begin(9600);

  dht.begin();

  pinMode(led, OUTPUT);

  pinMode(enablePump, OUTPUT);
  pinMode(pumpIn1, OUTPUT);
  pinMode(pumpIn2, OUTPUT);

  digitalWrite(led, HIGH);

  digitalWrite(enablePump, LOW);
  digitalWrite(pumpIn1, LOW);
  digitalWrite(pumpIn2, LOW);
}

void waterPlant(int timer) {
  Serial.write(128);
  Serial.write(128);
  Serial.write(airTemperature);
  Serial.write(airHumidity);
    
  digitalWrite(enablePump, HIGH);
  digitalWrite(pumpIn1, HIGH);
  digitalWrite(pumpIn2, LOW);

  int timePassed = 0;

  while (timePassed < timer) {
    digitalWrite(led, LOW);

    delay(500);

    digitalWrite(led, HIGH);

    delay(500);

    timePassed += 1000;
  }

  digitalWrite(enablePump, LOW);
  digitalWrite(pumpIn1, LOW);
  digitalWrite(pumpIn2, LOW);

  digitalWrite(led, HIGH);
}

void loop() {
  if (Serial.available() > 0) {
    code = Serial.read();

    //flush serial
    while(Serial.available()){Serial.read();}

    if (code == 255) {
      waterPlant(2000);
    }
  }
  
  airTemperature = dht.readTemperature();
  airHumidity = dht.readHumidity();

  soilHumidity = analogRead(A5);
  soilHumidityL = soilHumidity % 128;
  soilHumidityH = soilHumidity >> 7;

  if (soilHumidity > 550) {
    waterPlant(4000);    
  }

  Serial.write(soilHumidityH);
  Serial.write(soilHumidityL);
  Serial.write(airTemperature);
  Serial.write(airHumidity);

  delay(3000);
}
