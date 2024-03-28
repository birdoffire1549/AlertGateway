# AlertGateway
This is a REST Service which allows network devices to easily send alerts to email recipients via the Internet.

This service uses SpringBoot to publish various REST endpoints. Communication with these REST endpoints are done primarily using JSON. 

## Please Note:
This project is currently a work-in-progress. Even so, its basic functionality has been tested locally on my development machine and deemed to have the desired basic functionality in place and working. 

## Install
This service can be installed on a Debian based linux box by using the 'deb' file which can be downloaded from one of the Releases here:
https://github.com/birdoffire1549/AlertGateway/releases

**NOTE:** There is also a RPM available for download and install but I haven't tested it yet.

Once downloaded and transfered to your linux host, you can install it by using the following command:
`sudo dpkg -i ./packageName.deb`

Once installed the home directory for the application is:
/opt/alert_gateway

After install you will want to exit the 'application.properties' file so it has the proper settings for the SMTP server the gateway is supposed to connect to, as well as the proper port number the AlertGateway API will answer on.

After editing the 'application.properties' file you can enable the service using:
`systemctl enable alert-gateway`

Finally start the service by using:
`systemctl start alert-gateway`

The application will generate logs in the '/opt/alert_gateway/logs' directory under the filename 'application.log'. The logs are setup to roll-over daily and delete from the system after 7 days. 

## AlertGateway API
### Overview

| Endpoint | Method | Description | 
| ---- | ---- | ---- |
| / | GET | Information page for the service |
| /alert | POST | Used to send an alert |
| /alert/subscriptions | GET | Used to query subscriptions |
| /alert/subscriptions | POST | Used to Create/Modify/Delete subscriptions |

### API: '/' Root/Default Endpoint
The root path of the API will respond to a GET request with a HTML formatted text. The page returned is basically a Welcome Page with some basic information about the Service that is running on that port. Currently it responds with the name of the service: 'Alert Gateway' and the version of the service that is running. In future releases more information about the service, server or API may appear here.

### API: '/alert' Endpoint
**POST MEHTOD:**
This endpoint responds to the POST method and expects a JSON format known to this application as an Alert JSON.

Here is an example of the Alert JSON:
```
{
    "sourceAddress": "192.168.1.25",
    "sourceName": "WaterDetector",
    "date": "202403240044",
    "dateMask": "yyyymmddHHMMss",
    "severity": 1,
    "message": "A system malfunction was detected!"
}
```


