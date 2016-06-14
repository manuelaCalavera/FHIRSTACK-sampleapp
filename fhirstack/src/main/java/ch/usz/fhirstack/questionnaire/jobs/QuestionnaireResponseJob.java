package ch.usz.fhirstack.questionnaire.jobs;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;


import ch.usz.fhirstack.dataqueue.DataQueue;
import ch.usz.fhirstack.dataqueue.jobs.Priority;
import ch.usz.fhirstack.questionnaire.QuestionnaireFragment;
import ch.usz.fhirstack.questionnaire.logic.TaskResult2QuestionnaireResponse;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 09.06.2016.
 * <p/>
 * This job is used by the DataQueue to convert ResearchStack TaskResult to a FHIR QuestionnaireResponse
 * in a background thread. The handler will move the result to the main (UI) thread, so it can be used
 * to update the UI.
 */
public class QuestionnaireResponseJob extends Job {
    public static final String LTAG = "FSTK";
    private static int HANDLER_MESSAGE_RESPONSE_READY = 0;
    private TaskResult result;
    private Handler dataHandler;

    /**
     * The TaskResult provided will be converted to a FHIR QuestionnaireResponse in a background
     * thread and passed back to the taskReceiver when done.
     * */
    public QuestionnaireResponseJob(TaskResult taskResult, final DataQueue.QuestionnaireResponseReceiver responseReceiver){
        super(new Params(Priority.HIGH));
        result = taskResult;
        dataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_MESSAGE_RESPONSE_READY) {
                    QuestionnaireResponse response = (QuestionnaireResponse) msg.obj;
                    responseReceiver.receiveResponse(response);
                } else {
                    //TODO error handling
                }
            }
        };
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        QuestionnaireResponse response = TaskResult2QuestionnaireResponse.taskResult2QuestionnaireResponse(result);
        Message msg = new Message();
        msg.what = HANDLER_MESSAGE_RESPONSE_READY;
        msg.obj = response;
        dataHandler.sendMessage(msg);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

}
