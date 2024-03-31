#!/bin/bash

echo "Running: postInstall..."

set +e

#############################
## Restore Properties File ##
#############################
if /usr/bin/test -f /tmp/alert-gateway.properties; then
    echo "Restoring the preserved application property file..."
    /usr/bin/mv /tmp/alert-gateway.properties /opt/alert_gateway/config/application.properties
    echo "Complete."
fi

####################################################################
## Modify ownership of the application's directories and files... ##
####################################################################
echo "Modifying ownership of application directory and contents..."
/usr/bin/chown -R alert-gateway:alert-gateway /opt/alert_gateway 2>/dev/null
echo "Complete."

#############################
## Reload systemctl daemon ##
#############################
echo "Reloading systemctl daemon..."
/usr/bin/systemctl daemon-reload
echo "Complete."

#################################
## Enable AlertGateway service ##
#################################
echo "Enabling the alert-gateway service..."
/usr/bin/systemctl enable alert-gateway
echo "Complete."

########################################
## Start service if was running prior ##
########################################
echo "Starting the alert-gateway service..."
if /usr/bin/test -f /tmp/alert-gateway.start; then
    /usr/bin/systemctl start alert-gateway
fi
echo "Complete."

###########################
## Remove the flag files ##
###########################
echo "Removing flag files..."
/usr/bin/rm -f /tmp/alert-gateway.install
/usr/bin/rm -f /tmp/alert-gateway.start
echo "Complete."

set -e

echo "postInstall complete."

exit 0