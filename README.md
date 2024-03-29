# AlertGateway Overview
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

# AlertGateway API
## Endpoints Overview

| Endpoint | Method | Description | 
| ---- | ---- | ---- |
| / | GET | HTML Information page for the service |
| /alert | POST | Used to send an alert |
| /alert/subscriptions | GET | Used to query subscriptions |
| /alert/subscriptions | POST | Used to Create/Modify/Delete subscriptions |

## API Endpoint: '/'
The root path of the API will respond to a GET request with a HTML formatted text. The page returned is basically a Welcome Page with some basic information about the Service that is running on that port. Currently it responds with the name of the service: 'Alert Gateway' and the version of the service that is running. In future releases more information about the service, server or API may appear here.

## API Endpoint: '/alert'
### POST Method
This endpoint responds to the POST method and expects a JSON format known to this application as an [Alert JSON](#alert-json).

When sending alerts it is good to keep in mind, that alerts are only sent out to subscribers who are subscribed to the particular 'sourceName' and 'severity'. That means the system will accept and drop alerts which have nowhere to be delivered.

One of the fields in the Alert JSON is 'severity' and the value it stores should be one of the following Severity Levels...

#### Severity Levels

| Value | Severity | 
| ---- | ---- |
| 1 | TEST |
| 2 | INFORMATIONAL |
| 3 | WARNING |
| 4 | CRITICAL |
| 5 | EMERGENCY |

#### Response
When a POST is made to the '/alert' endpoint the response will be in the form of an [Action Response JSON](#action-response-json).

The value returned for Response Type will be one of the following...

##### Response Types

| Value | Description | 
| ---- | ---- |
| 1 | ACTION_SUCCESSFUL | 
| 2 | ACTION_FAILED |

## API Endpoint: '/alert/subscriptions'
This endpoint is multifaceted in its capabilities. It can be used to query for existing subscriptions or it can be used to Add, Modify or Delete Subscriptions.

### GET Method
When interacting with this endpoint using the GET METHOD one can query for existing subscriptions. The most basic type of query is to simply hit the endpoint with the basic GET METHOD request. The response to the query will be a JSON response which contains an Array/List of all known Subscriptions. Subscriptions use the [Subscription JSON](#subscription-json) format.

So, a little more on the 'severityMask' field may be needed... The 'severityMask' field is a number that can represent one or more Severity Levels. This is achieved by first raising 2 to the power of the Severity Level's value, and then adding each of the calculated values together, the result is a single numeric value which is the 'severityMask'. Below is an example...

Example for calculating the 'severityMask' for a subscription to TEST(1), INFORMATIONAL(2), and EMERGENCY(5) would be as follows:
```
TEST ....... : 2^1 = 2
INFORMATIONAL: 2^2 = 4
EMERGENCY .. : 2^5 = 32

2 + 4 + 32 = 38 
```

Calculating the 'severityMask' this way allows for a Severity to be tested for a match against the 'severityMask' by simply performing a logical 'AND' between the two values, if the result is non-zero then it is a match.

#### Using Parameters
The GET method sent to this endpoint can also be supplied with one or more of the following parameters to help find a specific Subscription if it exists:

| Parameter | Description |
| ---- | ---- |
| email | The email address of the Subscriber to search for. |
| sourceName | The 'sourceName' of the subscriptions to find. |

Here is an example of a query that would pull back the subscriptions for the email address 'someone@email.com':

`/alert/subscriptions?email=someone@email.com`

### Response
A query of this endpoint will result in a JSON response where all matching Subscriptions will be returned as a JSON List/Array of [Subscription JSON](#subscription-json).

# API JSON Objects
## Alert JSON

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
| Field | Description |
| ---- | ---- |
| sourceName | The information here is intended to be a kind of address or unique label for the device actually sending the Alert. This is a String field and its contents aren't verified. |
| sourceName | This is a String field and is also the name used to match with an email address for a subscription. The field is required but the value is not verified |
| date | This is a String field that is to contain date and/or time information of when the Alert occurred. The format of the data in this field is not important as long as it can be parsed using the supplied dateMask |
| dateMask | A String value which specified the format mask to be used when parsing the supplied date field. |
| severity | Numeric field corresponding to the severity level of the alert. More information on [Severity Levels](#severity-levels) below. |
| message | A String field for the message that is intended to be sent to the subscriber. |

## Action Response JSON

Here is what the Action Response JSON will look like:
```
{
    "type": 1,
    "message": "Action Successful",
    "addInfo": ""
}
```

| Field | Description | 
| ---- | ---- |
| type | A numeric field which indicates the response type. See [Response Types](#response-types) below for more. |
| message | A String field that contains a short message about the result of the action. |
| addInfo | A String that can sometime contain additional information. At times it could be details about the action that occurred or even a stack-trace that resulted from the action. |

## Subscription JSON

Here is an example of the Subscription JSON format:
```
{
    "email": "somebody@gmail.com",
    "sourceName": "WaterDetector",
    "severityMask": 6,
    "id": "somebody@gmail.com:waterdetector"
}
```
| Field | Description |
| ---- | ---- |
| email | The subscriber's email address. |
| sourceName | The sourceName the subscriber has subscribed to. |
| severityMask | A numeric value which acts as a mask for all of the Severity Levels the subscriber is interested in. |
| id | This field is a unique identifier that the system has assigned to the subscription. |
