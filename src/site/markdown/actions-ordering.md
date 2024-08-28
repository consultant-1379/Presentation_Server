<head><title>How to add or update actions provided by my application</title></head>

# Actions Ordering

The actions are ordered using the following criteria:

1. Local Actions (actions that belong to the application. E.g: "Add to Collection" in Network Explorer.)
2. Remote Actions (actions remote to the application)

If two or more local actions are present, then the order attribute will be used to decide which one comes first.

## Remote Actions Sorting

The remote actions are sorted by category in the following order:

1. Fault Management Actions
2. Monitoring & Troubleshooting Actions
3. Configuration Management
4. Performance Management
5. Security Management
6. Collection Actions
7. Collection Modification Actions
8. Legacy Actions

**If you don't know which category your action should be, please check with your PO and UX.**

### Sorting inside each category

Inside each category the actions will be sorted using the following criteria:

1. Actions marked as **primary** always comes first. 
2. The action order attribute

So the action order attribute will define the position unless there's a primary action in the same category.

The order attribute can be any unique integer value (negative or positive). 
This means that you can't have any order duplication inside the same group.
  
If an action does not declare an order or declares duplicated order value, the action will be rejected (will not be available) and the error will be logged in the server logs.

**Please make sure to test your action metadata on a server with the latest ISO to guarantee that you are not using duplicated values before delivering**