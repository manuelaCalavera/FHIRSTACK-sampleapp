package fhirstack;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by manny on 18.05.2016.
 */
public class ConditionalQuestionStep extends QuestionStep implements ConditionalStep, Serializable {

    private List<ResultRequirement> requirements;
    public ConditionalQuestionStep(){
        // parent class has no default constructor, so we have to provide one
        // should not be used!
        super("");
    }

    public ConditionalQuestionStep(String id, String text, AnswerFormat fmt) {
        super(id, text, fmt);
    }


    @Override
    public void addRequirement(ResultRequirement req) {
        if (requirements == null){
            requirements = new ArrayList<>();
        }
        requirements.add(req);
    }

    @Override
    public void addRequirements(List<ResultRequirement> reqs) {
        if (requirements == null){
            requirements = new ArrayList<>();
        }
        requirements.addAll(reqs);
    }

    @Override
    public boolean requirementsAreSatisfiedBy(TaskResult result) {
        for (ResultRequirement req : requirements){
            if (req.isSatisfiedBy(result) == false){
                return false;
            }
        }
        return true;
    }
}
