package ch.usz.fhirstack.dataqueue;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.researchstack.backbone.task.Task;

import ca.uhn.fhir.rest.client.IGenericClient;
import ch.usz.fhirstack.dataqueue.jobs.CreateResourceJob;
import ch.usz.fhirstack.dataqueue.jobs.ReadResourceJob;


/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 07.06.2016.
 * <p/>
 * This DataQueue will manage async jobs to upload and download data from the FHIRServer as well as
 * converting files between HAPI FHIR and ResearchStack. It is provided by the FHIRStack class.
 * Set up initialize the FHIRStack class in the onCreate method of your application and access the
 * Queue through it.
 */
public class DataQueue {
    public static String UPLOAD_GROUP_TAG = "FHIR_UPLOAD_GROUP";

    private JobManager jobManager;
    private String server;

    /**
     * The BundleReceiver interface is used to pass back downloaded resources in a FHIR Bundle.
     * */
    public interface BundleReceiver {
        public void receiveBundle(String requestID, org.hl7.fhir.dstu3.model.Bundle resource);
    }

    /**
     * The TaskReceiver interface is used to pass back Tasks that were created from Questionnaires.
     * */
    public interface TaskReceiver {
        public void receiveTask(Task task);
    }

    /**
     * The TaskReceiver interface is used to pass back Tasks that were created from Questionnaires.
     * */
    public interface QuestionnaireResponseReceiver {
        public void receiveResponse(QuestionnaireResponse questionnaireResponse);
    }

    /**
     * Interface needed for a HAPIQueryJob. Implement the runQuery method and run a HAPI Query on the
     * provided client.
     */
    public interface QueryPoster {
        public void runQuery(IGenericClient client);
    }

    /**
     * The DataQueue needs the URL to a FHIR Server and a JobManager to run. A DataQueue is provided
     * as a singleton by the FHIRStack class, no need to have another instance of it around!
     * */
    public DataQueue(String FHIRServerURL, JobManager manager) {
        jobManager = manager;
        server = FHIRServerURL;
    }

    /**
     * Creates the FHIR resource on the server provided at the setup of FHIRStack.
     * */
    public void create(IBaseResource resource) {
        CreateResourceJob job = new CreateResourceJob(resource, server);
        jobManager.addJobInBackground(job);
    }

    /**
     * searchURL defines the search, can be absolute or relative to the FHIRServerURL defined in
     * the FHIRStack, where the resource is loaded from. requestID will be passed back for
     * identification with the result to the resourceReceiver.
     * */
    public void read(String requestID, String searchURL, BundleReceiver resourceReceiver) {
        ReadResourceJob job = new ReadResourceJob(requestID, searchURL, resourceReceiver);
        jobManager.addJobInBackground(job);
    }

    /**
     * The class Job can be subclassed to run custom jobs asynchronously through the DataQueue
     * */
    public void addJob(Job job) {
        jobManager.addJobInBackground(job);
    }

    /**
     * returns the URL which is setup with the FHIRStack
     * */
    public String getFHIRServerURL() {
        return server;
    }
}
