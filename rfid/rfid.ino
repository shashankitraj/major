#include <SPI.h>
#include <MFRC522.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>;
#include <ThingSpeak.h>;
  
#define WIFI_SSID "anuj" 
#define WIFI_PASSWORD "12345678"
unsigned long myChannelNumber = 1009719;
const char * myWriteAPIKey = "BG52FE0QWZPTOQ3R";
WiFiClient client;

#define RST_PIN         5         
#define SS_PIN          4      

MFRC522 mfrc522(SS_PIN, RST_PIN); 

void setup() {
	Serial.begin(9600);	
	while (!Serial);	
	SPI.begin();		
	mfrc522.PCD_Init();	
	delay(4);				
	mfrc522.PCD_DumpVersionToSerial();

  //wifi
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to ");
    Serial.print(WIFI_SSID);
    while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);   
    }
    Serial.println();
    Serial.println("Connected");

    ThingSpeak.begin(client);
}

void loop() {
	if ( ! mfrc522.PICC_IsNewCardPresent()) {
		return;
	}
	if ( ! mfrc522.PICC_ReadCardSerial()) {
		return;
	}

	Serial.print("UID tag :");
  String content= "";
  byte letter;
  for (byte i = 0; i < mfrc522.uid.size; i++) 
  {
     content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
     content.concat(String(mfrc522.uid.uidByte[i], HEX));
  }
  content.toUpperCase();
  content.trim();
  Serial.println(content);
  ThingSpeak.writeField(myChannelNumber, 1,content, myWriteAPIKey); 
     
  delay(500);
}
