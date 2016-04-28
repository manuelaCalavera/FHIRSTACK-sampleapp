package manny.fhirstack_sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.parser.IParser;
import sampledata.SampleData;

//survey stuff
import org.researchstack.backbone.StorageAccess;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;


public class MainActivity extends PinCodeActivity {

    // Activity Request Codes
    private static final int REQUEST_SURVEY = 1;

    //survey stuff task/step identifiers
    public static final String INSTRUCTION = "identifier";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String NUTRITION = "nutrition";
    public static final String MULTI_STEP = "multi_step";
    public static final String SAMPLE_SURVEY = "sample_survey";
    public static final String FORM_STEP = "form_step";
    public static final String BASIC_INFO_HEADER = "basic_info_header";
    private static final String FORM_NAME = "form_name";
    public static final String FORM_AGE = "form_age";
    public static final String FORM_GENDER = "gender";
    public static final String FORM_MULTI_CHOICE = "multi_choice";
    public static final String FORM_DATE_OF_BIRTH = "date_of_birth";

    //views
    private AppCompatButton surveyButton;
    private AppCompatButton clearButton;
    private boolean hasSurveyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FhirContext ctxDstu2 = FhirContext.forDstu2();
        Log.d("FHIR", "context created!");

        IParser parser = ctxDstu2.newXmlParser();

        String sampleString = SampleData.getPatientString();

        Patient patient = parser.parseResource(Patient.class, sampleString);

        ((TextView) findViewById(R.id.displayText)).setText(patient.getName().get(0).getFamily().get(0).getValue());

        surveyButton = (AppCompatButton) findViewById(R.id.survey_button);
        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey();
            }
        });
        clearButton = (AppCompatButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Toast.makeText(MainActivity.this, R.string.menu_data_cleared, Toast.LENGTH_SHORT).show();
            }
        });

    }


    // setup stuff


    private void clearData() {
        hasSurveyed = false;
        initViews();
    }

    /*
        @Override
        public void onDataReady()
        {
            super.onDataReady();
            initViews();
        }
    */
    private void initViews() {

        TextView surveyAnswer = (TextView) findViewById(R.id.survey_results);

        if (hasSurveyed) {
            surveyAnswer.setVisibility(View.VISIBLE);
            printSurveyInfo(surveyAnswer);
        } else {
            surveyAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SURVEY && resultCode == RESULT_OK) {
            processSurveyResult((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
        }
    }


    // Survey Stuff

    private void launchSurvey() {


        // Create an activity using the task and set a delegate.
        Intent intent = ViewTaskActivity.newIntent(this, SampleData.getTask());
        //Intent intent = QuestionnaireActivity.newIntent(this, SampleData.getTask());
        startActivityForResult(intent, REQUEST_SURVEY);
    }

    private void processSurveyResult(TaskResult result) {
        StorageAccess.getInstance().getAppDatabase().saveTaskResult(result);

        Log.d("processSurveyResult", "setting hasSurveyed to true");
        Log.d("processSurveyResult", result.toString());

        hasSurveyed = true;
        initViews();
    }

    private void printSurveyInfo(TextView surveyAnswer) {

        Log.d("printSurveyInfo", "hassurveyed is " + hasSurveyed);
        TaskResult taskResult = StorageAccess.getInstance()
                .getAppDatabase()
                .loadLatestTaskResult(SAMPLE_SURVEY);

        String results = "";
        if (taskResult != null) {
            for (String id : taskResult.getResults().keySet()) {
                StepResult stepResult = taskResult.getStepResult(id);
                if (stepResult.getResult() != null) {
                    results += id + ": " + stepResult.getResult().toString() + "\n";
                }
            }
        }
        surveyAnswer.setText(results);
    }


}
