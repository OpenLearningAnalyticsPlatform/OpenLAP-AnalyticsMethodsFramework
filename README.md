# OpenLAP-AnalyticsMethodsFramework

## INTRODUCTION

The OpenLAP-AnalyticsMethodsFramework contains the necessary classes that the developers can extend, implement, pack in JAR and upload to add new Analytics Methods to the OpenLAP. The following video gives the introduction to Open Learning Analytics Platform (OpenLAP) following with the step by step video guide to adding new Analytics Method to OpenLAP.

<p align="center">
	<a href="http://www.youtube.com/watch?feature=player_embedded&v=9PdU8pQkvLU" target="_blank">
		<img src="http://img.youtube.com/vi/9PdU8pQkvLU/0.jpg" alt="OpenLAP Introduction and New Analytics Method"/>
		<br>
		<span>Video Tutorial to add new Analytics Methods to OpenLAP</span>
	</a>
</p>
## MOTIVATION

One of the motivations of the OpenLAP is the extensibility and modularity based on the principle of allowing developers
and researches to add new Analytics Methods, Visualizations and Analytics Modules.
The OpenLAP-AnalyticsMethodsFramework has the facilities for developers to download the required dependencies 
and guarantee that they can create new Analytics Methods that are compatible to the Analytics Method macro component of
the OpenLAP.

The framework follows a simple principle of Inversion of Control: Once the framework is implemented and the binaries
are uploaded to the macro component, it is possible for the OpenLAP to use the uploaded Analytics Methods in runtime,
allowing users to extend the functionalities that the platform provides trough a simple API.

## FUNCTIONALITY AND INTERNALS

The main element of the OpenLAP-AnalyticsMethodsFramework is the abstract class `AnalyticsMethod`. This class must be
extended by any developer who wishes to expand the functionality of the Analytics Methods macro component of the
OpenLAP, i.e. provide new Analytics Methods to analyze Indicator Data.

Since the Analytics Methods macro component is one of the main data manipulation entities of the
OpenLAP, the `AnalyticsMethod` was designed to use at its core the OpenLAP-DataSet facilities. An Analytics Method
purpose is to transform data from an input and pipe it to an output.
The input will typically be from an Indicator of the Indicator Engine macro component and the outputs would be directed
to visualizations of the Visualizer macro component. 
Two `OLAPDataSet` realize effectively the input and output of the Analytics Method, and are the main
properties of the `AnalyticsMethod`.

### Methods of the AnalyticsMethod abstract class
Additionally, the `AnalyticsMethod` abstract class has a series of java methods that allows new 
classes that extend it to be used by the OpenLAP.

#### Concrete Methods
* A concrete initialization method `initialize` that takes a `OLAPDataSet` and a `OLAPPortConfiguration` as parameters.
The `AnalyticsMethod` will use this to set it's input `OLAPDataSet` if the `OLAPPortConfiguration` is valid.
* A concrete `execute()` method that returns the `AnalyticsMethod` output `OLAPDataSet` after executing it's main
algorithm, i.e. the `implementationExecution` method. The reason for this is that the main implementation must guarantee
that the output is consistent with what the adversited output `OLAPDataSet` informs. Typically, any class using an
`AnalyticsMethod` will first initialize it with some Data in the form of a `OLAPDataSet` and a `OLAPPortConfiguration`,
and then will use the `execute()` method to obtain the result of using the concrete Analytics Method over 
the provided data.
* The methods `getInputPorts` and `getOutputPorts` allow other classes to obtain the `OLAPColumnConfigurationData` of
the input and output `OLAPDataSet` properties of the developed Analytis Method. This enables an automated
way of the Analytics Method to make available the configuration it requires
in order to be executed and the structure of its output once it is executed.

