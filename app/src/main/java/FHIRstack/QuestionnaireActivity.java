package fhirstack;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;

import org.hl7.fhir.dstu3.model.Questionnaire;
import sampledata.SampleData;


/**
 * Created by manny on 28.04.2016.
 */
public class QuestionnaireActivity extends ViewTaskActivity {

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, QuestionnaireActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    public static Intent newIntent(Context context, Questionnaire questionnaire)
    {

        //TODO create task from Questionnaire here //

        return ViewTaskActivity.newIntent(context, SampleData.getTask());
    }

    @Override
    public void finish()
    {

        //TODO add QuestionnaireResult to intent here //
        Log.d("YAY","it's happening!!");
        super.finish();
    }

}
