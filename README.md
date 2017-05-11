# pma
## Platform for Model Analysis

This is a Spring-Boot project that uses Maven.

To run the project:

```bash
mvn spring-boot:run
```

To package the project

```bash
mvn package -Dmaven.test.skip=true
```
The resulting files will be located inside of the target directory.

## Deployment
ssh into server
java -jar pma.jar &

A log file will be generated when pma starts. File will be called pma.log

This JSON will need to be posted to the dbUpdate endpoint the first time it is deployed to store the credentials in the database.
```
{
"username":"jenkinsUser",
"password":"jenkinsPassword",
"url":"jenkinsURL",
"agent":"jenkinsBuildAgentLabel"
}
```
#Tests
To run spring-boot and junit tests type into the command line:
```bash
mvn test
```
