# iot-java-sdk
Appdynamics IoT Java SDK to monitor performance of embedded applications on connected devices
# AppDynamics IoT Java SDK

## Table of contents

- [Overview](#overview)
- [What is included](#what-is-included)
- [Getting Started](#getting-started)
- [Download the released jar](#download-the-released-jar)
- [Building the SDK](#building-the-sdk)
- [Building the Javadocs](#building-the-javadocs)
- [Documentation](#documentation)
- [Versioning](#versioning)
- [Copyright and License](#copyright-and-license)

## Overview
This repository contains AppDynamics IoT Java SDK that can be used in embedded applications to monitor network perfomance, errors and business metrics.


## What is included

* `sdk` - The Appdynamics Java SDK for IoT 
* `sample-apps` - A Sample Application showing how to use the SDK
* `docs` - The Javadoc for the SDK. It can also be viewed online [here](https://appdynamics.github.io/iot-java-sdk/)


## Getting Started

Best place to understand the usage of SDK is to run the sample application.

* Clone or download the repo `https://github.com/Appdynamics/iot-java-sdk.git` in `<MY_SOURCE_FOLDER>`
 
* Get [EUM App Key and Collector URL](https://docs.appdynamics.com/display/latest/Set+Up+and+Access+IoT+Monitoring#SetUpandAccessIoTMonitoring-iot-app-key)

* Add these to your sample app by editing.  
`<MY_SOURCE_FOLDER>/iot-java-sdk/sample-apps/src/main/java/com/appdynamics/iotapps/MyIoTSampleApp.java`  
`public static final String APP_KEY = "<YOUR-APP-KEY>";`   
`public static final String COLLECTOR_URL = "<YOUR-COLLECTOR-URL>";`  

* Build the sample-app by running the following commands.  
`cd <MY_SOURCE_FOLDER>/iot-java-sdk `  
`./gradlew -p sample-apps clean distTar` 

* Create a new folder from where to run the deployed code.  
`mkdir <MY_DEPLOY_FOLDER>`

* Copy the distribution tar file which includes the appdynamics iot java sdk to this deployment folder.    
`cp ./sample-apps/build/distributions/appd-iot-sample-app.tar <MY_DEPLOY_FOLDER>`

* Un-tar and run the application.  

	`cd <MY_DEPLOY_FOLDER> `  
	`tar xvf appd-iot-sample-app.tar`  
	`./appd-iot-sample-app/bin/sample-apps`  

* Confirm [IoT Data](https://docs.appdynamics.com/display/latest/Confirm+the+IoT+Application+Reported+Data+to+the+Controller) is
reported to Collector.

## Download the released jar 
The released version of the SDK can be downloaded from [here](https://github.com/Appdynamics/iot-java-sdk/releases)

## Building the SDK
* Clone or download the repo: https://github.com/Appdynamics/iot-java-sdk.git in `<MY_SOURCE_FOLDER>`
* Make any modifications to the sdk, if desired
* Run the following command from the terminal  
`cd <MY_SOURCE_FOLDER>/iot-java-sdk `
`./gradlew -p sdk/ clean assemble test `

## Building the Javadocs

* Clone or download the repo: https://github.com/Appdynamics/iot-java-sdk.git in `<MY_SOURCE_FOLDER>`
* Make any modifications, if desired
* Run the following command from the terminal
`cd <MY_SOURCE_FOLDER>/iot-java-sdk `  
`./gradlew -p sdk/ clean assemble test generateZippedJavadocs `
 
## Documentation

1. [Javadoc](https://appdynamics.github.io/iot-java-sdk/) 
2. [REST API docs](https://docs.appdynamics.com/javadocs/iot-rest-api/4.4/latest/) - provides payload structure sent to Appdynamics IoT Collector
3. [User Documents](https://docs.appdynamics.com/display/latest/IoT+Monitoring) - provides overview on IoT monitoring capabilities
4. Support for other languages
    * [C++ SDK and Sample App](https://github.com/Appdynamics/iot-cpp-sdk)
    * [Python Sample Code](https://github.com/Appdynamics/iot-rest-api-sample-apps)


## Versioning
Versioning of releases of this project is maintained under [the Semantic Versioning Guidelines](https://semver.org/)

## Copyright and license

Code released under the [Apache 2.0](https://github.com/Appdynamics/iot-java-sdk/blob/master/LICENSE) License. Copyright (c) 2018 AppDynamics LLC and its affiliates.
