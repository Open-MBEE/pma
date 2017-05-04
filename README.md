# pma
## Platform for Model Analysis

This is a Spring-Boot project that uses Maven.

To run the project:

```bash
mvn spring-boot:run
```

To package the project

```bash
mvn package
```
The resulting files will be located inside of the target directory.

## Deployment
ssh into server
java -jar pma.jar &

A log file will be generated when pma starts. File will be called pma.log

The endpoint /dbUpdate will need to be called to pass in jenkins credentials. 
