package fhirstack.questionnaire;

import org.researchstack.backbone.result.TaskResult;

import java.util.List;

/**
 * FHIRSTACK / C3PRO_Android
 *
 * Created by manny on 18.05.2016.
 *
 * This Interface defines the methods used for ConditionalSteps which can be added to
 * {@link ConditionalOrderedTask}s and are only shown to the user when certain conditions are met.
 * The logic is derived from the FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent} element.
 * {@link ResultRequirement}s can be added to the step and will be verified every time before
 * displaying the step to the user.
 */
public interface ConditionalStep {
    /**
     * Adds a {@link ResultRequirement} which has to be met in order for the ConditionalStep to be
     * displayed to the user.
     * */
    public void addRequirement (ResultRequirement req);

    /**
     * Adds a List of {@link ResultRequirement}s which have to be met in order for the ConditionalStep
     * to be displayed to the user.
     * */
    public void addRequirements (List<ResultRequirement> reqs);

    /**
     * Checks if all the {@link ResultRequirement}s of a ConditionalStep are met by the answers
     * given by the user up to the point of the check.
     * */
    public boolean requirementsAreSatisfiedBy(TaskResult result);
}
