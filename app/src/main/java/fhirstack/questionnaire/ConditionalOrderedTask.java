package fhirstack.questionnaire;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.io.Serializable;
import java.util.List;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 18.05.2016.
 * <p/>
 * This class extends the ResearchStack {@link org.researchstack.backbone.task.OrderedTask} and can
 * display {@link org.researchstack.backbone.step.Step}s as well as
 * {@link ConditionalStep}s. {@link ConditionalStep}s can have {@link ResultRequirement}s and are
 * only shown to the user when all of them are met.
 * The logic is derived from the FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent} element.
 */
public class ConditionalOrderedTask extends OrderedTask implements Serializable {

    /**
     * The parent class {@link org.researchstack.backbone.task.OrderedTask} has no default constructor, so we have to provide one.
     * This constructor should not be used.
     */
    public ConditionalOrderedTask() {
        super("", new ConditionalQuestionStep());
    }

    /**
     * Consturctor.
     * Returns an initialized ConditionalOrderedTask using the specified identifier and array of steps.
     *
     * @param identifier The unique identifier for the task. Should be identical to the the LinkId
     *                   of the corresponding FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire}
     * @param steps     An array of {@link org.researchstack.backbone.step.Step}s and
     *                  {@link ConditionalStep}s in the order in which they should be presented.
     */
    public ConditionalOrderedTask(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    /**
     * Returns the next step that has all its requirements met by the provided {@link org.researchstack.backbone.result.TaskResult},
     * or null
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> after the passed step that has all its
     * requirements met by the provided {@link org.researchstack.backbone.result.TaskResult}, or null if at the end
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
     * Returns the next step before the passed step that has all its requirements met by the
     * provided {@link org.researchstack.backbone.result.TaskResult}, or null
     *
     * @param step   The reference step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> before the passed step that has all its
     * requirements met by the provided {@link org.researchstack.backbone.result.TaskResult}, or null if at the start
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