package core;

import OLAPDataSet.*;
import core.exceptions.AnalyticsMethodInitializationException;

import java.io.File;
import java.util.List;

/**
 * Created by lechip on 15/11/15.
 */
public abstract class AnalyticsMethod {
    private OLAPDataSet input;
    private OLAPDataSet output;
    private Boolean hasPMML;

    public OLAPDataSet execute(){
        implementationExecution(output);
        return output;
    }

    public abstract File getPMMLFile();
    protected abstract void implementationExecution(OLAPDataSet output);

    public List<OLAPColumnConfigurationData> getInputPorts(){
        return input.getColumnsConfigurationData();
    }
    public List<OLAPColumnConfigurationData> getOutputPorts(){
        return output.getColumnsConfigurationData();
    }

    public void initialize(OLAPDataSet data, OLAPPortConfiguration configuration)
            throws AnalyticsMethodInitializationException {
        DataSetConfigurationValidationResult validationResult;
        // Check that the configuration
        validationResult =input.validateConfiguration(configuration);
        if (!validationResult.isValid())
        {
            // throw exception if not
            throw new AnalyticsMethodInitializationException(validationResult.getValidationMessage());
        };
        // for each configuration element of the configuration
        for (OLAPPortMapping mappingEntry:
             configuration.getMapping()) {
            // map the data of the column c.id==element.id to the input
            input.getColumns().get(mappingEntry.getInputPort().getId()).setData(
                    data.getColumns().get(mappingEntry.getOutputPort().getId()).getData()
            );
        }
    }

    public OLAPDataSet getInput() {
        return input;
    }

    public OLAPDataSet getOutput() {
        return output;
    }

    public void setInput(OLAPDataSet input) {
        this.input = input;
    }

    public void setOutput(OLAPDataSet output) {
        this.output = output;
    }

    public Boolean getHasPMML() {
        return hasPMML;
    }

    public void setHasPMML(Boolean hasPMML) {
        this.hasPMML = hasPMML;
    }
}