package ch.usz.fhirstack.dataqueue.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import ch.usz.fhirstack.FHIRStack;

/**
 * Created by manny on 07.06.2016.
 */
public class HAPIQueryJob extends Job {
    private QueryPoster queryPoster;
    private String url;

    public HAPIQueryJob(String singleInstanceID, QueryPoster poster, String FHIRServerURL){
        super(new Params(Priority.HIGH).requireNetwork().singleInstanceBy(singleInstanceID));
        queryPoster = poster;
        url = FHIRServerURL;
    }

    public HAPIQueryJob(String singleInstanceID, QueryPoster poster){
        this(singleInstanceID, poster, FHIRStack.getDataQueue().getFHIRServerURL());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        queryPoster.runQuery(FHIRStack.getFhirContext().newRestfulGenericClient(url));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    /* This is how you use this class
      public void runQuery(){
        HAPIQueryJob job = new HAPIQueryJob("instanceID", new QueryPoster() {
            @Override
            public void runQuery(IGenericClient client) {
                org.hl7.fhir.dstu3.model.Bundle results = client.search()
                        .forResource(Patient.class)
                        .where(Patient.NAME.matches().value("Smith"))
                        .returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
                        .execute();
                // do something with the result
            }
        });
        FHIRStack.getDataQueue().addJob(job);
    }*/
}
