package fhirstack;


import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Questionnaire;


/**
 * Created by manny on 02.05.2016.
 */
public class Items2Steps {

    public static List<Step> items2Steps(List<Questionnaire.QuestionnaireItemComponent> items) {
        List<Step> steps = new ArrayList<Step>();

        for (Questionnaire.QuestionnaireItemComponent item : items){
            Questionnaire. QuestionnaireItemType qType = item.getType();

            if (item.getType() == Questionnaire.QuestionnaireItemType.GROUP){
                steps.addAll(items2Steps(item.getItem()));
            }else{
                steps.add(Item2Step.item2Step(item));
            }

        }
        //TODO conditional stuff
        return steps;
    }
}
