package fhirstack;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

import java.util.Iterator;
import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;

/**
 * Created by manny on 02.05.2016.
 */
public class Questionnaire2Task {


    public static Task questionnaire2Task (Questionnaire questionnaire){

        Questionnaire.Group toplevel = questionnaire.getGroup();
        String identifier = questionnaire.getId().getValueAsString();

        List<Step> steps = Group2Steps.group2Steps(toplevel);

        return new OrderedTask(identifier, steps) {
        };




    }



}
