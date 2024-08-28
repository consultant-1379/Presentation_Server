<head><title>UI Settings</title></head>

# UI settings

UI settings is an API that is exposed to store the settings of the UI components that need to be persisted on the service side.

To be eligible to be stored the setting should be:

1. UI-related (should constitute a property of the UI component and not a service-side configuration)
2. Small enough (recommended size is less than 1K characters, the max size is 5K characters. Setting sizes are monitored, **oversized settings will mean a TR will be created with the request to bring the setting size down**)
3. Not eligible to be stored anywhere else in a more specialized storage (e.g. on a client side, in the server-side storage that suits more etc.)

* Storing of the JSON structures and other entities that could grow uncontrollable is not recommended.

## How the settings are stored

The settings are stored in a key-value way, key being the combination of the application name, username and the setting name proper.

E.g. application=netex,user=administrator,setting=searchButtonVisible,value=false

## Settings limits and constraints

All keys and values are strings. Application should be a valid application, the user should be a valid user.

Current maximum value for the setting is 5000 characters.

**Saving an oversized setting will mean a TR will be created with the request to bring the setting size down**

### What if I need to store the setting that exceeds the maximum limit?

You should call a meeting with team guardians for UI Settings to reach an agreement on the maximum size.

Be prepared to answer these questions:
1. Are the data you want to store valid UI settings? Are they UI-related? Should they be stored on service side?
2. Why do you need to store such a big UI setting as a single entity? Could it be broken down to smaller pieces logically?
3. Is there a service (now or planned) that could alternatively store your data? Should there be one if it's not planned yet?
4. Could this data grow larger over time and put at risk of reaching max limit? What could be done to prevent this?

