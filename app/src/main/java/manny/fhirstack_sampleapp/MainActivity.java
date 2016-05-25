package manny.fhirstack_sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;


import fhirstack.Questionnaire2Task;
import fhirstack.TaskResult2QuestionnaireResponse;
import sampledata.SampleData;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;


public class MainActivity extends AppCompatActivity {

    // Activity Request Codes
    private static final int TEXTVALUES_SURVEY = 1;
    private static final int CHOICES_SURVEY = 2;
    private static final int DATES_SURVEY = 3;
    private static final int VALUESETCONTAINED_SURVEY = 4;
    private static final int VALUESETRELATIVE_SURVEY = 5;

    //views
    private AppCompatButton survey1Button;
    private AppCompatButton survey2Button;
    private AppCompatButton survey3Button;
    private AppCompatButton clearButton;
    private AppCompatTextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        survey1Button = (AppCompatButton) findViewById(R.id.survey1_button);
        survey1Button.setText("questionnaire text values");
        survey1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey(R.raw.questionnaire_textvalues, TEXTVALUES_SURVEY);
            }
        });

        survey1Button = (AppCompatButton) findViewById(R.id.survey2_button);
        survey1Button.setText("questionnaire choices");
        survey1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey(R.raw.questionnaire_choices, CHOICES_SURVEY);
            }
        });

        survey1Button = (AppCompatButton) findViewById(R.id.survey3_button);
        survey1Button.setText("questionnaire dates");
        survey1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey(R.raw.questionnaire_dates, DATES_SURVEY);
            }
        });

        survey2Button = (AppCompatButton) findViewById(R.id.survey4_button);
        survey2Button.setText("questionnaire valueset contained");
        survey2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey(R.raw.questionnaire_valueset_contained, VALUESETCONTAINED_SURVEY);
            }
        });

        survey3Button = (AppCompatButton) findViewById(R.id.survey5_button);
        survey3Button.setText("questionnaire valueset relative");
        survey3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSurvey(R.raw.questionnaire_valueset_realative, VALUESETRELATIVE_SURVEY);
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

        resultView = (AppCompatTextView) findViewById(R.id.result_textView);
        resultView.setText("Welcome");
    }


    private void launchSurvey(int rawID, int requestID) {
        FHIRStackApplication myApp = (FHIRStackApplication) getApplication();

        Questionnaire questionnaire = SampleData.getQuestionnaireFromJson(myApp.getFhirContext(), getResources(), rawID);

        /*
        * This is how you launch a ViewTaskActivity (must be declared in AndroidManifest!) from a FHIR Questionnaire
        * */
        Task task = Questionnaire2Task.questionnaire2Task(questionnaire);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, requestID);
    }

    /*
    * This is where you get the results back. ViewTaskActivity will return A TaskResult in the data Intent
    * It can be read from the data or directly passed on to TaskResult2QuestionnaireResponse to get a FHIR resource from it
    * To get the TaskResult:
    * TaskResult taskResult = (TaskResult) data.getExtras().get(ViewTaskActivity.EXTRA_TASK_RESULT);
    * To get the QuestionnaireResponse:
    * QuestionnaireResponse response = TaskResult2QuestionnaireResponse.resultIntent2QuestionnaireResponse(data);
    * or
    * QuestionnaireResponse response = TaskResult2QuestionnaireResponse.resultIntent2QuestionnaireResponse(taskResult);
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TEXTVALUES_SURVEY:
                    // DO Whatever
                    break;
                case CHOICES_SURVEY:
                    // DO Whatever
                    break;
                case DATES_SURVEY:
                    // DO Whatever
                    break;
                default:
                    // DO Whatever
            }
            QuestionnaireResponse response = TaskResult2QuestionnaireResponse.resultIntent2QuestionnaireResponse(data);
            printQuestionnaireAnswers(response);
        }
    }



    private void printQuestionnaireAnswers(QuestionnaireResponse response) {
        String results = "";
        results = ((FHIRStackApplication) getApplication()).getFhirContext().newJsonParser().encodeResourceToString(response);
        resultView.setText(results);
    }

    private void clearData() {
        resultView.setText("");
    }

}
