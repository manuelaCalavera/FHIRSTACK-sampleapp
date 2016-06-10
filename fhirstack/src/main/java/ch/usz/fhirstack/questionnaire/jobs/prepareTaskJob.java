package ch.usz.fhirstack.questionnaire.jobs;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.hl7.fhir.dstu3.model.Questionnaire;
import org.researchstack.backbone.task.Task;


import ch.usz.fhirstack.dataqueue.jobs.Priority;
import ch.usz.fhirstack.questionnaire.QuestionnaireFragment;
import ch.usz.fhirstack.questionnaire.logic.Questionnaire2Task;

/**
 * Created by manny on 09.06.2016.
 */
public class PrepareTaskJob extends Job {
    private Handler dataHandler;
    private Questionnaire questionnaire;

    public PrepareTaskJob(Questionnaire FHIRQuestionnaire, Handler handler) {
        super(new Params(Priority.HIGH));
        dataHandler = handler;
        questionnaire = FHIRQuestionnaire;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Task task = Questionnaire2Task.questionnaire2Task(questionnaire);
        Message msg = new Message();
        msg.what = QuestionnaireFragment.HANDLER_MESSAGE_TASK;
        msg.obj = task;
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