### Abstract Methods
* The `implementationExecution` realizes the main algorithm of the  Analytis Method. It will be used by the `execute`
method described above. This method is where the developer will write the main algorithm, mainipulate the input data
and output it into the output `OLAPDataSet` of the implementation of the `AnalyticsMethod`. It is important to note that
the developer is responsible of outputing the result of the algorithm into the  `AnalyticsMethod` output `OLAPDataSet`, 
since it is the output proeprty the one that is returned by the `execute` method.
* If the developer desires to use PMML [PMML](#references), then it is possible to allow the Framework to provide
validation of the PMML XML during upload. The `hasPMML` method must then return `True` in order for the Analytics
Method macro component to perform the PMML validation.
* If the Analytics Method `hasPMML` returns `True`, then the method `getPMMLInputStream` must return an input stream
with the PMML XML file. It is done so because of the manner Java references files within JAR files.

## USAGE

### Implementing an Analytics Method

A developer must follow this steps to create a new Analytics Method for the OpenLAP:

1. Import the dependency into a new Java project.
2. Create a class that extends the `AnalyticsMethod`.
3. Declare the input and output `OLAPDataSet` of the implementation in the class that extends the `AnalyticsMethod`
3. Implement the abstract methods of the `AnalyticsMethod` abstract class.
4. Pack the binaries into a JAR file.
5. Upload the JAR file trough the HTTP endpoint of the Analytics Method macro module along with a JSON metadata object
that specifies the creator, name of the Analytics Method, description, and the name of the class that implements the
`AnalyticsMethod` abstract class.

The steps are explained in the next subsections.

#### Importing into a project

**Step 1.** The JitPack repository must be added to the build file:

Maven:
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
```
Gradle:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

**Step 2.**  The dependency must be added:

Maven:
```xml
	<dependency>
	    <groupId>com.github.OpenLearningAnalyticsPlatform</groupId>
	    <artifactId>AnalyticsMethodsFramework</artifactId>
	    <version>-SNAPSHOT</version>
</dependency>
```
Gradle:
```gradle
dependencies {
	        compile 'com.github.OpenLearningAnalyticsPlatform:AnalyticsMethodsFramework:-SNAPSHOT'
}
```

#### Create a class that extends the AnalyticsMethod

In the project, a class that extends the `OpenLAP-AnalyticsMethodsFramework.core.AnalyticsMethod`
as shown in the listing below:

```java
// Class extending the AnalyticsMethod abstract class that is part of the OpenLAP-AnalyticsMethodsFramework
package main;
import DataSet.*;
import core.AnalyticsMethod;
import exceptions.OLAPDataColumnException;

public class AnalyticsMethodImplementation extends AnalyticsMethod {

   //...
   
    @Override
    protected void implementationExecution(OLAPDataSet output) {
        ...
    }

    @Override
    public InputStream getPMMLInputStream() {
        ...
    }

    @Override
    public Boolean hasPMML() {
        ...
    }
}
```

The class must be always be contained within a package within the `src` folder.
It cannot be a class which resides on the root of the `src` folder.

### Declare input and output OLAPDataSets

The constructor of the Analytics Method must declare the input and output `OLAPDataSet` objects. Each must be a
collection of `OLAPDataColumn` objects created using the `OLAPDataColumnFactory`. An example can be seen in the listing
below:

```java
// Declaration of inputs and outputs adding OLAPDataColum objects with the OLAPDataColumnFactory
public AnalyticsMethodImplementation()
    {
        this.setInput(new OLAPDataSet());
        this.setOutput(new OLAPDataSet());

        try {
            this.getInput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("item_name", OLAPColumnDataType.STRING, true, "Items", "List of items to count")
            );
            this.getOutput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("item_name", OLAPColumnDataType.STRING, true, "Item Names", "List of top 10 most occuring items in the list")
            );
            this.getOutput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("item_count", OLAPColumnDataType.INTEGER, true, "Item Count", "Number of time each item occured in the list")
            );
        } catch (OLAPDataColumnException e) {
            e.printStackTrace();
        }
    }
```

#### Implement the required methods

The developer must implement the three java methods that the abstract `AnalyticsMethod` has for being overriden.
As explained before, the java method `implementationExecution` should contain the main algorithm and deliver the result
into the output provided.
If the developer desires to use the validation utilities that the back-end server gives for validating PMML
files on upload of the Analytics Method, it is possible by returning true on the `hasPMML` method and providing an
`InputStream` to the XML file in the `getPMMLInputStream` java method. An example of the overriding of these methods
can be seen in the listing below.

```java
// Example implementation of the methods that need to be overriden
// from the OpenLAP-AnalyticsMethodsFramework.core.AnalyticsMethod
    @Override
    protected void implementationExecution(OLAPDataSet output) {
        LinkedHashMap<String, Integer> itemCount = new LinkedHashMap<String, Integer>();
	    // Iterate over each word of the column of the arrays
	    for (Object word : this.getInput().getColumns().get("item_name").getData()) {
	        if (itemCount.containsKey(word))
	            itemCount.put((String) word, itemCount.get((String) word) + 1);
	        else
	            itemCount.put((String) word, 1);
	    }
	    Set<Map.Entry<String, Integer>> itemCountSet = itemCount.entrySet();
	    int counter = 10;
	    if(itemCountSet.size()<10)
	        counter = itemCountSet.size();
	    for(;counter>0;counter--){
	
	        Iterator<Map.Entry<String, Integer>> itemCountSetIterator = itemCountSet.iterator();
	
	        Map.Entry<String, Integer> topEntry = itemCountSetIterator.next();
	
	        while (itemCountSetIterator.hasNext()) {
	            Map.Entry<String, Integer> curEntry = itemCountSetIterator.next();
	
	            if (curEntry.getValue() > topEntry.getValue())
	                topEntry = curEntry;
	        }
	        getOutput().getColumns().get("item_name").getData().add(topEntry.getKey());
	        getOutput().getColumns().get("item_count").getData().add(topEntry.getValue());
	        itemCountSet.remove(topEntry);
	    }
    }

    @Override
    public InputStream getPMMLInputStream() {
        return getClass().getResourceAsStream(PMML_RESOURCE_PATH);
    }

    @Override
    public Boolean hasPMML() {
        return false;
    }
