package fhirstack;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Type;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.io.Serializable;

/**
 * Created by manny on 18.05.2016.
 */
public class ResultRequirement implements Serializable {
    private String questionIdentifier;
    private Type reqAnswer;

    public ResultRequirement(String questionID, Type enableWhenAnswer){
        questionIdentifier = questionID;
        reqAnswer = enableWhenAnswer;
    }

    public boolean isSatisfiedBy(TaskResult result){

        StepResult resultAnswer = result.getStepResult(questionIdentifier);
        resultAnswer.getResult();

        if (reqAnswer instanceof BooleanType){
            boolean reqBool = ((BooleanType) reqAnswer).booleanValue();
            Boolean ansBool = (Boolean)resultAnswer.getResult();

            if (reqBool == ansBool){
                return true;
            }
        }
        return false;
    }
}
