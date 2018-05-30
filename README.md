## Ping-application

##### The application was tested for Windows and Mac OS using jdk 8.

In order to run it just execute:

- mvn clean install
- java -jar target/ping-application-1.0-SNAPSHOT.jar http://www.google.com,www.uol.com.br
- or to get hosts from application.properties: java -jar target/ping-application-1.0-SNAPSHOT.jar

 __In an Internet browser or another client like postman or soapUI:__
http://localhost:8090/report
http://localhost:8090/report/all