package fhirstack;

import android.content.Intent;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.StringType;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;

import java.util.Date;
import java.util.Map;

/**
 * Created by manny on 23.05.2016.
 */
public class TaskResult2QuestionnaireResponse {
    public static QuestionnaireResponse taskResult2QuestionnaireResponse(TaskResult taskResult) {
        QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        Map<String, StepResult> stepResults = taskResult.getResults();

        for (Map.Entry<String, StepResult> entry : stepResults.entrySet()) {
            String key = entry.getKey();
            StepResult stepResult = entry.getValue();
            QuestionnaireResponse.QuestionnaireResponseItemComponent responseItem = stepResult2ResponseItem(stepResult);
            if (responseItem != null) {
                responseItem.setLinkId(key);
                questionnaireResponse.addItem(responseItem);
            }
        }

        return questionnaireResponse;
    }

    public static QuestionnaireResponse.QuestionnaireResponseItemComponent stepResult2ResponseItem(StepResult stepResult) {

        if ((stepResult != null) && (stepResult.getResult() != null)) {
            QuestionnaireResponse.QuestionnaireResponseItemComponent responseItem = new QuestionnaireResponse.QuestionnaireResponseItemComponent();
            responseItem.addAnswer(getFHIRAnswerForStepResult(stepResult));
            return responseItem;
        } else {
            return null;
        }
    }

    public static QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent getFHIRAnswerForStepResult(StepResult stepResult) {

        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerComponent = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
        if (stepResult.getAnswerFormat() instanceof BooleanAnswerFormat) {
            answerComponent.setValue(new BooleanType((Boolean) stepResult.getResult()));
        } else if (stepResult.getAnswerFormat() instanceof IntegerAnswerFormat) {
            answerComponent.setValue(new IntegerType((int) stepResult.getResult()));
        } else if (stepResult.getAnswerFormat() instanceof DateAnswerFormat) {
            answerComponent.setValue(new DateType((Date) stepResult.getResult()));
        } else if (stepResult.getAnswerFormat() instanceof TextAnswerFormat) {
            answerComponent.setValue(new StringType((String) stepResult.getResult()));
        } else if (stepResult.getAnswerFormat() instanceof ChoiceAnswerFormat) {
            // TODO ChoiceAnswers
            answerComponent.setValue(new StringType((String) stepResult.getResult()));
        }

        return answerComponent;
    }

    public static QuestionnaireResponse resultIntent2QuestionnaireResponse(Intent data) {

        if (data != null) {
            TaskResult taskResult = (TaskResult) data.getExtras().get(ViewTaskActivity.EXTRA_TASK_RESULT);
            if (taskResult != null) {
                return taskResult2QuestionnaireResponse(taskResult);
            }
        }
        return null;
    }
}
