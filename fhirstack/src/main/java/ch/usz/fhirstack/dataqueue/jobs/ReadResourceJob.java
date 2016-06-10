package ch.usz.fhirstack.dataqueue.jobs;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;


import ch.usz.fhirstack.FHIRStack;
import ch.usz.fhirstack.dataqueue.DataQueue;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 07.06.2016.
 * <p/>
 *
 */
public class ReadResourceJob extends Job {
    private static int HANDLER_MESSAGE_BUNDLE = 0;
    private String search;
    private String url;
    private DataQueue.BundleReceiver receiver;
    private Handler dataHandler;

    public ReadResourceJob(final String requestID, String searchURL, DataQueue.BundleReceiver resourceReceiver, String FHIRServerURL){
        super(new Params(Priority.HIGH).requireNetwork().singleInstanceBy(requestID));
        search = searchURL;
        url = FHIRServerURL;
        receiver = resourceReceiver;
        dataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_MESSAGE_BUNDLE) {
                    org.hl7.fhir.dstu3.model.Bundle bundle = (org.hl7.fhir.dstu3.model.Bundle)msg.obj;
                    receiver.receiveBundle(requestID, bundle);
                } else {
                    //TODO error handling
                }
            }
        };
    }

    public ReadResourceJob(String requestID, String searchURL, DataQueue.BundleReceiver resourceReceiver){
        this(requestID, searchURL, resourceReceiver, FHIRStack.getDataQueue().getFHIRServerURL());
    }


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        org.hl7.fhir.dstu3.model.Bundle response = FHIRStack.getFhirContext().newRestfulGenericClient(url).search()
                .byUrl(search)
                .returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
                .execute();
        Message msg = new Message();
        msg.what = HANDLER_MESSAGE_BUNDLE;
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
