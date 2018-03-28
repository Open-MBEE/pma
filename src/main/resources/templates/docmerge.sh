# docmerge job
set +x +e # x is for quieter logs. e is so the job can continue after a failure.

# Tell PMA that this job has started
status=running

echo fromRefId: $fromRefId

echo $JOB_NAME
echo $TEAMWORK_PROJECT

ticket='"'$ticket'"'
ticketKey='"ticket"' #ticket key for the pmaUpdateJSON
valueKey='"value"' #value key for the pmaUpdateJSON

param='"'$status'"'

pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_HOST})
echo $pmaResponse

sleep 10s

echo hello > DocMergeLog.txt

status=completed
param='"'$status'"'
pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_HOST})
echo $pmaResponse
