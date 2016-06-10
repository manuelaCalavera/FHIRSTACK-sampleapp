package ch.usz.fhirstack.questionnaire.jobs;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.researchstack.backbone.result.TaskResult;


import ch.usz.fhirstack.dataqueue.jobs.Priority;
import ch.usz.fhirstack.questionnaire.QuestionnaireFragment;
import ch.usz.fhirstack.questionnaire.logic.TaskResult2QuestionnaireResponse;

/**
 * Created by manny on 09.06.2016.
 */
public class QuestionnaireResponseJob extends Job {
    public static final String LTAG = "FSTK";
    private TaskResult result;
    private Handler mHandler;

    public QuestionnaireResponseJob(TaskResult taskResult, Handler handler){
        super(new Params(Priority.HIGH));
        result = taskResult;
        mHandler = handler;

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        QuestionnaireResponse response = TaskResult2QuestionnaireResponse.taskResult2QuestionnaireResponse(result);
        Message msg = new Message();
        msg.what = QuestionnaireFragment.HANDLER_MESSAGE_RESPONSE;
        msg.obj = response;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

}
