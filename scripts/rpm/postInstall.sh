#!/bin/bash

## Modify ownership of the application's directories and files...
/usr/bin/chown -R alert-gateway:alert-gateway /opt/alert_gateway

## Enable the alert-gateway service...
/usr/bin/systemctl enable alert-gateway

## Start the alert-gateway service...
/usr/bin/systemctl start alert-gateway