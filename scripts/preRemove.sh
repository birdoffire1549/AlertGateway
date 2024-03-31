#!/bin/bash

echo "Running: preRemove..."

set +e

#######################################
## Stop the alert-gateway service... ##
#######################################
x=$(ps -ef | grep "/usr/bin/java -jar /opt/alert_gateway/alert-gateway.jar" | grep -v grep | wc -l)
if [ $x -gt 0 ] ; then
    echo "Attempting to stop the alert-gateway service..."
    if /usr/bin/test -f /tmp/alert-gateway.install; then
        /usr/bin/touch /tmp/alert-gateway.start
    fi
    /usr/bin/systemctl stop alert-gateway
    echo "Complete."
fi

##########################################
## Disable the alert-gateway service... ##
##########################################
echo "Attempting to disable the alert-gateway service..."
/usr/bin/systemctl disable alert-gateway 2>/dev/null
echo "Complete."

######################
## Reload systemctl ##
######################
echo "Reloading the systemctl daemon..."
/usr/bin/systemctl daemon-reload
echo "Complete."

set -e 

echo "preRemove complete."