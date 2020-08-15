#include <ESP8266WiFi.h>

const char* ssid = "WIFI";
const char* password = "PASSWORD";

unsigned long lastWatering;    // no data about last watering
unsigned long timeSinceLastWatering;

unsigned int soilHumidity;
unsigned int soilHumidityH;
unsigned int soilHumidityL;

unsigned int airTemperature;
unsigned int airHumidity;

WiFiServer server(1234);

String header;
String getInfo = "GET /info";
String waterNow = "GET /water";
char message[100];
unsigned long currentTime = millis();
unsigned long previousTime = 0; 
const long timeoutTime = 2000;

void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }

  server.begin();
  
  lastWatering = 0;
}

void loop(){  
  if (Serial.available() > 3) {
    soilHumidityH = Serial.read();
    soilHumidityL = Serial.read();
    airTemperature = Serial.read();
    airHumidity = Serial.read();    

    //flush serial
    while(Serial.available()){Serial.read();}
    
    if (soilHumidityH == 128 && soilHumidityL == 128) {
      lastWatering = millis();
    } else {
      soilHumidity = (soilHumidityH << 7) + soilHumidityL;
    }
  } 
  
  // Listen for incoming clients
  WiFiClient client = server.available();

  if (client) {
    String currentLine = "";
    
    currentTime = millis();
    previousTime = currentTime;
    
    while (client.connected() && currentTime - previousTime <= timeoutTime) {
      // loop while the client's connected
      currentTime = millis();
              
      if (client.available()) {
        // if there's bytes to read from the client,
        char c = client.read();
        header += c;
        
        if (c == '\n') {
          if (currentLine.length() == 0) {
            if (header.startsWith(getInfo)) {
              // HTTP header
              client.println("HTTP/1.1 200 OK");
              client.println("Content-type:application/json");
              client.println("Connection: close");
              client.println();
                         
              // HTTP body
              if (lastWatering == 0) {
                timeSinceLastWatering = 0;
              } else {
                timeSinceLastWatering = millis() - lastWatering; 
              }            
              sprintf(message, "{\"soilHumidity\":\"%u\",\"airHumidity\":\"%u\",\"airTemperature\":\"%u\",\"status\":\"%u\"}", soilHumidity, airHumidity, airTemperature, timeSinceLastWatering);
              client.println(message);
                          
              // The HTTP response ends with another blank line
              client.println();
            }
            else if (header.startsWith(waterNow)) {
              Serial.write(255);
              
              // HTTP header
              client.println("HTTP/1.1 200 OK");
              client.println("Content-type:text");
              client.println("Connection: close");
              client.println();
                         
              // HTTP body
              client.println("Plant watered successfully!");
                          
              // The HTTP response ends with another blank line
              client.println();
            }
            
            // Break out of the while loop to close connection
            break;
          } else {
            // if you got a newline, then clear currentLine
            currentLine = "";
          }
        } else if (c != '\r') {
          // if you got anything else but a carriage return character, add it to the end of the currentLine
          currentLine += c;
        }
      }
    }
    
    // Clear the header variable
    header = "";
    
    // Close the connection
    client.stop();
  }
}
