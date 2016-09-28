---
title: "Getting Started with RWX"
---

#### The Basics

To start using RWX for the most common cases, you need a few basic things:

* Some message classes with the appropriate data-binding annotations
* A bindery of the message classes you expect to send and receive (assuming you're using data binding)
* A HTTP client that can render and parse your messages

##### Message Classes

Forget "Hello, World". Let's start with a reasonably complicated XML-RPC request, taken from [Kojiji](https://github.com/release-engineering/kojiji) (a library used to talk to the [Koji](https://docs.pagure.org/koji/) build system). This request / response pair is used to list built archives that match a given Apache Maven GAV (groupId, artifactId, version...a project coordinate).

First, take a look at the XML for the request:


```
<?xml version='1.0'?>
<methodCall>
    <methodName>listArchives</methodName>
    <params>
        <param>
            <value><struct>
                <member>
                    <name>__starstar</name>
                    <value><boolean>1</boolean></value>
                </member>
                <member>
                    <name>type</name>
                    <value><string>maven</string></value>
                </member>
                <member>
                    <name>typeInfo</name>
                    <value><struct>
                        <member>
                            <name>group_id</name>
                            <value><string>org.jbpm</string></value>
                        </member>
                        <member>
                            <name>artifact_id</name>
                            <value><string>jbpm-case-mgmt</string></value>
                        </member>
                        <member>
                            <name>version</name>
                            <value><string>6.4.0.Final-redhat-3</string></value>
                        </member>
                    </struct></value>
                </member>
                <member>
                    <name>filename</name>
                    <value><string>jbpm-case-mgmt-6.4.0.Final-redhat-3.pom</string></value>
                </member>
            </struct></value>
        </param>
    </params>
</methodCall>
```

To model this, we have to create a message class that contains a single parameter, which is actually a relatively complex mapping of data. Let's start with the ListArchivesRequest

Now, the XML for the response (truncated for brevity):

```
<?xml version='1.0'?>
<methodResponse>
    <params>
        <param>
            <value><array><data>
                <value><struct>
                    <member>
                        <name>build_id</name>
                        <value><int>492164</int></value>
                    </member>
                    <member>
                        <name>version</name>
                        <value><string>6.4.0.Final-redhat-3</string></value>
                    </member>
                    <member>
                        <name>type_name</name>
                        <value><string>pom</string></value>
                    </member>
                    <member>
                        <name>artifact_id</name>
                        <value><string>jbpm-case-mgmt</string></value>
                    </member>
                    <member>
                        <name>type_id</name>
                        <value><int>3</int></value>
                    </member>
                    <member>
                        <name>checksum</name>
                        <value><string>f18c45047648e5d6d3ad71319488604e</string></value>
                    </member>
                    <member>
                        <name>extra</name>
                        <value><nil/></value>
                    </member>
                    <member>
                        <name>filename</name>
                        <value><string>jbpm-case-mgmt-6.4.0.Final-redhat-3.pom</string></value>
                    </member>
                    <member>
                        <name>type_description</name>
                        <value><string>Maven Project Object Management file</string></value>
                    </member>
                    <member>
                        <name>metadata_only</name>
                        <value><boolean>0</boolean></value>
                    </member>
                    <member>
                        <name>type_extensions</name>
                        <value><string>pom</string></value>
                    </member>
                    <member>
                        <name>checksum_type</name>
                        <value><int>0</int></value>
                    </member>
                    <member>
                        <name>group_id</name>
                        <value><string>org.jbpm</string></value>
                    </member>
                    <member>
                        <name>buildroot_id</name>
                        <value><nil/></value>
                    </member>
                    <member>
                        <name>id</name>
                        <value><int>1409691</int></value>
                    </member>
                    <member>
                        <name>size</name>
                        <value><int>1177</int></value>
                    </member>
                </struct></value>
            </data></array></value>
        </param>
    </params>
</methodResponse>
```



