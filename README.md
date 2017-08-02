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
### Init Script

Pma script is located $PMASourceFolder/src/main/resources/

pma script is called pma

Put the pma script inside /etc/init.d/

Run these commands inside /etc/init.d/

```
sudo chmod u+x pma
sudo chkconfig pma on
sudo chkconfig --list
```

#### New Server Installation

Create directory /opt/local/pma/

sudo chmod -R a+rw /opt/local/pma/ #so PMA can create log files in that directory

create credentials.txt inside /opt/local/pma/
Add this text inside.
```
sslKeyAlias $alias
sslKeyPassword $password
```
Replace $alias and $password with the ssl key alias and the ssl key password

save the file


Download the pma jar from artifactory to /opt/local/pma

To run PMA:

    sudo bash /etc/init.d/pma start



#### Updating PMA on server

Stop PMA:
```
    sudo bash /etc/init.d/pma stop
```
navigate to /opt/local/pma/

remove all pma jars.

Download the newest pma jar from artifactory to this directory

##### Run PMA:

    sudo bash /etc/init.d/pma start


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
