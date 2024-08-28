<head><title>Action Service FAQ</title></head>

# Action Service FAQ

### Where in my RPM project should I put my action rule file?

The location of the file in your rpm project is irrelevant.
What is important for Presentation Server is that after RPM installation occurs, the file is accessible through the
shared file system.

See [Installing your metadata files](installing-metadata-files.html) for details.

### In which RPM should I put my json file?

All your json metadata and action rules should be defined in your application UI RPM.

If your application does not have a UI, or the UI is provided by a 3PP, then the services RPM is the preferred option.

### How can I test my changes on a server?

#### Recommended
You can install your UI RPM and execute the following script:

    copy_meta_data_to_nfs.sh

See [Installing your metadata files](installing-metadata-files.html) for details of where to find this script.

#### Alternative
If you just want to make a quick test before create your RPM, you can create your json files directly on
the server under:

* /ericsson/tor/data/apps/your-app: for application metadata
* /ericsson/tor/data/apps/your-app/action/rules: for action rules

These files are read every minute, so if you want to make a quick test make your changes then wait for up to 1 minute.

### What is an application metadata file?

Refer to [this page](addAppToLauncher.html) for more details on application metadata.

### I don't have an application metadata file. Do I need to create it to use actions?

Yes. The application metadata is mandatory for every new change in any application published on Launcher.

### I've included the provideActions attribute in my application metadata but i still can't see my action in my application. What should i check?

* Make sure the logged user has access to your application (the application that provides the action)

* Make sure your client application has the consumeActions attribute with your new action declared.

* Make sure your selection matches the action rule restrictions (moType, neType, etc...)

* Make sure you don't have any invalid information on your metadata. Check Presentation Server logs, if any action
is skipped, we log the reason.

* Make sure you informed all mandatory fields and you are using one of the supported categories.
Here's a list of the supported categories:
    * Fault Management Actions
    * Monitoring & Troubleshooting Actions
    * Configuration Management
    * Performance Management
    * Security Management
    * Collection Actions
    * Collection Modification Actions
    * Legacy Actions

* Make sure you have an order attribute on your action definition.

* Make sure your order number does not clash with the order number for other action in the same category. If this happens both actions will be rejected and the reason will be logged in the server logs.

**IMPORTANT**: The category string **must** match exactly one of these strings, including cases and spaces.

### I need a different category. How can I add a new category?

There are other requirements associated to each of these categories like sorting. Please talk to Mavericks team so
we can identify the requirements and agree the inclusion of a new category.