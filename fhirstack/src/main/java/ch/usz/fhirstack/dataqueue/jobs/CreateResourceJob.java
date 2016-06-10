package ch.usz.fhirstack.dataqueue.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ch.usz.fhirstack.FHIRStack;
import ch.usz.fhirstack.dataqueue.DataQueue;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 07.06.2016.
 * <p/>
 * This job will create the provided resource on the server at the FHIRServerURL.
 * It will persist, which means, the job will stay in the dataqueue until a network is available
 * the resurce is uploaded. If more than one Job is added to the dataqueue, they will be uploaded
 * FIFO. If no FHIRServerURL is provided, the serverURL from the FHIRStack will be used.
 */
public class CreateResourceJob extends Job {
    private IBaseResource uploadResource;
    private String serverURL;
    /**localID is needed for persistence*/
    private long localID;

    /**
     * Enqueues the resource to be uploaded to the provided FHIRServer. The job will persist even
     * when app state changes.
     * */
    public CreateResourceJob(IBaseResource FHIRResource, String FHIRServerURL){
        super(new Params(Priority.MID).requireNetwork().persist().groupBy(DataQueue.UPLOAD_GROUP_TAG));
        uploadResource = FHIRResource;
        serverURL = FHIRServerURL;
        localID = -System.currentTimeMillis();
    }

    /**
     * Enqueues the resource to be uploaded to the FHIRServer defined in the FHIRStack. The job will
     * persist even when app state changes.
     * */
    public CreateResourceJob(IBaseResource resource){
        this(resource, FHIRStack.getDataQueue().getFHIRServerURL());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        IGenericClient client = FHIRStack.getFhirContext().newRestfulGenericClient(serverURL);
        MethodOutcome outcome = client.create().resource(uploadResource).prettyPrint().encodedJson().execute();
        //TODO decide what to do when upload does not return anything
        Log.d("SENDJOBS", "created resource with id "+outcome.getId().getValue());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
