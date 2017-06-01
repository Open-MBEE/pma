# docgen job
set +x +e # x is for quieter logs. e is so the job can continue after a failure.

export MAGICDRAW_HOME="/opt/local/MD"

# Tell PMA that this job has started
status=Running

line=$(sed -n '2p' /opt/local/jenkins/credentials/pma.properties)
MMS_PORT=$(echo $line | awk '{print $2}')

line=$(sed -n '3p' /opt/local/jenkins/credentials/pma.properties)
MMS_USERNAME=$(echo $line | awk '{print $2}')

line=$(sed -n '4p' /opt/local/jenkins/credentials/pma.properties)
MMS_PASSWORD=$(echo $line | awk '{print $2}')

line=$(sed -n '5p' /opt/local/jenkins/credentials/pma.properties)
TWC_HOST=$(echo $line | awk '{print $2}')

line=$(sed -n '6p' /opt/local/jenkins/credentials/pma.properties)
TWC_PORT=$(echo $line | awk '{print $2}')

line=$(sed -n '7p' /opt/local/jenkins/credentials/pma.properties)
TWC_USERNAME=$(echo $line | awk '{print $2}')

line=$(sed -n '8p' /opt/local/jenkins/credentials/pma.properties)
TWC_PASSWORD=$(echo $line | awk '{print $2}')

ticket=$(curl -X POST -H Content-Type:application/json --data "{"username":$MMS_USERNAME, "password":$MMS_PASSWORD}" https://${MMS_HOST}/alfresco/service/api/login)
#echo "$ticket"

echo jobName $JOB_NAME
echo twProject $TEAMWORK_PROJECT
pmaResponse=$(curl -X POST -H Content-Type:application/json --data "$ticket" $PMA_HOST/projects/$PROJECT_ID/refs/master/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus/$status?mmsServer=${MMS_HOST})
echo pmaResponse $pmaResponse


echo --mmsHost $MMS_HOST --mmsPort $MMS_PORT --mmsUsername $MMS_USERNAME --mmsPassword $MMS_PASSWORD \
 --twcHost $TWC_HOST --twcPort $TWC_PORT --twcUsername $TWC_USERNAME --twcPassword $TWC_PASSWORD \
 --projectId $PROJECT_ID --refId $REF_ID --targetViewId $TARGET_VIEW_ID --jobId $JOB_ID

DISPLAY=:9001
vncserver -kill "$DISPLAY" 2> /dev/null || true
vncserver "$DISPLAY" -SecurityTypes None
export DISPLAY

bash /opt/local/MD/analysis/automatedviewgeneration.sh \
--mmsHost $MMS_HOST --mmsPort $MMS_PORT --mmsUsername $MMS_USERNAME --mmsPassword $MMS_PASSWORD \
--twcHost $TWC_HOST --twcPort $TWC_PORT --twcUsername $TWC_USERNAME \
--twcPassword $TWC_PASSWORD --projectId $PROJECT_ID --refId $REF_ID \
--targetViewId $TARGET_VIEW_ID --generateRecursively

mdExitCode=$?
echo MDEXITCODE $mdExitCode

vncserver -kill "$DISPLAY"

if (($mdExitCode == 0))
then
	status=Completed
else
	status=Failed
fi

echo status $status

ticket=$(curl -X POST -H Content-Type:application/json --data "{"username":$MMS_USERNAME, "password":$MMS_PASSWORD}" https://${MMS_HOST}/alfresco/service/api/login)
#echo "$ticket"

pmaResponse=$(curl -X POST -H Content-Type:application/json --data "$ticket" $PMA_HOST/projects/$PROJECT_ID/refs/master/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus/$status?mmsServer=${MMS_HOST})
echo pmaResponse $pmaResponse
