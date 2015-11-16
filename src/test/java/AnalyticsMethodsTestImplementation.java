import OLAPDataSet.*;
import core.AnalyticsMethod;
import exceptions.OLAPDataColumnException;

import java.util.ArrayList;

/**
 * Created by lechip on 16/11/15.
 */
public class AnalyticsMethodsTestImplementation extends AnalyticsMethod {


    public AnalyticsMethodsTestImplementation()
    {
        this.input = new OLAPDataSet();
        this.output = new OLAPDataSet();

        try {
            this.input.addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("inputColumn1",OLAPColumnDataType.STRING, true)
            );
            this.output.addOLAPDataColumn(
                    OLAPDataColumnFactory.createOLAPDataColumnOfType("outputColumn1",OLAPColumnDataType.INTEGER, true)
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
}