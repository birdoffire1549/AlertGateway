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

##################################
## Remove the install-flag file ##
##################################
echo "Removing the install-flag file..."
/usr/bin/rm -f /tmp/alert-gateway.install
echo "Complete."

set -e

echo "postInstall complete."

exit 0