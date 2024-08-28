
<head><title>Action rules definition (Examples)</title></head>

# Action rules definition

## _Please note that these are examples only!_

### Example 1: Match objects using the ManagedObject data type only

```json
{
  "actionName": "networkexplorer-add-to-collection",
  "condition": {
    "dataType": "ManagedObject"
  }
}
```
#### Meaning:
Any ManagedObject can be added to a Collection in NetworkExplorer.

### Example 2: Match objects using the Collection data type only

```json
{
  "actionName": "collectionmanagement-remove-collection",
  "condition": {
    "dataType": "Collection"
  }
}
```
#### Meaning:
Any Collection can be removed in Collection Management.

### Example 3: Match a specific type of ManagedObject representing a Cell

```json
{
  "actionName": "cellmanagement-lock-cells",
  "condition": {
    "dataType": "ManagedObject",
    "properties": [
      {
        "name": "type",
        "value": "EUtranCellFDD"
      }
    ]
  }
}
```
#### Meaning:
This rule states that only ManagedObjects of type EUtranCellFDD can be "locked" in Cell Management.

### Example 4: Match an MeContext from a specific node type

```json
{
  "actionName": "launch-amos",
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
#### Meaning:
This rule states that only an MeContext with node type ERBS can be opened with AMOS. It will not match any MeContext from other node types.