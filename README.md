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

## Https requirements
By default, the server uses http.
Ssl certificate must be located in /etc/pki/certs/ 
Certificate must be called server.jks

## Deployment
scp the jar to the server
ssh into server

If https is needed these commands are required:
```
export ssl_key_password=sslKeyPassword
export ssl_key_alias=sslKeyAlias
```

java -jar pma.jar &

A log file will be generated when pma starts. File will be called pma.log

This JSON will need to be posted to the dbUpdate (ex: localhost:8443/dbUpdate) endpoint the first time it is deployed to store the credentials in the database. The cae org will be used by default. Other configurations for different orgs can be posted to this endpoint. Configurations will be overwritten for the org if it is sent again.
```
{
"username":"jenkinsUser",
"password":"jenkinsPassword",
"url":"jenkinsURL",
"agent":"jenkinsBuildAgentLabel",
"org":"cae"
}
```

Use the dbDelete (ex. localhost:8443/dbDelete) endpoint to delete an org's configuration. The JSON sent will be in this format:

```
{"org":"orgName"}
```


#Tests
To run spring-boot and junit tests type into the command line:
```bash
mvn test
```
These environment variables have to be set for the tests to be ran locally.

* ADMIN_USER (mms admin username)
* ADMIN_PASS (mms admin password)
* JENKINS_TEST_USER (Jenkins admin account)
* JENKINS_TEST_PASSWORD (Jenkins admin password)
