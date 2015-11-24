import OLAPDataSet.*;
import core.AnalyticsMethod;
import exceptions.OLAPDataColumnException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by lechip on 16/11/15.
 */
public class AnalyticsMethodsTestImplementation extends AnalyticsMethod {


    private static final String PMML_RESOURCE_PATH = "pmmlXmlExample/single_audit_kmeans.xml";

    public AnalyticsMethodsTestImplementation()
    {
        this.setInput(new OLAPDataSet());
        this.setOutput(new OLAPDataSet());

        try {
            this.getInput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("inputColumn1",OLAPColumnDataType.STRING, true)
            );
            this.getOutput().addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("outputColumn1",OLAPColumnDataType.INTEGER, false)
            );
        } catch (OLAPDataColumnException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void implementationExecution(OLAPDataSet output) {
        ArrayList outputData = new ArrayList<Integer>();
        for (Object word :
                this.getInput().getColumns().get("inputColumn1").getData()) {
            outputData.add(((String)word).length());
        }
        output.getColumns().get("outputColumn1").setData(outputData);
    }

    @Override
    public Boolean hasPMML() {
        return true;
    }

    @Override
    public InputStream getPMMLInputStream() {
        //URL fileUrl = getClass().getClassLoader().getResource(PMML_RESOURCE_PATH);
        //return new File(fileUrl.toURI());
        return getClass().getResourceAsStream(PMML_RESOURCE_PATH);
    }

}
