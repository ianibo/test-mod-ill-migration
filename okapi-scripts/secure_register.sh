BASEDIR=$(dirname "$0")

# This script generates the module descriptor for mod-ill and posts it to a secured OKAPI control interface
# the script is controlled by a ~/.okapirc file where you need to specify the supertenant username (ST_UN)
# supertnent password (ST_PW) and the OKAPI_URL (For the rancher-desktop install, likely http://localhost:30100)

if [ -f .okapirc ]; then
  . .okapirc
elif [ -f $HOME/.okapirc ]; then
  . $HOME/.okapirc
else
  echo You must configure \$HOME/.okapirc
  echo export IS_SECURE_SUPERTENANT=Y
  echo export ST_UN=sysadm
  echo export ST_PW=PASSWORD_FROM_LOCAL_okapi_commander_cfg.json
  echo export OKAPI_URL=http://localhost:30100
  exit 0
fi

echo $BASEDIR
pushd "$BASEDIR/../service"

DIR="$BASEDIR/../"

echo "\nUsing directory $DIR"

# Check for decriptor target directory.

DESCRIPTORDIR="../service/build/resources/main/okapi"

if [ ! -d "$DESCRIPTORDIR" ]; then
    echo "No descriptors found. Let's try building them."
    
    ./gradlew generateDescriptors
fi

# DEP_DESC=`cat ${DESCRIPTORDIR}/DeploymentDescriptor.json | jq -c ".url=\"$2\""`
DEP_DESC=`cat ${DESCRIPTORDIR}/DeploymentDescriptor.json | jq -c ".url=\"http://${DEVELOPMENT_MACHINE}:${DEVELOPMENT_MACHINE_ILL_PORT}/\""`
SVC_ID=`cat ${DESCRIPTORDIR}/ModuleDescriptor.json | jq -rc '.id'`
INS_ID=`echo $DEP_DESC | jq -rc '.instId'`

AUTH_TOKEN=`../okapi-scripts/okapi-login -u $ST_UN -p $ST_PW -t supertenant`
echo Super: $AUTH_TOKEN
echo $ST_UN $ST_PW
AUTH_TOKEN_TENANT=`../okapi-scripts/okapi-login -u $UN -p $PW`
echo Tenant: $AUTH_TOKEN_TENANT
echo $UN $PW
pwd

# Quick hack to remove proxy tenants that have a timestamp at the end of them
#while read -r installedId
#do
#	echo Removing module ${installedId}
#	echo curl -XDELETE --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN}" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${installedId}"
#	curl -XDELETE --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN_TENANT}" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${installedId}"
#	echo
#done < ../okapi-scripts/modIds.txt
#exit

echo Unattaching module with tenants
# Get hold of all the versions associated with the tenant
echo curl -XGET --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN_TENANT}" "${OKAPI_URL}/_/proxy/tenants/${TENANT}/modules?limit=1000&filter=${SVC_ID}&latest=100"
EXISTING_TENANT_IDS=`curl -XGET --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN_TENANT}" "${OKAPI_URL}/_/proxy/tenants/${TENANT}/modules?limit=1000&filter=${SVC_ID}&latest=100" | jq -rc '.[].id'`	
if [ ! -z "${EXISTING_TENANT_IDS}" ]
then
	echo Existing Ids for tenant: ${EXISTING_TENANT_IDS}
	while read -r installedTenantIdentifier
	do
		echo Removing module ${installedTenantIdentifier}
		echo curl -XDELETE --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN}" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${installedTenantIdentifier}"
		curl -XDELETE --no-progress-meter -H "X-Okapi-Token: ${AUTH_TOKEN_TENANT}" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${installedTenantIdentifier}"
		echo
	done <<< "${EXISTING_TENANT_IDS}"
fi
	

echo Remove any existing module ${SVC_ID}/${INS_ID}
echo Waiting for curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${SVC_ID}"
echo
curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/tenants/${TENANT_NAME}/modules/${SVC_ID}"
echo

#echo Wauting for curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/tenants/test1Install/modules/${SVC_ID}"
#echo
#curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/tenants/test1Install/modules/${SVC_ID}"
#echo

echo Waiting for curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/discovery/modules/${SVC_ID}/${INS_ID}"
echo
curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/discovery/modules/${SVC_ID}/${INS_ID}"
echo

echo Waiting for curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/modules/${SVC_ID}"
echo
curl -XDELETE -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/proxy/modules/${SVC_ID}"
echo

# ./gradlew clean generateDescriptors
echo Install latest module ${SVC_ID}/${INS_ID} 
echo
curl -XPOST -H "X-Okapi-Token: $AUTH_TOKEN" ${OKAPI_URL}/_/proxy/modules -d @"${DESCRIPTORDIR}/ModuleDescriptor.json"

echo -e "\n\nPOSTING DEPLOYMENT DESCRIPTOR:"
curl -XPOST -H "X-Okapi-Token: $AUTH_TOKEN" "${OKAPI_URL}/_/discovery/modules" -d "$DEP_DESC"

popd
