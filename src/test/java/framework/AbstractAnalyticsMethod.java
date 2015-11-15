package framework;

/**
 * Created by lechip on 15/11/15.
 */
public abstract class AbstractAnalyticsMethod {
    Boolean isPredictive;
    public abstract String sayHi();

    public AbstractAnalyticsMethod(Boolean isPredictive) {
        this.isPredictive = isPredictive;
    }

    public Boolean getPredictive() {
        return isPredictive;
    }
}
