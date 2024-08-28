<head><title>Internationalization FAQ</title></head>

# Internationalization FAQ

### Where my json files should be created?
In your source project (your UI rpm), you should create your files in:

```text
YOUR_APP_ROOT/locales/en-us/YOUR_APP_NAME
```

![Network Explorer Structure](example-netex-ui-folders.png)

Please keep in mind that you should provide with your UI rpm only the default locale (en-us).

At deployment time, these files must be copied to **/ericsson/tor/data/apps/YOUR_APP/locales/en-us/**
You need to make sure your RPM copies your files to this location in the server, otherwise yout locales can't be found by the service.

**Important:** the files must be created in the server with **jboss_user** as owner and **jboss** as group to be acessible by the server side service.


### How can i include additional locales for my application?
Any locale different than en-us, will be included in a separate RPM. The process to create these RPMs is out of scope of Presentation Server.
Is just important top know that the localized files (app.json and app_actions.json) must be located in the same
folder structure (/ericsson/tor/data/apps/**YOUR_APP**/locales/**NEW_LOCALE**) and the same permissions mentioned in the previous question.


### I have created my localization files, but my application is still in english. What is the problem?
You should check each of these steps before reporting any bug on Presentation Server or Launcher.

* Your localization files (app.json and app_actions.json) are located on the server at the folder **/ericsson/tor/data/apps/YOUR_APP/locales/en-us/** ?
* Your localization files were created with the ownership set to **user:** jboss_user; **group:** jboss ?
* Have you checked if you json files contents are compliant with the [expected structure](localization-howto.html)?
* Have you waited 60 seconds for your change to be picked by the server? There's a delay of 60 seconds for the server to update the localization files.
* Have you checked if your browser locale is set to the locale you are looking for? Check the header "Accept-Language" in the REST call.
* The Accept Language header has the locations in the right order? This header supports a list of locales that will be used to set the user preference.
The server side service will return the first locale in this list with localization available.

### My application has a title, description acronym or action shown in blank. What should i do?
Check if you json files contents are compliant with the [expected structure](localization-howto.html) and all attributes have a value.
If you left something blank or don't declare an attribute (e.g. title) this will be show as an empty text in the UI.

### Is there any way to test my changes in the server without install my RPM again?
Yes, you can manually update the files in the filesystem in the locations specified in this documentation. 

This can be used for testing only. Please remember to update your RPM to properly install your updated file.
