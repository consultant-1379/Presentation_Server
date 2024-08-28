<head><title>How to add or update actions that should be consumed by my application</title></head>

# How to add or update actions that should be consumed by my application

This guide will show you how to use an action published by other application on your own.

Basically all you need to do is declare in your application metadata file that you are ready to use a given action.

```json
{
  "id": "app01",
  "name": "My Application 01",
  "consumeActions": ["app02-action-01"]
}
```

In this example you are declaring that your application is ready to handle the action **app02-action-01**

**Important:** Keep in mind that this only allows the Action Service to lookup for this action when the origin is your application.
The user will still need to have access to the target application (application that actually executes the action) and
the selection should match **app02-action-01** rules to be usable.

If an empty consumesActions array is declared then the application will consume all available actions.

```json
{
  "id": "app02",
  "name": "My Application 02",
  "consumeActions": []
}
```

**Warning:** New actions can be introduced at any time so consuming all available actions may produce unexpected results.
As with the previous example, the user will still need to have access to the target application that provides the action.