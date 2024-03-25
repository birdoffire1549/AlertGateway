#!/bin/bash

## Ensure service file is removed...
/usr/bin/rm -f /usr/lib/systemd/system/alert-gateway.service

## Remove the application's user...
/usr/sbin/userdel -r alert-gateway

## Ensure the application directories are removed...
/usr/bin/rm -rf /opt/alert_gateway