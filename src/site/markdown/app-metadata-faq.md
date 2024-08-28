<head><title>Application Metadata FAQ</title></head>

# Application Metadata FAQ

### Where in my RPM project should I put my json metadata file?

The location of the file in your rpm project is irrelevant.
What is important for Presentation Server is that after RPM installation occurs, the file is accessible through the
shared file system.

See [Installing your metadata files](installing-metadata-files.html) for details.

### I've created my RPM with my JSON file and installed on the server but I can't still see my application. What should I check?

1. First of all, make sure you have followed all the steps in [Installing your metadata files](installing-metadata-files.html)
correctly. If you believe so, proceed to the next step.

2. Check on your server if your files exist in **/ericsson/tor/data/apps/**. If they are, proceed to the next step.

3. Check if your application is available in Launcher search box or the A - Z list

    If you can find your application there, it means that the group id you have defined does not match
    Launcher's predefined groups. In this case just update your json to use the id of the group you want.

    The predefined groups are:

    |  ID                           | Name                            |
    | ----------------------------- |:------------------------------- |
    |  Tools                        | Tools                           |
    |  Monitoring                   | Monitoring                      |
    |  Performance_and_Optimization | Performance and Optimization    |
    |  Configuration                | Configuration                   |
    |  System                       | System                          |
    |  Security                     | Security                        |
    |  Documentation                | Documentation                   |

4. Check if the user you are using has access to the resources declared on your application metadata.

5. If you created new resources, please make sure they are installed on your server.

6. Make sure your application has a name defined either in the metadata json file or [localization files](localization-howto.html).

### Can I manually edit my json file on the server to test?

Yes. This will make a temporary change that will be wiped out once the vm is redefined. To persist the change, it
must be committed through your standard code review and release process.

These files are read every minute, if you want to start testing ASAP then restart the Presentation Server service
group (uiserv for physical or presentation for vApps).

### Can I use roles instead resources on my json file?

No. Roles are deprecated and should not be used. Presentation Server/Launcher only supports resources now.

### How can I make my application visible to everyone regardless the user role?

Just remove the resources information from your json metadata.

### How can I define my consumes (open with) information on the json file?

You can't. The "open with" feature will be deprecated with the Actions stories, so that's not migrated to the json structure.


