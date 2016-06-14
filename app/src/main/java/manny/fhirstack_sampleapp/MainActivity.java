package manny.fhirstack_sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ch.usz.fhirstack.FHIRStack;
import ch.usz.fhirstack.questionnaire.QuestionnaireFragment;
import sampledata.SampleData;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 19.04.2016.
 * <p/>
 * This is the main activity of the sample application to show how you can use FHIRSTACK to conduct
 * a survey you have as a HAPI FHIR Questionnaire
 */
public class MainActivity extends AppCompatActivity {
    public static final String LTAG = "FSTK";
    // Activity Request Code used when starting a survey from launchSurvey()
    private static final int SURVEY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Adding an Item to the ListView for every questionnaire file in the raw resource folder
         * */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SampleData.getAllRawResources());
        final ListView surveyListView = (ListView) findViewById(R.id.survey_list);
        surveyListView.setAdapter(adapter);

        surveyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int rawID = getResources().getIdentifier((String) surveyListView.getItemAtPosition(position),
                        "raw", getPackageName());
                Questionnaire questionnaire = SampleData.getQuestionnaireFromJson(getResources(), rawID);
                launchSurvey(questionnaire);
            }
        });


        AppCompatButton clearButton = (AppCompatButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Toast.makeText(MainActivity.this, R.string.menu_data_cleared, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * launch a survey from a json provided in the raw folder. rawID is the resource id that can be
     * accessed by R.raw.filename, requistID will be passed to the onActivityResult callback to
     * identify the started survey.
     */
    private void launchSurvey(Questionnaire questionnaire) {

        final QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
        questionnaireFragment.newInstance(questionnaire, new QuestionnaireFragment.QuestionnaireFragmentListener() {
            @Override
            public void whenTaskReady() {
                questionnaireFragment.startTaskViewActivity();
            }

            @Override
            public void whenCompleted(QuestionnaireResponse questionnaireResponse) {
                printQuestionnaireAnswers(questionnaireResponse);
            }

            @Override
            public void whenCancelledOrFailed() {

            }
        });

        getSupportFragmentManager().beginTransaction().add(questionnaireFragment, questionnaire.getId()).commit();

        Log.d(LTAG, "fragment commited: "+ questionnaire.getId());
        if (getSupportFragmentManager().getFragments() != null) {
            Log.d(LTAG, "number of fragments: " + getSupportFragmentManager().getFragments().size());
        }
        questionnaireFragment.prepareTaskViewActivity();



        /*
        * This is how you launch a ViewTaskActivity from a FHIR Questionnaire
        * The activity must be declared in the AndroidManifest!
        *
        Task task = Questionnaire2Task.questionnaire2Task(questionnaire);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, requestID);*/
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
    *
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LTAG, "shön blöd");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SURVEY_REQUEST:
                    QuestionnaireResponse response = TaskResult2QuestionnaireResponse.resultIntent2QuestionnaireResponse(data);
                    printQuestionnaireAnswers(response);
                    FHIRStack.getDataQueue().create(response);
                    break;
            }
        }
    }*/

    /**
     * prints the QuestionnaireResponse into the textView of the main activity under the list of questionnaires.
     */
    private void printQuestionnaireAnswers(QuestionnaireResponse response) {
        String results = FHIRStack.getFhirContext().newJsonParser().encodeResourceToString(response);
        AppCompatTextView resultView = (AppCompatTextView) findViewById(R.id.result_textView);
        resultView.setText(results);
    }

    /**
     * clears the data in the textView of the main activity under the list of questionnaires
     */
    private void clearData() {
        AppCompatTextView resultView = (AppCompatTextView) findViewById(R.id.result_textView);
        resultView.setText("");
    }
}

