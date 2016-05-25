package fhirstack;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.io.Serializable;
import java.util.List;

/**
 * Created by manny on 18.05.2016.
 */
public class ConditionalOrderedTask extends OrderedTask implements Serializable {
    public ConditionalOrderedTask() {
        // parent class has no default constructor, so we have to provide one
        // should not be used!
        super("", new ConditionalQuestionStep());
    }

    public ConditionalOrderedTask(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    /**
     * Returns the next step immediately after the passed in step in the list of steps, or null
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> after the passed in step, or null if at the end
     */
    @Override
    public Step getStepAfterStep(Step step, TaskResult result) {
        Step checkStep = null;
        if (step == null) {
            checkStep = steps.get(0);
        } else {
            int nextIndex = steps.indexOf(step) + 1;

            if (nextIndex < steps.size()) {
                checkStep = steps.get(nextIndex);
            }
        }

        if (checkStep instanceof ConditionalStep) {
            if (((ConditionalStep) checkStep).requirementsAreSatisfiedBy(result)) {
                return checkStep;
            } else {
                return getStepAfterStep(checkStep, result);
            }
        } else {
            return checkStep;
        }
    }

    /**
     * Returns the next step immediately before the passed in step in the list of steps, or null
     *
     * @param step   The reference step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> before the passed in step, or null if at the
     * start
     */
    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        Step checkStep = null;

        int nextIndex = steps.indexOf(step) - 1;

        if (nextIndex >= 0) {
            checkStep = steps.get(nextIndex);
        }

        if (checkStep instanceof ConditionalStep) {
            if (((ConditionalStep) checkStep).requirementsAreSatisfiedBy(result)) {
                return checkStep;
            } else {
                return getStepBeforeStep(checkStep, result);
            }
        } else {
            return checkStep;
        }
    }
}
