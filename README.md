# Tools
* Download and install the Java Development Kit 11. You can download it from: [here][1]
* Download and install Maven 3. You can download it from: [here][2]
* Download and install Docker latest. You can download it from: [here][docker-download]
* Using Lombok, so it is recommended to add a Lombok plugin to your IDE, but not mandatory.

# Build
* Build the application with the following command in the root directory of project: **mvn clean install**
* You can build the application without tests too with the following command in the root directory of project: **mvn clean install -DskipTests**


# Start the application
* You can set up environment variables for customized credentials or system will be using the default(given in docker-compose file)
    * **FIELD_API_DB_USER** to set your db username
    * **FIELD_API_PASSWD** to set your db password
    * **FIELD_API_DB_NAME** to set your db name
    * **FIELD_API_PROFILE** to set up the configuration profile which you want run
* Execute **docker-compose up --build** command in the root directory of the project
* You can reach the application by the following url: **localhost** or by the following Ip-address **127.0.0.1**  [example][3]


[1]: https://www.oracle.com/technetwork/java/javase/downloads/5066655
[2]: https://maven.apache.org/download.cgi
[docker-download]: https://docs.docker.com/docker-for-windows/install/
[3]: http://localhost