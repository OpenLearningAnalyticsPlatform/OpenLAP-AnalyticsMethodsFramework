import OLAPDataSet.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.AnalyticsMethod;
import core.exceptions.AnalyticsMethodInitializationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Created by lechip on 16/11/15.
 */
public class AnalyticsMethodFrameworkTests {


    AnalyticsMethod testMethod1;
    OLAPDataSet inputDataSet;
    OLAPPortConfiguration configuration1;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void beforeTests() throws IOException {
        // Configure mapper
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.getFactory().configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        testMethod1 = new AnalyticsMethodsTestImplementation();
        // Initialize mockup DataSet for inputDataSet to the analytics method
        inputDataSet = mapper.readValue(this.getClass().getResourceAsStream("DataSetSample.json"),OLAPDataSet.class);
        // Initialize mockup OLAPPortConfiguration
        configuration1 = mapper.
                readValue(this.getClass().getResourceAsStream("ConfigurationSample.json"),
                        OLAPPortConfiguration.class);
    }

    // Test initialization
    @Test
    public void testInitialization() throws AnalyticsMethodInitializationException {
        // Make Input
        //
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList("bananito", "abc" ));
        testMethod1.initialize(inputDataSet, configuration1);
        Assert.assertEquals(expected, testMethod1.getInput().getColumns().get("inputColumn1").getData());
    }

    // Test getInputs and getOutputs
    @Test
    public void testGetInputsAndOutputs() throws IOException {
        //Make object for input from json
        List<OLAPColumnConfigurationData> inputExpected = mapper.readValue(this.getClass().
                getResourceAsStream("InputConfigurationDataSample.json"),
                mapper.getTypeFactory().constructCollectionType(List.class, OLAPColumnConfigurationData.class) );
        List<OLAPColumnConfigurationData> outputExpected = mapper.readValue(this.getClass().
                getResourceAsStream("OutputConfigurationDataSample.json"),
                mapper.getTypeFactory().constructCollectionType(List.class, OLAPColumnConfigurationData.class) );
        //Make object for output from json

        Assert.assertArrayEquals(inputExpected.toArray(), testMethod1.getInputPorts().toArray());

        Assert.assertArrayEquals(outputExpected.toArray(),testMethod1.getOutputPorts().toArray());

    }

    // Test execution
    @Test
    public void testExecution() throws IOException, AnalyticsMethodInitializationException {
        //Make a dataset from json with the expected result
        OLAPDataSet expectedOutPutDataset = mapper.readValue(
                this.getClass().getResourceAsStream("DataSetOutputSample.json"), OLAPDataSet.class);
        testMethod1.initialize(inputDataSet, configuration1);

        //Execute the method
        //Assert that they are equal
        Assert.assertEquals(expectedOutPutDataset,testMethod1.execute());
    }

    //Test PMML
    @Test
    public void testPMMLLoading()throws Exception{
        Assert.assertTrue(testMethod1.hasPMML());
        Assert.assertNotNull(testMethod1.getPMMLInputStream());
    }

}
