# docmerge job
set +x +e # x is for quieter logs. e is so the job can continue after a failure.

# Tell PMA that this job has started
status=running

ticket='"'$ticket'"'

echo fromRefId: $fromRefId
echo comment: $comment
echo $JOB_NAME
echo $TEAMWORK_PROJECT

ticketKey='"ticket"' #ticket key for the pmaUpdateJSON
valueKey='"value"' #value key for the pmaUpdateJSON

param='"'$status'"'

pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_HOST})
echo $pmaResponse # Updating job status to running

param='"'$BUILD_NUMBER'"'
pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/buildNumber?mmsServer=${MMS_HOST})
echo pmaResponse $pmaResponse # Updating buildNumber to current

currentTime=$(date +%Y-%m-%dT%H:%M:%S)
param='"'$currentTime'"'
pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/started?mmsServer=${MMS_HOST})
echo pmaResponse $pmaResponse # Updating started time

projectIdKey='"projectId"'
docIdKey='"docId"'
toRefIdKey='"toRefId"'
fromRefIdKey='"fromRefId"'
mmsServerKey='"mmsServer"'
commentKey='"comment"'

projectId='"'$PROJECT_ID'"'
docId='"'$TARGET_VIEW_ID'"'
toRefId='"'$REF_ID'"'
fromRefId='"'$fromRefId'"'
mmsServer='"'https://$MMS_HOST'"'
comment='"'$comment'"'

docmergeServiceUrl="https://bwtj0li4ii.execute-api.us-gov-west-1.amazonaws.com/development/mms-merge-doc"

jsonBody="{$ticketKey:$ticket,$projectIdKey:$projectId,$docIdKey:$docId,$toRefIdKey:$toRefId,$fromRefIdKey:$fromRefId,$mmsServerKey:$mmsServer,$commentKey:$comment}"

curlResponse=$(curl -H "Content-Type: application/json" -d "$jsonBody" -XPOST $docmergeServiceUrl)

echo $curlResponse > DocMergeLog.txt

if [[ $curlResponse = *"200"* ]];
then
	status=Completed
  exitCode=0
else
	status=Failed
  exitCode=1
fi

echo buildStatus: $status
echo exitCode: $exitCode

param='"'$status'"'
pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA

pmaResponse=$(curl -X POST -H Content-Type:application/json --data $pmaUpdateJSON https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/jobStatus?mmsServer=${MMS_HOST})
echo $pmaResponse # Updating completion status

artifactLink=$BUILD_URL"artifact/DocMergeLog.txt"

param='"'$artifactLink'"'

pmaUpdateJSON="{$ticketKey:$ticket,$valueKey:$param}" #JSON to send to PMA
pmaResponse=$(curl -X POST -H Content-Type:application/json --data "$pmaUpdateJSON" https://$PMA_HOST:$PMA_PORT/projects/$PROJECT_ID/refs/$REF_ID/jobs/$JOB_BASE_NAME/instances/$BUILD_NUMBER/logUrl?mmsServer=${MMS_HOST})
echo pmaResponse $pmaResponse # Updating logUrl

exit $exitCode
