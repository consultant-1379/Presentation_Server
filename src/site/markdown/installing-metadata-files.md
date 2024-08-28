<head><title>Safely installing metadata files in the server</title></head>

# Safely installing metadata files in the server

The metadata files (applications, actions and localization) need to be stored on an NFS (SFS or VA)
folder that is accessible by HTTP and UIserv instances. The expected location is
`/ericsson/tor/data/apps/`. Since there may be some scenarios where this NFS folder is not available
when installing your UI RPM, these files should not be installed directly to the NFS.

Instead the files should be installed locally and moved to the required location as part of a
pre-start phase of a service on the VM you are installed in.

## Installing Presentation Server Metadata Files on httpd VM

If the package delivering your metatdata files is installed in the httpd VM, you should take
advantage of the `copy_meta_data_to_nfs.sh` script which is executed before the httpd service is
started. This script will copy all content from `/ericsson/httpd/data/apps/` on the local filesystem
to `/ericsson/tor/data/apps/` on the NFS share.

#### 1. Unpack files to local filesystem

Update your RPM plugin definition to unpack the files to the `/ericsson/httpd/data/apps/` folder on
the local filesystem.

```xml
<mapping>
    <!-- Unpack the metadata files to local filesystem in httpd VM -->
    <directory>/ericsson/httpd/data/apps/${applicationId}</directory>
    <filemode>550</filemode>
    <username>${jboss-username}</username>
    <groupname>${jboss-groupname}</groupname>
    <sources>
        <source>
            <location>launcher/metadata/networkexplorer</location>
        </source>
    </sources>
</mapping>
```

#### 2. Copy files to NFS filesystem

As mentioned above, the `copy_meta_data_to_nfs.sh` script in httpd VM will ensure all files/folders from
`/ericsson/httpd/data/apps/` on the local filesystem are copied to the `/ericsson/tor/data/apps/` folder on the
NFS share before the httpd service is started.

## Installing Presentation Server Metadata Files on other VMs (not httpd)

If your package is NOT installed in the httpd VM, you should still follow a similar pattern to above:

1. Unpack metadata files to a location on the local filesystem of the VM.
2. Deliver a script that will copy metadata files to NFS share.
3. Ensure this script is executed before the service in that VM starts.

#### 1. Unpack files to local filesystem

Update your RPM plugin definition to unpack the files to some folder on the local filesytem. In this
example, `/ericsson/httpd/data/apps/` is used.

Ensure that the `username` and `groupname` used exist in the VM where your package will be installed.

```xml
<mapping>
    <!-- Unpack the metadata files to local filesystem -->
    <directory>/ericsson/httpd/data/apps/${applicationId}</directory>
    <filemode>550</filemode>
    <username>${jboss-username}</username>
    <groupname>${jboss-groupname}</groupname>
    <sources>
        <source>
            <location>launcher/metadata/networkexplorer</location>
        </source>
    </sources>
</mapping>
```

#### 2. Deliver script to copy files to NFS filesystem

This script should ensure your files are in the required NFS location before the service starts.

A suitable example is the `copy_meta_data_to_nfs.sh` script from the httpd VM mentioned above.

This script can be found in:

* The `/ericsson/httpd/bin/pre-start/` folder in a httpd VM of an installed ENM system.
* The `ERIChttpdconfig_CXP9031096/src/main/resources/pre-start/` folder of the
OSS/com.ericsson.oss.itpf.configuration/HttpdConfig git repository.

#### 3. Execute script before service starts

The example here will work for VMs running a JBoss container, for other VMs it will depend on the
init scripts for the service is running in that VM.

**NOTE:** Do not use RPM scriptlets to copy content to any NFS share, this is against the Generic
Design Rules for building RPM packages (eridoc 1/10260-FCP1300697)

Update your RPM plugin definition to include your script and install it to the JBoss pre-start hook
script directory:

```xml
<mapping>
    <!-- Unpack the script to JBoss pre-start directory -->
    <directory>/ericsson/3pp/jboss/bin/pre-start/</directory>
    <filemode>550</filemode>
    <username>${jboss-username}</username>
    <groupname>${jboss-groupname}</groupname>
    <sources>
        <source>
            <location>src/main/scripts</location>
            <includes>
                <include>copy_meta_data_to_nfs.sh</include>
            </includes>
        </source>
    </sources>
</mapping>
```

## Troubleshooting

#### `copy_meta_data_to_nfs.sh` script

The `copy_meta_data_to_nfs.sh` pre-start script, in the httpd VM, will not allow the `httpd` service
to start until all content has been successfully copied to the NFS share, so you should ensure that
you only include required files in the `/ericsson/httpd/data/apps/` local directory.

If the script is unable to copy all content, it will retry until either:

* All content is successfully copied
* VM is re-constituted by HA mechanism

If you want to check for any warnings from the pre-start script, use the following query in
logviewer:
> tag:"HTTPD_CONFIG" AND message:"copy_meta_data_to_nfs.sh"

#### Package installation

If your application is not showing up in Launcher, it may be because your RPM did not install
successfully. You can check for this in logviewer by querying for your package name, you should find
either of the below logs:

Successful installation:
> Installed: ERICalexconfiguration_CXP9031821-1.2.7-1.noarch

Failed installation:
> ERICalexconfiguration_CXP9031821-1.2.7-1.noarch: 100
