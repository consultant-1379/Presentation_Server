<head><title>Application providing and consuming actions (Example)</title></head>

# Application providing and consuming actions (Example)

```json
{
  "id": "networkexplorer",
  "name": "Network Explorer",
  "shortInfo": "Use Network Explorer to search and retrieve Network Configuration Data.",
  "path": "/#networkexplorer",
  "type": "web",
  "version": 1,
  "openInNewWindow": false,
  "resources": [
    "topologySearchService",
    "searchExecutor",
    "topologyCollectionsService",
    "modelInformationService"
  ],
  "groups": [
    {
      "id": "Tools",
      "name": "Tools"
    }
  ],
  "consumeActions": [
      "cellmanagement-lock-cells",
      "cli-describe",
      "launch-amos"
  ],
  "provideActions": [
    {
      "name": "networkExplorer-add-to-collection",
      "defaultLabel": "Add to Collection",
      "multipleSelection": true,
      "plugin": "networkExplorer/actions/add-to-collection",
      "order": 1,
      "resources": [
        {
          "name": "topologySearchService",
          "action": "READ"
        }
      ] 
    }
  ]
}
```

In this example we have:
 * A web app defined called Network Explorer
 * Network Explorer provides one action to add objects to a collection, usable by other apps
 * Network Explorer consumes 3 external actions that are provided by other apps