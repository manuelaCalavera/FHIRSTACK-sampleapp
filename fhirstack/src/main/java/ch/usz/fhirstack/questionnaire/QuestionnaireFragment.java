package ch.usz.fhirstack.questionnaire;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;

import ch.usz.fhirstack.FHIRStack;
import ch.usz.fhirstack.questionnaire.jobs.PrepareTaskJob;
import ch.usz.fhirstack.questionnaire.jobs.QuestionnaireResponseJob;

/**
 * Created by manny on 09.06.2016.
 */
public class QuestionnaireFragment extends Fragment {
    public static final String LTAG = "FSTK";
    public static final int TASKVIEW_REQUEST_ID = 12345;
    public static final int HANDLER_MESSAGE_TASK = 0;
    public static final int HANDLER_MESSAGE_RESPONSE = 1;

    private Questionnaire questionnaire;
    private Task mTask;
    private QuestionnaireFragmentListener mCallback;
    private Handler dataHandler;

    public interface QuestionnaireFragmentListener {
        public abstract void whenTaskReady();

        public abstract void whenCompleted(QuestionnaireResponse questionnaireResponse);

        public abstract void whenCancelledOrFailed();
    }


    public void init(Questionnaire FHIRQuestionnaire, QuestionnaireFragmentListener listener) {
        questionnaire = FHIRQuestionnaire;
        mCallback = listener;
        dataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_MESSAGE_RESPONSE) {
                    QuestionnaireResponse response = (QuestionnaireResponse)msg.obj;
                    mCallback.whenCompleted(response);

                } else if (msg.what == HANDLER_MESSAGE_TASK) {
                    mTask = (Task)msg.obj;
                    mCallback.whenTaskReady();
                } else {
                    //TODO error handling
                }
            }
        };
    }

    public void prepareTaskViewActivity() {
        PrepareTaskJob job = new PrepareTaskJob(questionnaire, dataHandler);
        FHIRStack.getJobManager().addJobInBackground(job);
    }

    public void startTaskViewActivity() {
        //TODO error handling when task not prepared yet
        if (getContext() == null) {
            Log.e(LTAG, "context null in qFragment");
        } else {
            Intent intent = ViewTaskActivity.newIntent(getContext(), mTask);
            startActivityForResult(intent, TASKVIEW_REQUEST_ID);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TASKVIEW_REQUEST_ID) {
            switch (resultCode) {
                case AppCompatActivity.RESULT_OK:
                    TaskResult taskResult = (TaskResult) data.getExtras().get(ViewTaskActivity.EXTRA_TASK_RESULT);
                    QuestionnaireResponseJob job = new QuestionnaireResponseJob(taskResult, dataHandler);
                    FHIRStack.getJobManager().addJobInBackground(job);
                    break;
                case AppCompatActivity.RESULT_CANCELED:
                    mCallback.whenCancelledOrFailed();
            }
        }
    }

    //fragment stuff
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }
}
