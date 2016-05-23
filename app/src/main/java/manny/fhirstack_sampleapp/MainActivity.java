package manny.fhirstack_sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;


import fhirstack.Questionnaire2Task;
import sampledata.SampleData;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;


public class MainActivity extends AppCompatActivity {

    // Activity Request Codes
    private static final int REQUEST_SURVEY = 1;
    private static final int TEXTVALUES_SURVEY = 2;
    private static final int CHOICES_SURVEY = 3;
    private static final int DATES_SURVEY = 4;
    private static final int VALUESETCONTAINED_SURVEY = 5;
    private static final int VALUESETRELATIVE_SURVEY = 4;


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TEXTVALUES_SURVEY:
                    resultView.setText("Textvalue Survey done");
                case CHOICES_SURVEY:
                    resultView.setText("Choices Survey done");
                case DATES_SURVEY:
                    resultView.setText("Dates Survey done");
                case REQUEST_SURVEY:
                default:
                    resultView.setText("Survey done");
            }
        }
    }

    private void launchSurvey(int rawID, int requestID) {
        FHIRStackApplication myApp = (FHIRStackApplication) getApplication();

        Questionnaire questionnaire = SampleData.getQquestionnaireFromJson(myApp.getFhirContext(), getResources(), rawID);

        /*
        * This is how you launch a VieTaskActivity (must be declared in AndroidManifest!) from a FHIR Questionnaire
        * */
        Task task = Questionnaire2Task.questionnaire2Task(questionnaire);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, requestID);
    }

    private void printQuestionnaireAnswers() {
        String results = "";
        //TODO get questionnaire answers
        resultView.setText(results);
    }

    private void clearData() {
        resultView.setText("");
    }

}
