
# AppDynamics IoT Java SDK

## Table of Contents

- [Overview](#overview)
- [What is included](#what-is-included)
- [Getting Started](#getting-started)
- [Download the released jar](#download-the-released-jar)
- [Building the SDK](#build-the-sdk)
- [Building the Javadocs](#build-the-javadocs)
- [Documentation](#documentation)
- [Versioning](#versioning)
- [Copyright and License](#copyright-and-license)

## Overview

This repository contains the AppDynamics IoT Java SDK that can be used in embedded applications to monitor network perfomance, errors, and business metrics.


## What is Included

* `sdk` - The Appdynamics Java SDK for IoT.
* `sample-apps` - A sample application showing how to use the SDK.
* `docs` - The Javadoc for the SDK. It can also be [viewed online](https://appdynamics.github.io/iot-java-sdk/).


## Getting Started

The best way to understand how to use the SDK is to run the sample application.

1. Clone or download the repo https://github.com/Appdynamics/iot-java-sdk.git in `<MY_SOURCE_FOLDER>`.
 
1. Get an [EUM App Key and a Collector URL](https://docs.appdynamics.com/display/latest/Set+Up+and+Access+IoT+Monitoring#SetUpandAccessIoTMonitoring-iot-app-key).

1. Add your EUM app key and Collector URL to your sample app by making the following edits to 
`<MY_SOURCE_FOLDER>/iot-java-sdk/sample-apps/src/main/java/com/appdynamics/iotapps/MyIoTSampleApp.java`: 

	```java
	public static final String APP_KEY = "<YOUR-APP-KEY>";  
	public static final String COLLECTOR_URL = "<YOUR-COLLECTOR-URL>";
	```
	
1. Build the sample-app by running the following commands. (It also builds the SDK.) 
	```bash
	cd <MY_SOURCE_FOLDER>/iot-java-sdk
	./gradlew -p sample-apps clean distTar
	```

1. Create a new folder where you'll run the deployed code.  
	```bash
	mkdir <MY_DEPLOY_FOLDER>
	```

1. Copy the distribution tar file that includes the sample app and the AppDynamics IoT Java SDK to this deployment folder.    
	```bash
	cp ./sample-apps/build/distributions/appd-iot-sample-app.tar <MY_DEPLOY_FOLDER>
	```

1. Un-tar and run the application.  

	```bash
	cd <MY_DEPLOY_FOLDER> 
	tar xvf appd-iot-sample-app.tar  
	./appd-iot-sample-app/bin/sample-apps
	```

1. Confirm the [IoT Data](https://docs.appdynamics.com/display/latest/Confirm+the+IoT+Application+Reported+Data+to+the+Controller) was
reported to the EUM Collector.

## Download the Released JAR 
The released version of the SDK can be downloaded from https://github.com/Appdynamics/iot-java-sdk/releases.

## Build the SDK

1. Clone or download the repo https://github.com/Appdynamics/iot-java-sdk.git in `<MY_SOURCE_FOLDER>`.
1. Make any modifications to the SDK, if desired.
1. Run the following command from a terminal:
	```bash
	cd <MY_SOURCE_FOLDER>/iot-java-sdk 
	./gradlew -p sdk/ clean assemble test
	```

## Build the Javadocs

1. Clone or download the repo https://github.com/Appdynamics/iot-java-sdk.git in `<MY_SOURCE_FOLDER>`.
1. Make any modifications, if desired.
1. Run the following command from a terminal:
	```bash
	cd <MY_SOURCE_FOLDER>/iot-java-sdk  
	./gradlew -p sdk/ clean assemble test generateZippedJavadocs
	```
 
## Documentation

* [Javadoc](https://appdynamics.github.io/iot-java-sdk/) 
* [REST API docs](https://docs.appdynamics.com/javadocs/iot-rest-api/4.4/latest/) - provides the payload structure sent to the Appdynamics EUM Collector.
* [User Documents](https://docs.appdynamics.com/display/latest/IoT+Monitoring) - provides an overview about the IoT monitoring capabilities.
* Support for other languages:
    * [C++ SDK and Sample App](https://github.com/Appdynamics/iot-cpp-sdk)
    * [Python Sample Code](https://github.com/Appdynamics/iot-rest-api-sample-apps)


## Versioning
Versioning of releases of this project is maintained under the [Semantic Versioning Guidelines](https://semver.org/)

## Copyright and License

The code is released under the [Apache 2.0](https://github.com/Appdynamics/iot-java-sdk/blob/master/LICENSE) License. Copyright (c) 2018 AppDynamics LLC and its affiliates.
