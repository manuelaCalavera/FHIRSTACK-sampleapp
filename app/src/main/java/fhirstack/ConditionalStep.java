package fhirstack;

import org.researchstack.backbone.result.TaskResult;

import java.util.List;

/**
 * Created by manny on 18.05.2016.
 */
public interface ConditionalStep {
    public void addRequirement (ResultRequirement req);
    public void addRequirements (List<ResultRequirement> reqs);

    public boolean requirementsAreSatisfiedBy(TaskResult result);
}
