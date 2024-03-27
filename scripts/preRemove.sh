#!/bin/bash

echo "Running: preRemove..."

set +e

#######################################
## Stop the alert-gateway service... ##
#######################################
x=$(ps -ef | grep alert-gateway | grep -v grep | wc -l)
if [ $x -gt 0 ] ; then
    echo "Attempting to stop the alert-gateway service..."
    /usr/bin/systemctl stop alert-gateway
    echo "Complete."
fi

##########################################
## Disable the alert-gateway service... ##
##########################################
echo "Attempting to disable the alert-gateway service..."
/usr/bin/systemctl disable alert-gateway 2>/dev/null
echo "Complete."

######################################
## Remove the application's user... ##
######################################
check=0
for x in $(awk -F":" '{ print $1 }' /etc/passwd ); do
    if [ "$x" = "alert-gateway" ] ; then
        check=1
    fi
done
if [ $check -eq 1 ] ; then
    echo "Removing the alert-gateway user..."
    /usr/sbin/userdel -r alert-gateway 2>/dev/null
    echo "Complete."
fi

set -e 

echo "preRemove complete."