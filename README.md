# Samply.Common.MDRFaces

Samply.Common.MDRFaces is a Java Server Faces library which eases the user
interface design of web applications where the respective data model relies on
well defined data elements. Especially in case of systems for electronic data
capturing, where the necessary data model is not known beforehand and can even
vary over time, the user interface has to be easily adjustable. This often means
the user instead of the developer designs the various forms for data entry and
therefore an easy to use mechanism has to be provided. By using Samply.MDRFaces
the developer can focus on the implementation of that mechanism, e.g. some
editor component, but does not have to cope with the visualization of every
single data element as for that is taken care of automatically.

# Features

- reuse predefined widgets which already contain a label and the appropiate
  input field
- the input field depends on the datatype of the underlying data element, e.g.
  Boolean (Checkbox), Date (Calendar), String (Text Input) or a List (Dropdown
Menu)
- uses Samply.MDRClient for the metadata retrieval from Samply.MDR
- validates the users input according to the validation information which is
  included in the metadata of a data element

# Build

In order to build this project, you need to configure maven properly.  See
[Samply.Maven](https://bitbucket.org/medinfo_mainz/samply.maven) for more
information.

Use maven to build the jar:

```
mvn clean package
```

Use it as a dependency:

```xml
<dependency>
    <groupId>de.samply</groupId>
    <artifactId>mdrfaces</artifactId>
    <version>${version}</version>
</dependency>
```

