#Managing applications on Launcher

Since the implementation of [this story](https://jira-nam.lmera.ericsson.se/browse/TORF-84877),
we have changed the way that applications are read from Launcher.

Any new modification on applications need now to follow these instructions:

##Application Metadata

All the application related metadata should now be declared in a JSON file inside a folder of the same name. 

The JSON file and folder name must also be the same as the id defined for your app as shown below.

* *Example*: `app01/app01.json`

Also with the inclusion of the Navigation Panel from [this story](https://jira-nam.lmera.ericsson.se/browse/TORF-296047), 
it is required that your applications id reflects the unique id as defined in your root application's UI config file.
This ensures that all children can be fetched to be shown in the panel.

* *Example*: If the root application is "pmiclistsubsciption", then the launcher metadata id should be "pmiclistsubscription".

When changing the ID of your application, it would not override the applications previous metadata as the file would be renamed with the ID change and 
so there may be multiple files with the same name for the application on the NFS. This may result in duplication applications.

If this is the case, then use the 'version' key to determine which is the most recent metadata to be taken and the deprecated file will be excluded

This file must respect this structure:


For an internal application that uses the ENM default host:

    {
        "id": "SomeENMApp",
        "name": "My Application 01",
        "shortInfo": "Brief description...",
        "acronym": "MYAPP-01",
        "path": "/#app01",
        "type": "web",
        "version": 1,
        "openInNewWindow": false,
        "resources": [
            "resource-01",
            "resource-02"
        ],
        "groups": [
            {
            "id": "group01",
            "name": "Group 01"
            }
        ]
    }
    

For the internal application that uses the host from default.properties:

    {
        "id": "app01",
        "name": "My Application 01",
        "shortInfo": "Brief description...",
        "acronym": "MYAPP-01",
        "path": "/#app01",
        "protocol": "secure",
        "port": 443,
        "host": "my-host",
        "type": "web",
        "version": 1,
        "openInNewWindow": false,
        "resources": [
            "resource-01",
            "resource-02"
        ],
        "groups": [
            {
            "id": "group01",
            "name": "Group 01"
            }
        ]
    }


For the external application:

    {
      "id": "my-app-external-google-maps",
      "name": "Sample Application External (Google Maps)",
      "shortInfo": "google maps launcher",
      "path": "/maps",
      "external": true,
      "externalHost": "www.google.ie",
      "protocol": "secure",
      "type": "web",
      "version": 1,
      "groups": [
        {
          "id": "group-01",
          "name": "Group 01"
        }
      ]
    }


###Fields
####Mandatory
* **id**: This must be a unique identifier for your application.
    * **<span style="color:red">N.B:</span>** Ensure that the id is the same as the UI application name defined in it's UI config file which you intend to link to launcher.
    * *Example*: `networkExplorer`
* **type**: Application type
    * `web` for web applications
    * `citrix` for citrix applications (deprecated)
* **path**: The path where your application is located.
    * *Example*: `/#networkexplorer`
* **groups**: The Launcher group id and name in which the application will be available. For details see the [FAQ](app-metadata-faq.html).

####Optional
* **name**: The name that should be displayed on Launcher for this application.
            If two applications exist with the same name, then the app with the higher version will be taken.
    * *Example*: `Network Explorer`
* **version**: Numeric sequential value to represent the metadata version.
* **shortInfo**: A brief description of your application that will be shown on the application tooltip.
    * *Example*:
        > Use Network Explorer to search and retrieve all Network Configuration Data.
          The returned data can be grouped into Collections or Saved searches to facilitate sharing and reuse.
* **acronym**: The application acronym.
    * *Example*: `SHM`
* **external**: Used to indicate that your application is outside of ENM and redirected from the container. Used with the Navigation Panel.
* **protocol**: The protocol used by your application. This is only required when your application uses a different host than the ENM default.
    * `secure` for HTTP protocol
    * `unsecure` for HTTPS protocol
* **host**: This refers to the variable name on global.properties containing the host
            address to your application. This is only required in cases where the application URL points to
            a different host than the ENM default. Most ENM applications don't require this.
    * *Example*: `esmon`
* **externalHost**: This allows you directly input the host address for an external host. This is used when you wish to link to an application outside enm
    * *Example*: `www.ericsson.com`
* **port**: Port number where your application is listening. This is only required in
            cases where the application URL points to a different port than the ENM default.
            Most ENM applications don't require this.
    * **Example**: `7881`
* **openInNewWindow**: Boolean value indicating if this application should be opened in a new window on the browser. If not provided the default value is _**false**_.
* **hidden**: Boolean value indicating if this application should be visible in the applications list. If not provided the default value is _**false**_.
* **resources**: All the resources allowed to view the application should be declared here.
    * **<span style="color:red">N.B:</span>** If the user does not have any of the resources declared here, the link will not be shown on Launcher.
    * **<span style="color:red">N.B:</span>** If no resource is declared, the application will be visible to any user.
    * *Example*: `searchExecutor`

###Brief explanation of the ENM hosts/PIB/SED

Where the host is added to the **ENM SED** (Site Engineering document) text file *(in a key=value format)* it is imported upon installation to 
the **global.properties**  which is used by the
**PIB** (platform integration bridge) which provides the communication to/from ENM Java services.

You can use the hostname from those properties to indicate which host you want your application to be on.

So to add the previously unknown hostname to the properties you need to change the SED.

For PIB docs see https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/PlatformIntegrationBridge/latest/PlatformIntegrationBridge-api/index.html  

###Metadata Location

The application metadata file needs to be placed in the following folder:

```/ericsson/tor/data/apps/<YOUR_APP_ID>/<YOUR_APP_ID>.json```

###Installing Metadata with your RPM

Please refer to [this guide](installing-metadata-files.html) for instructions on how to install your metadata.
