package core;

import OLAPDataSet.*;
import core.exceptions.AnalyticsMethodInitializationException;

import java.io.File;
import java.util.List;

/**
 * Analytics Method abstract class.
 *
 * To make an implementation the developer must:
 * <ol>
 *     <li>Use the {@code OLAPDataColumnFactory} to set both inputs an outputs</li>
 *     <li>Override the methods {@code hasPMML()} and {@code getPMMLFile()}, which allow service consumer
 *     classes to access the PMML file if it exists and to inform themselves about the existence of such file.</li>
 *     <li>Override the main algorithm of the class: {@code implementationExecution()} which MUST write its
 *     contents on the{@code output} property, since service consumer classes will use the {@code execute()} method,
 *     which returns {@code output} after executing {@code implementationExecution()}</li>
 * </ol>
 *
 * Initial data setup is done by the {@code initialize(OLAPDataSet data, OLAPPortConfiguration configuration)}
 * method of this class and uses the given configuration to populate the {@code input}.
 * @author  Oscar Barrios
 */

public abstract class AnalyticsMethod {

    private OLAPDataSet input;
    private OLAPDataSet output;

    /**
     * Main execution for the Analytics Method. Will run {@code implementationExecution(output)}, which should be set
     * internally. This forces the implementation to use the output.
     * @return {@code OLAPDataSet output} porperty of the Analytics Method.
     */
    public OLAPDataSet execute(){
        implementationExecution(output);
        return output;
    }

    /**
     * Method to be overriden by the implementing Analytics Method. Uses the property {@code output} in order
     * to be modified so the consumer who runs {@code execute()} can obtain the output.
     * @param output {@code OLAPDataSet output} property of the abstract Analytics Method
     */
    protected abstract void implementationExecution(OLAPDataSet output);

    /**
     * Used to determine wether the method uses or not a PMML file
     * @return {@code true} if the Method uses PMML, {@code false} otherwise
     */
    public abstract Boolean hasPMML();

    /**
     * A File handler to the PMML XML
     * @return a {@code File} handler of the PMML XML. Should be null whenever {@code hasPMML() == true}
     */
    public abstract File getPMMLFile();

    /**
     * Gets the {@code OLAPColumnConfigurationData} for the {@code input} OLAPDataSet.
     * Note that the implementor MUST set the {@code input} property
     * @return an array of the  {@code OLAPColumnConfigurationData} for the {@code input}  OLAPDataSet
     */
    public List<OLAPColumnConfigurationData> getInputPorts(){
        return input.getColumnsConfigurationData();
    }
    /**
     * Gets the {@code OLAPColumnConfigurationData} for the {@code output} OLAPDataSet.
     * Note that the implementor MUST set the {@code output} property
     * @return an array of the  {@code OLAPColumnConfigurationData} for the {@code output} OLAPDataSet
     */
    public List<OLAPColumnConfigurationData> getOutputPorts(){
        return output.getColumnsConfigurationData();
    }

    /**
     * Uses a given {@code OLAPPortConfiguration} to initialize the data of the {@code input} property fot the Method.
     * Since the service consumer will use this method before running {@code execute()}, is important that the
     * {@code OLAPDataSet input} is properly set by the implementation.
     * @param data {@code OLAPDataSet} containing the incoming data
     * @param configuration {@code OLAPPortConfiguration} that specifies how to map {@code data} to the {@code input}
     *                                                   property of the Analytics method.
     * @throws AnalyticsMethodInitializationException
     */
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

    /**
     * Getter accessor for the {@code input} of the Analytics Method
     * @return the {@code input} of the Analytics Method
     */
    public OLAPDataSet getInput() {
        return input;
    }

    /**
     * Getter accessor for the {@code output} of the Analytics Method
     * @return the {@code output} of the Analytics Method
     */
    public OLAPDataSet getOutput() {
        return output;
    }

    /**
     * Setter accessor for the {@code input} of the Analytics Method
     * @param input to be set
     */
    public void setInput(OLAPDataSet input) {
        this.input = input;
    }

    /**
     * Setter accessor for the {@code output} of the Analytics Method
     * @param output to be set
     */
    public void setOutput(OLAPDataSet output) {
        this.output = output;
    }




}