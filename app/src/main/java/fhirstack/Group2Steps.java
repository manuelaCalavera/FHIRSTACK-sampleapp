package fhirstack;

import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;


/**
 * Created by manny on 02.05.2016.
 */
public class Group2Steps {

    public static List<Step> group2Steps(Questionnaire.Group group) {

        List<Step> steps = new ArrayList<Step>();
        String groupTitle = group.getTitle();
        String groupText = group.getText();

        // create an instruction step if we have a title or text
        if ((groupTitle != null && groupTitle != "") || (groupText != null && groupText != "")) {
            String id = "";
            if (group.getLinkId() != null) {
                id = group.getLinkId().toString();
            } else {
                if (groupTitle != null) {
                    id = groupTitle;
                } else {
                    id = groupText;
                }
            }
            Step intro = new InstructionStep(id, groupTitle, groupText);
            steps.add(intro);
        }


        //TODO conditional stuff


        // add subgroups or questions
        List<Questionnaire.Group> subgroups = group.getGroup();
        List<Questionnaire.GroupQuestion> questions = group.getQuestion();

        if (!subgroups.isEmpty()) {

            for (Questionnaire.Group grp : subgroups){
                steps.addAll(group2Steps(grp));
            }

        } else if (!questions.isEmpty()) {
            for (Questionnaire.GroupQuestion qst : questions){
                steps.add(Question2Step.question2Step(qst));
            }
        }


        return steps;
    }
}