```

#### Pack binaries into a JAR file

The complied binaries must be packed into a JAR file. An example of the partial 
contents of a valid JAR file is listed below.

```
$jar tf AnalyticsMethodImplementation.jar
...
core/
core//
core/AbstractAnalyticsMethod.class
META-INF/maven/org.rwth-aachen.olap/
META-INF/maven/org.rwth-aachen.olap//
META-INF/maven/org.rwth-aachen.olap/AnalyticsMethodsFramework/
META-INF/maven/org.rwth-aachen.olap/AnalyticsMethodsFramework//
META-INF/maven/org.rwth-aachen.olap/AnalyticsMethodsFramework/pom.xml
META-INF/maven/org.rwth-aachen.olap/AnalyticsMethodsFramework/pom.properties
...
```

#### Upload JAR to the OpenLAP Analytics Methods macro module

The Analytics Method is then ready to be uploaded by making an HTTP POST request to the `/AnalyticsMethods` endpoint
of the OpenLAP Analytics Method macro component server with both the JAR file and a JSON object with
the Analytics Method name, creator, description,
implementing class(the class that extends the `AnalyticsMethod` abstract class)
 and an ASCII filename to be used in the server to store the JAR.
 
An example using the linux command `curl` is shown in the listing below.

```
$ curl \
> -i -X POST \
> -F jarBundle=@AnalyticsMethodImplementation.jar \
> -F methodMetadata='{
> "name" : "Example Analytics Method",  "creator" : "lechip",  "description" : "Analytics Method for example","implementingClass" : "main.AnalyticsMethodImplementation", "filename":"AnalyticsMethodImplementation"}' \
> localhost:8080/AnalyticsMethods
HTTP/1.1 100 Continue

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Application-Context: application:development
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Mon, 28 Dec 2015 21:28:30 GMT

{
  "id": "2",
  "name": "AnalyticsMethodImplementation",
  "creator": "lechip",
  "description": "A Method",
  "implementingClass": "main.AnalyticsMethodImplementation",
  "binariesLocation": "./analyticsMethodsJars_tests/",
  "filename": "AnalyticsMethodExample"
}
```

The successful response for method upload is shown in the following list, in its JSON form.

```json
{
  "id": "2",
  "name": "AnalyticsMethodImplementation",
  "creator": "lechip",
  "description": "A Method",
  "implementingClass": "main.AnalyticsMethodImplementation",
  "binariesLocation": "./analyticsMethodsJars_tests/",
  "filename": "AnalyticsMethodExample"
}
```

The Analytics Method macro component is then aware of the uploaded Analytics Method implementation and its information
can be fetched with an HTTP GET request like the one in the following listing.

```
$ curl -i localhost:8080/AnalyticsMethods/2

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Application-Context: application:development
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Mon, 28 Dec 2015 21:33:04 GMT

{
  "id": "2",
  "name": "AnalyticsMethodImplementation",
  "creator": "lechip",
  "description": "A Method",
  "implementingClass": "main.AnalyticsMethodImplementation",
  "binariesLocation": "./analyticsMethodsJars_tests/",
  "filename": "AnalyticsMethodExample"
}
```

## REFERENCES

* [PMML]: "PMML Standard 4.2. (2014, February). Data Mining Group. Retrieved from
 http://dmg.org/pmml/v4-2-1/GeneralStructure.html"
