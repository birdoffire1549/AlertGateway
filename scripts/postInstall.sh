#!/bin/bash

echo "Running: postInstall..."

####################################################################
## Modify ownership of the application's directories and files... ##
####################################################################
echo "Modifying ownership of application directory and contents..."
set +e
/usr/bin/chown -R alert-gateway:alert-gateway /opt/alert_gateway 2>/dev/null
set -e
echo "Complete."
echo "postInstall complete."

exit 0