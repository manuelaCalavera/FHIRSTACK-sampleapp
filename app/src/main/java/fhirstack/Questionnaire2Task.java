package fhirstack;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;

import java.util.List;



/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 02.05.2016.
 * <p/>
 * This is a static class that will provide the conversion of a FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire} Resource
 * to a ResearchStack {@link org.researchstack.backbone.task.Task} that can be used with a ResearchStack
 * {@link org.researchstack.backbone.ui.ViewTaskActivity} to conduct a survey.
 * <p/>
 * Referenced ValueSets in getOptions() of ChoiceQuestions can only be resolved if included in the
 * FHIR questionnaire file.
 * <p/>
 * Not all QuestionTypes are supported yet.
 * @see org.researchstack.backbone.answerformat.AnswerFormat
 */
public class Questionnaire2Task {

    /**
    * Returns a ResearchStack {@link org.researchstack.backbone.task.Task} that can be viewed by a
     * {@link org.researchstack.backbone.ui.ViewTaskActivity} based on a FHIR {@link org.hl7.fhir.dstu3.model.Questionnaire}.
     * If the items have {@link org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent}s, the returned
     * {@link Task} will be a {@link ConditionalOrderedTask}.
     *
     * @param questionnaire a HAPI FHIR Questionnaire Resource
     * @return              a ResearchStack Task
    */
    public static Task questionnaire2Task (Questionnaire questionnaire){

        List<Questionnaire.QuestionnaireItemComponent> items = questionnaire.getItem();
        String identifier = questionnaire.getId();

        List<Step> steps = Items2Steps.items2Steps(items);

        return new ConditionalOrderedTask(identifier, steps) {
        };
    }
}
