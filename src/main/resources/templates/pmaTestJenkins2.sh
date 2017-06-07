# Example test job
set +x

# Tell PMA that this job has started
status=running

line=$(sed -n '2p' /opt/local/jenkins/.netrc)
username=$(echo $line | awk '{print $2}')

line=$(sed -n '3p' /opt/local/jenkins/.netrc)
password=$(echo $line | awk '{print $2}')

ticket=$(curl -X POST -H Content-Type:application/json --data "{"username":$username, "password":$password}" https://${MMS_SERVER}/alfresco/service/api/login)
echo $ticket

echo $JOB_NAME
echo $TEAMWORK_PROJECT

ticketKey='"ticket"' #ticket key for the pmaUpdateJSON
valueKey='"value"' #value key for the pmaUpdateJSON

ticket=$(echo $ticket | tr -d '\r') #Removing new lines
ticket=${ticket#*'ticket":'} #Removing everything until ticket:"
ticket=${ticket%' } }'} #Removing the last two brackets

param='"'$status'"'

pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://cae-pma-test:8443/projects/$TEAMWORK_PROJECT/refs/master/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_SERVER})
echo $pmaResponse

sleep 10s

status=completed
param='"'$status'"'
pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://cae-pma-test:8443/projects/$TEAMWORK_PROJECT/refs/master/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_SERVER})
echo $pmaResponse
