package ch.usz.fhirstack.dataqueue;

import android.os.Handler;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ch.usz.fhirstack.dataqueue.jobs.CreateResourceJob;
import ch.usz.fhirstack.dataqueue.jobs.ReadResourceJob;


/**
 * Created by manny on 06.06.2016.
 */
public class DataQueue {
    public static String UPLOAD_GROUP_TAG = "FHIR_UPLOAD_GROUP";

    private JobManager jobManager;
    private String server;
    private Handler dataHandler;

    public interface BundleReceiver {
        public void receiveBundle(String requestID, org.hl7.fhir.dstu3.model.Bundle resource);
    }

    public DataQueue(String FHIRServerURL, JobManager manager) {
        jobManager = manager;
        server = FHIRServerURL;
    }

    public void create (IBaseResource resource){
        CreateResourceJob job = new CreateResourceJob(resource, server);
        jobManager.addJobInBackground(job);
    }

    public void read(String requestID, String searchURL, BundleReceiver resourceReceiver){
        ReadResourceJob job = new ReadResourceJob(requestID, searchURL, resourceReceiver);
        jobManager.addJobInBackground(job);
    }

    public void addJob(Job job){
        jobManager.addJobInBackground(job);
    }

    public String getFHIRServerURL(){
        return server;
    }
}
