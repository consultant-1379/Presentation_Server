<head><title>How to translate your applications on Launcher</title></head>

# How to translate your applications on Launcher

There are two localized entities supported by Launcher/Presentation Server. 
Each one of these entities use a different file for localization:
 
1. **Application:** app.json (same one used by UI-SDK)
2. **Actions:** app_actions.json

These files need to be available for Presentation Server to be imported to the applications cache. 
To make these files available, you need to make sure your RPM installation copies the files to the following location:

![Locales Structure](locales-structure.png)

**Important Notes**

* app.json is mandatory for each application, while app_actions.json should be used only if the application provides actions.
* One app.json and one app_actions.json file should be available for each supported locale.
* The application directory should be the same used to provide [application metadata](addAppToLauncher.html).

**Network Explorer Example**

![Network Explorer Example](example-netex-locales.png)

## app.json contents

```json
{
  "title": "My Application 01",
  "shortInfo": "This is a brief description of My Application",
  "acronym": "APP01"
}
```
* title: Localized title for the application.
* shortInfo: Localized description for the application.
* acronym: Localized acronym for the application.

![Locales Texts](locale-texts.png)

## app_actions.json contents

```json
{
  "app01-action-01": {
    "label": "First Action"
  },
  "app01-action-02": {
    "label": "Second Action"
  }
}
```

This json file is composed by an object with all the available actions as attributes, and a label attribute for each action.
This label will be used on the menus and buttons where the action is provided.

Please refer to our [FAQ](localization-faq.html) for other commons questions.

## Example

You can use as example the [code review](https://gerrit.ericsson.se/#/c/2225278/) for Network Explorer to adopt internationalization in Launcher.