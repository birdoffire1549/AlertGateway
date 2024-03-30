#!/bin/bash

echo "Running: preInstall..."

###########################################
## Add a user the service will run as... ##
###########################################
check=0
for user in $(awk -F":" '{ print $1 }' /etc/passwd); do
    if [ "$user" = "alert-gateway" ] ; then
        echo "The alert-gateway user already exists; Skipping adding of user."
        check=1
    fi
done
if [ $check -eq 0 ] ; then
    echo "Adding the alert-gateway user..."
    /usr/sbin/useradd -r -m -U -d /opt/alert_gateway -s /bin/false alert-gateway
    echo "Complete."
fi

#####################################
## Drop alert-gateway.install file ##
#####################################
echo "Dropping install-flag file incase upgrading service..."
/usr/bin/touch /tmp/alert-gateway.install
echo "Complete."

##################################
## Move property file to safety ##
##################################
if /usr/bin/test -f /opt/alert_gateway/config/application.properties; then
    echo "Moving the application.properties file to preserve it..."
    /usr/bin/cp /opt/alert_gateway/config/application.properties /tmp/alert-gateway.properties
    echo "Complete."
fi
echo "preInstall complete."