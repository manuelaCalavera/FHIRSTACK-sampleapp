package fhirstack;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 18.05.2016.
 * <p/>
 * This Class extends the ResearchStack {@link org.researchstack.backbone.step.QuestionStep} and implements the FHIRSTACK
 * {@link ConditionalStep}.
 * {@link ConditionalStep}s provide the logic to add Steps to a {@link ConditionalOrderedTask} that
 * are only shown to the user when certain conditions are met.
 * The logic is derived from the FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent} element.
 * {@link ResultRequirement}s can be added to the step and will be verified every time before
 * displaying the step to the user.
 */
public class ConditionalQuestionStep extends QuestionStep implements ConditionalStep, Serializable {

    private List<ResultRequirement> requirements;

    /**
     * The parent class {@link org.researchstack.backbone.step.QuestionStep} has no default constructor, so we have to provide one.
     * This constructor should not be used.
     */
    public ConditionalQuestionStep() {
        super("");
    }

    /**
     * Constructor.
     * Returns an initialized ConditionalQuestionStep.
     *
     * @param identifier    ID of the QuestionStep. Should be identical to the LinkId of the
     *                      corresponding FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent}
     * @param title         Question text that is displayed to the user.
     * @param format        The {@link org.researchstack.backbone.answerformat.AnswerFormat} used to get the user input.
     */
    public ConditionalQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    /**
     * Adds a {@link ResultRequirement} which has to be met in order for the {@link ConditionalStep}
     * to be displayed to the user.
     * */
    @Override
    public void addRequirement(ResultRequirement req) {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }
        requirements.add(req);
    }

    /**
     * Adds a List of {@link ResultRequirement}s which have to be met in order for the
     * {@link ConditionalStep} to be displayed to the user.
     * */
    @Override
    public void addRequirements(List<ResultRequirement> reqs) {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }
        requirements.addAll(reqs);
    }

    /**
     * Checks if all the {@link ResultRequirement}s of a {@link ConditionalStep} are met by the
     * answers given by the user up to the point of the check.
     * */
    @Override
    public boolean requirementsAreSatisfiedBy(TaskResult result) {
        for (ResultRequirement req : requirements) {
            if (req.isSatisfiedBy(result) == false) {
                return false;
            }
        }
        return true;
    }
}