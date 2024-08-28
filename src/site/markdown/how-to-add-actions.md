<head><title>How to add or update actions provided by my application</title></head>

# How to add or update actions provided by my application

Action service allows applications to provide actions for other applications.

This guide is focused to show how you can publish an action to be provided by your application.

The action implementation by itself is out of the scope of this guide. 
This guide shows only the metadata definition for actions.

For the UI implementation, refer to the [Action Library Documentation](https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/applib/latest/actionLibrary.html)

## Updating/Creating the metadata

You will need to update two metadata files:

* Application Metadata: to include the actions provided by your application. Please refer to [this link](addAppToLauncher.html) 
for more information about application metadata.

* Action Rules: to define the rules that needs to be matched for your action be usable by the client application 
(e.g: multiple selections, managed object types, node types)  

### Application Metadata

Edit your application metadata file and include your action definition on the provideActions attribute. 
This attribute is an array, so you can provide as many actions as you need.

```json
{
  "id": "app01",
  "name": "My Application 01",
  "provideActions": [
      {
        "name": "app01-action-01",
        "defaultLabel": "[App01] Action 01",
        "multipleSelection": false,
        "plugin": "plugins/app01/action01-plugin.js",
        "primary": true,
        "category": "Security Management",
        "icon": "icon-01",
        "order": 1,
        "metadata" : [
          {
            "name": "textDisplayColour",
            "value": "purple"
          },
          {
            "name": "defaultItemsToDisplay",
            "value": "6"
          }
         ],
        "resources": [
          {
            "name": "resource-01",
            "action": "READ"
          },
          {
            "name": "resource-01",
            "action": "DELETE"
          }
        ]
      }
    ]
}
```
* **name**: Defines a name for your action. This name should be unique, so we recommend to include your 
application id as prefix.

* **defaultLabel**: Defines the default label that should be used on the UI for this action.

* **multipleSelection**: Indicates if your action is capable to handle multiple object selection.
If your action expects a single MO (e.g. action Lock Cell) set it to false. If your application

* **plugin**: Indicates the path to the plugin used to execute the action. This will be used by the client side to trigger the action execution.
Is important that this plugin is available when you deliver this metadata otherwise the action will be available but the execution will fail.

* **primary**: This attribute is optional and the default value is false. It indicates that this action is a primary action.

* **category**: Indicates on which category the action should be available in the menu. This attribute is mandatory.
 You should only use one of the supported categories otherwise your metadata will be considered invalid and will be ignored.
 Check the [FAQ](action-service-faq.html) to get the list of supported categories. 
 
* **icon**: This attribute is optional and it defines the keyword for the action button icon from the [available icons](https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/assets/latest/showcase/#ui-showcase/icons).

* **order**:This is a required attribute used to define in which position your action should be shown on the UI. Read more about action ordering [here](actions-ordering.html).

* **metadata**: This attribute is optional and may be used to pass additional properties to a plugin. "name" identifies the optional plugin property. "value" the value of the optional plugin attribute, a String.

* **resources**: This attribute is optional and can be used to define if an action should be available under RBAC constraints.
This means that is a resource/security action is provided here, the action will be visible if the user has any of the required
resource/security action combination.

  In the example provided above, the action will only be available for users with **READ** or **DELETE** permission on **resource-01**.
  
  If no resource is defined on the action, the permissions of the application will be used.
   

### Action Rules

Action rules are used to decide if a given action is applicable to the given selection.

The action library will pick the selected objects and send a [request to Action Service](action-service-v1-api.html#action_matches__post)
to discover which actions are applicable to the current selection.

The rules are composed by a name and a condition. This condition contains the dataType applicable to the given rule and
some properties that needs to mach with the selected objects.

```json
{
  "actionName": "app02-action-01",
  "condition": {
    "dataType": "ManagedObject",
    "properties": [
      {
        "name": "moType",
        "value": "MeContext"
      },
      {
        "name": "neType",
        "value": "ERBS"
      }
    ]
  }
}
```

* **actionName**: The action name should match the name given on the application metadata.

* **dataType**: Indicated the dataType applicable to this rule. Currently **Collection** and **ManagedObject** are supported.

* **properties**: All properties declared here must match the properties from the selected objects for this rule to be evaluated to true.

In the given example the action app02-action-01 will only be applicable when all objects in the selection are 
**Managed Objects of type MeContext in the node type ERBS**.

If the selection contains any other selection (e.g. NetworkElement or MeContext from SGSN) this action will not be applicable.

An action can have different rules. If i need for example to support all MeContexts from the node types ERBS and SGSN, 
i can create a second rule file with the following content:

```json
{
  "actionName": "app02-action-01",
  "condition": {
    "dataType": "ManagedObject",
    "properties": [
      {
        "name": "moType",
        "value": "MeContext"
      },
      {
        "name": "neType",
        "value": "SGSN"
      }
    ]
  }
}
```
If you have more than one rule for the same action, the action will be available if **any** of the rules are applicable.
So in this case if the selection contains MeContexts from any node type different than **ERBS** and **SGSN**, 
the action will not be applicable.

**Important:** If your action can support multiple selections, remember to set **multipleSelection** to true in the 
application metadata.

In summary for a given action become available, is required:

* Permission on any resource declared in the application metadata which provides the action (if no resource is defined in the action)
* Permission on any resource/security action combination declared in action metadata (if present)
* The selection needs to match at least one of the available action rules
* The client application needs to be configured to [consume](how-to-consume-actions.html) the action.

#### Deploying Action Rules
 
Action rules should be delivered in the same way as application metadata files.

#### Naming Convention

We recommend to give to your action rule files meaningful names to make it easier to diagnose problems.

You should always use the following name convention:

> [action-name]-[rule-objective].json

**Examples:**

* **app-01-action-01-any-collection.json** (matches any collection data type)
* **app-01-action-01-MeContext-on-ERBS.json** (matches any MeContext on ERBS node types)
