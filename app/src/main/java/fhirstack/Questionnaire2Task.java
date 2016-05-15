package fhirstack;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;


import java.util.List;



/**
 * Created by manny on 02.05.2016.
 */
public class Questionnaire2Task {


    public static Task questionnaire2Task (Questionnaire questionnaire){

        List<Questionnaire.QuestionnaireItemComponent> items = questionnaire.getItem();
        String identifier = questionnaire.getId();

        List<Step> steps = Items2Steps.items2Steps(items);

        return new OrderedTask(identifier, steps) {
        };




    }



}
