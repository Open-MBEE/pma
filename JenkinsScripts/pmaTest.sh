set +x

# Tell PMA that this job has started
status=running

line=$(sed -n '2p' /opt/local/jenkins/.netrc)
username=$(echo $line | awk '{print $2}')

line=$(sed -n '3p' /opt/local/jenkins/.netrc)
password=$(echo $line | awk '{print $2}')

IP=$(curl -X POST -H Content-Type:application/json --data "{"username":$username, "password":$password}" https://${MMS_SERVER}/alfresco/service/api/login)
echo "$IP"

echo $JOB_NAME
echo $TEAMWORK_PROJECT
pmaResponse=$(curl -X POST -H Content-Type:application/json --data "$IP" localhost:8080/projects/$TEAMWORK_PROJECT/refs/master/jobs/$JOB_NAME/instances/$BUILD_NUMBER/jobStatus/$status/${MMS_SERVER})
echo $pmaResponse

sleep 10s

status=completed
pmaResponse=$(curl -X POST -H Content-Type:application/json --data "$IP" localhost:8080/projects/$TEAMWORK_PROJECT/refs/master/jobs/$JOB_NAME/instances/$BUILD_NUMBER/jobStatus/$status/${MMS_SERVER})
echo $pmaResponse
