#!/bin/bash

echo "Running: postRemove..."

########################################################
## If not upgrading, remove the application's user... ##
########################################################
set +e
if ! /usr/bin/test -f /tmp/alert-gateway.install; then
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
else
    echo "Upgrade in-progress, application user is needed and won't be removed."
fi

set -e

echo "postRemove Complete."