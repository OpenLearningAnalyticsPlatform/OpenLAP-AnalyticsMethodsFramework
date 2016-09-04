## INTRODUCTION
The Open Learning Analytics Platform (OpenLAP) supports extensibility and modularity by allowing developers and researches to add new Analytics Methods, Visualizations and Analytics Modules. The OpenLAP follows a simple principle of Inversion of Control: Once the new component is implemented and the binaries are uploaded, it is possible for the OpenLAP to use it at runtime, allowing developers and researches to extend the functionalities of the OpenLAP. 

The Analytics Methods component is responsible for managing the repository of all available analytics methods in the OpenLAP. Analytics methods of any type can be added to this repository such as statistics, data mining (DM), and social network analysis (SNA). The OpenLAP-AnalyticsMethodsFramework project contains the necessary classes that the developers can extend, implement, pack in JAR and upload to add new analytics method to the OpenLAP. The following video gives the introduction to the OpenLAP followed by the tutorial to add new Analytics Method to the OpenLAP.

<p align="center">
	<a href="http://www.youtube.com/watch?feature=player_embedded&v=9PdU8pQkvLU" target="_blank">
		<span><strong>Video Tutorial to add new Analytics Methods to OpenLAP</strong></span>
		<br>
		<img src="http://img.youtube.com/vi/9PdU8pQkvLU/0.jpg" alt="OpenLAP Introduction and New Analytics Method"/>
	</a>
</p>

## Fundamental Concepts
The main idea behind analytics methods is to receive the incoming data in the OpenLAP-DataSet format, apply the analysis to this data and return the analyzed data in the OpenLAP-DataSet format. To implement a new analytics method, the developer must extend the abstract `AnalyticsMethod` class available in the OpenLAP-AnalyticsMethodsFramework project. In the following sub-sections the OpenLAP-DataSet and the methods of the abstract `AnalyticsMethod` class are explained in detail.

### OpenLAP-DataSet
The OpenLAP-DataSet is the internal data exchange format used in the OpenLAP. It is a modular JSON based serializable dataset to validate and exchange data between different components of the OpenLAP. Since the modular approach is used to develop the OpenLAP, different components act with relative independence from each other. Thus, a data exchange model is needed which can easily be serialized to and from JSON.

