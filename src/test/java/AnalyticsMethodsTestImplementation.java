import core.AnalyticsMethod;
import de.rwthaachen.openlap.dataset.OpenLAPColumnDataType;
import de.rwthaachen.openlap.dataset.OpenLAPDataColumnFactory;
import de.rwthaachen.openlap.dataset.OpenLAPDataSet;
import de.rwthaachen.openlap.exceptions.OpenLAPDataColumnException;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * An implementation for testing
 */
public class AnalyticsMethodsTestImplementation extends AnalyticsMethod {


    private static final String PMML_RESOURCE_PATH = "pmmlXmlExample/single_audit_kmeans.xml";

    public AnalyticsMethodsTestImplementation()
    {
        this.setInput(new OpenLAPDataSet());
        this.setOutput(new OpenLAPDataSet());

        try {
            this.getInput().addOpenLAPDataColumn(
                    OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType("inputColumn1", OpenLAPColumnDataType.STRING, true)
            );
            this.getOutput().addOpenLAPDataColumn(
                    OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType("outputColumn1",OpenLAPColumnDataType.INTEGER, false)
            );
        } catch (OpenLAPDataColumnException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void implementationExecution() {
        ArrayList outputData = new ArrayList<Integer>();
        for (Object word :
                this.getInput().getColumns().get("inputColumn1").getData()) {
            outputData.add(((String)word).length());
        }
        this.getOutput().getColumns().get("outputColumn1").setData(outputData);
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
