package ch.usz.fhirstack.dataqueue.jobs;

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
 * This job can be used to asynchronously run a HAPI query. If no FHIRServerURL is provided, the
 * URL set in the dataqueue will be used. Jobs with the same singleInstanceID will only run if no
 * other job with same ID is present in the queue. Define a QueryPoster with the query in the
 * runQuery method.
 * Be aware that the query will run on a background thread. It won't be possible to access the UI
 * from it. Use a Handler to send the result to the main thread first.
 *
 * This is how you use this class:
 * public void runQuery(){
 * HAPIQueryJob job = new HAPIQueryJob("instanceID", new QueryPoster() {
 * @Override
 * public void runQuery(IGenericClient client) {
 * org.hl7.fhir.dstu3.model.Bundle results = client.search()
 * .forResource(Patient.class)
 * .where(Patient.NAME.matches().value("Smith"))
 * .returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
 * .execute();
 * // do something with the result
 * }
 * });
 * FHIRStack.getDataQueue().addJob(job);
 * */
public class HAPIQueryJob extends Job {
    private DataQueue.QueryPoster queryPoster;
    private String url;

    /**
     * The QueryPoster will get a generic HAPI client for the specified URL on which it can run its
     * query. If you add multiple jobs to the queue with the same singleINstanceID, only one will run.
     * */
    public HAPIQueryJob(String singleInstanceID, DataQueue.QueryPoster poster, String FHIRServerURL){
        super(new Params(Priority.HIGH).requireNetwork().singleInstanceBy(singleInstanceID));
        queryPoster = poster;
        url = FHIRServerURL;
    }

    /**
     * The QueryPoster will get a generic HAPI client for the URL specified in the FHIRStack on
     * which it can run its query. If you add multiple jobs to the queue with the same
     * singleINstanceID, only one will run.
     * */
    public HAPIQueryJob(String singleInstanceID, DataQueue.QueryPoster poster){
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
}