The OpenLAP-DataSet is implemented under the class name `OLAPDataSet`. It is a collection of columns represented using the class `OLAPDataColumns`. Each column consists of two distinctive sections. A metadata section contains id, type, required flag, title and description of the column encapsulated in a class `OLAPColumnConfigurationData`. The second section is the data itself, represented as an array of the specified type. More details are available on the [OpenLAP-DataSet project](https://github.com/OpenLearningAnalyticsPlatform/OpenLAP-DataSet) page. Concrete examples to initialize, read from and write to OpenLAP-DataSet is given below in step by step guide to implement a new Analytics Method.

### Methods of the AnalyticsMethod abstract class
The `AnalyticsMethod` abstract class has a series of methods that allows new classes that extend it to be used by the OpenLAP.

#### Implemented Methods
* The `initialize()` method takes an `OLAPDataSet` and a `OLAPPortConfiguration` as parameters. The `AnalyticsMethod` will use this as its input `OLAPDataSet` with the incoming data if the `OLAPPortConfiguration` is valid.
* The `execute()` method returns the output `OLAPDataSet` after executing the `implementationExecution()` method and performing the analysis. 
* The `getInputPorts()` and `getOutputPorts()` methods allow other classes to obtain the columns metadata as `OLAPColumnConfigurationData` class of the input and output `OLAPDataSet`.

#### Abstract Methods
* The `implementationExecution()` method is where the developer will implement the logic to interpret the incoming data from input `OLAPDataSet`, analyze it and write it to the output `OLAPDataSet`. This method is called by the `execute()` method described above to execute this analytics method. The important point here is that the analyzed data should be written to the output `OLAPDataSet` before this method ends.
* The `hasPMML()` method returns a Boolean value indicating the desire of the developer to use [Predictive Model Markup Language (PMML)](http://dmg.org/pmml/v4-2-1/GeneralStructure.html) in the analytics method. The PMML is mainly used while performing a predictive analysis. The OpenLAP provides the mechanism to validate the PMML XML during upload.
* The `getPMMLInputStream()`method should return an input stream to the PMML file available in the JAR bundle of the analytics method If the `hasPMML()` method returns `true`.


## Step by step guide to implement a new Analytics Method

The following steps must be followed by the developer to implement a new Analytics Method for the OpenLAP:

1. Setting up the development environment

2. Creating project and importing the dependencies into it.

3. Create a class that extends the `AnalyticsMethod`.

4. Define the input and output `OLAPDataSet`.

5. Implement the abstract methods.

6. Pack the binaries into a JAR bundle.

7. Upload the JAR bundle using the OpenLAP administration panel along with the configuration.

These steps are explained in more details with concrete examples in the following sections.

### Step 1. Setting up the development environment
To develop a new analytics method, you need to install the following softwares.
* [Java Development Kit (JDK) 7+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Any Integrated Development Environment (IDE) for Java development, such as, [Intellij IDEA](https://www.jetbrains.com/idea/download), [NetBeans](https://netbeans.org/downloads/), [Eclipse](https://eclipse.org/downloads/), etc. 

In the following steps we are going to use the Intellij IDEA for developing the sample analytics method using maven.

### Step 2. Creating project and importing the dependencies into it.
* Create a new project. `File -> New -> Project`
* Select `Maven` from the left and click `Next`.
* Enter the `GroupId`, `ArtifactId` and `Version`, e.g.

	`GroupId`: de.rwthaachen.openlap.analyticsmethods.Samples
	
	`ArtifactId`: ItemCounter
	
	`Version`: 1.0-SNAPSHOT
	
* Specify project name and location, e.g.

	`Project Name`: Item-Counter
	
	`Project Location`: C:\Users\xxxxx\Documents\IdeaProjects\Item-Counter
	
* Add JitPack repository to the `pom.xml` file.

Maven:
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
```

* Add dependency of the OpenLAP-AnalyticsMethodsFramework project to the ‘pom.xml’ file. The latest version of the dependency xml can be retrieved from the  [![](https://jitpack.io/v/OpenLearningAnalyticsPlatform/OpenLAP-AnalyticsMethodsFramework.svg)](https://jitpack.io/#OpenLearningAnalyticsPlatform/OpenLAP-AnalyticsMethodsFramework). 

Maven:
```xml
	<dependency>
	    <groupId>com.github.OpenLearningAnalyticsPlatform</groupId>
	    <artifactId>OpenLAP-AnalyticsMethodsFramework</artifactId>
	    <version>v2.2.1</version>
	</dependency>
```

### Step 3. Create a class that extends the `AnalyticsMethod`.
In the project create a class that extends the `AnalyticsMethod` as shown in the example below. The class should be contained in a package within the `src` folder to avoid naming conflicts.

```java
package de.rwthaachen.openlap.analyticsmethods.samples;

import core.AnalyticsMethod;
import java.io.InputStream;

public class ItemCount extends AnalyticsMethod {
    protected void implementationExecution() {
		...
    }

    public Boolean hasPMML() {
		...
    }

    public InputStream getPMMLInputStream() {
		...
    }
}
```
### Step 4. Define the input and output `OLAPDataSet`.
The input and output `OLAPDataSet` should be defined in the constructor of the extended class `ItemCount` as shown in the example below.

```java
// Declaration of input and output OLAPDataSet by adding OLAPDataColum objects with the OLAPDataColumnFactory
public ItemCount()
    {
        this.setInput(new OLAPDataSet());
        this.setOutput(new OLAPDataSet());

        try {
            this.getInput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("items_list", OLAPColumnDataType.STRING, true, "Items List", "List of items to count")
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

### Step 5. Implement the abstract methods.
Three abstract methods of the `AnalyticsMethod` class (as discussed in the Fundamental Concept section) should be implemented. The example below shows a sample implementation of the analytics method which accepts the list of string items as an input, count the number of time each item occurred in the list and return the top 10 most occurred items.

```java
    @Override
    protected void implementationExecution() { 
	LinkedHashMap<String, Integer> itemCount = new LinkedHashMap<String, Integer>();
	
	    //Iiterate over each item of the column
	    for (Object item : this.getInput().getColumns().get("items_list").getData()) {
	        if (itemCount.containsKey(item))
	            itemCount.put((String) item, itemCount.get((String) item) + 1);
	        else
	            itemCount.put((String) item, 1);
	    }
	    
	    Set<Map.Entry<String, Integer>> itemCountSet = itemCount.entrySet();
	    int counter = 10;
	    if(itemCountSet.size()<10)
	        counter = itemCountSet.size();
	        
	//Finding the item with the highest count, adding it to the output OLAPDataSet and removing it from the itemCount Array.
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
	//if `hasPMML()` return true than example can be like 
	//return getClass().getResourceAsStream(PMML_RESOURCE_PATH);
	
        return null;
    }

    @Override
    public Boolean hasPMML() {
        return false;
    }
```

#### Step 6. Pack the binaries into a JAR bundle.

The complied binaries must be packed into a JAR bundle. It should be noted that the file name of the JAR bundle should consists of integers and characters only. The JAR bundle can easily be generated in the Intellij IDEA by following the following steps:
* Open the `Run/Debug Configurations`. `Run -> Edit Configurations…`
* Add new configuration by pressing the `+` on the top left.
* Select `Maven` from the available options.
* Set the `Name` to "Generate JAR" (without double quotes).
* On the `Parameters` tab set `Command line` = clean install
* Run the project by pressing `Shift + F10` or from `Run -> Run 'Generate JAR'`
* The JAR bundle will be generated in the `targer` folder within the project directory.
* Rename the generated JAR bundle to contain only integers and characters.

#### Step 7. Upload the JAR bundle to the OpenLAP.
The newly implemented analytics method is now ready to be uploaded to the OpenLAP through the administration panel including the JAR file and parameters like, analytics method name, description, and name of the implementing class including package (the class that extends the `AnalyticsMethod` abstract class). 
