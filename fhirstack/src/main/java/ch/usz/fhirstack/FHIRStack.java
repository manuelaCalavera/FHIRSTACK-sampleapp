package ch.usz.fhirstack;

import android.content.Context;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;

import ca.uhn.fhir.context.FhirContext;
import ch.usz.fhirstack.dataqueue.DataQueue;

/**
 * Created by manny on 08.06.2016.
 */
public class FHIRStack {
    private static FhirContext fhirContext;
    private static JobManager jobManager;
    private static DataQueue dataQueue;
    private static String serverURL;


    private FHIRStack() {
    }

    public static void init(Context context, String FHIRServerURL) {
        serverURL = FHIRServerURL;
        initFhirContext();
        initJobManager(context);
        initDataQueue(FHIRServerURL);
    }

    public static void init(Context context) {
        initFhirContext();
        initJobManager(context);
    }

    public static void initFhirContext() {
        if (fhirContext == null) {
            fhirContext = FhirContext.forDstu3();
        }
    }

    public static void initJobManager(Context context) {
        if (jobManager == null) {
            jobManager = new JobManager(getDefaultBuilder(context).build());
        }
    }

    public static void initDataQueue(String FHIRServerURL){
        if (dataQueue == null){
            dataQueue = new DataQueue(FHIRServerURL, getJobManager());
        }
    }

    public static FhirContext getFhirContext() {
        return fhirContext;
    }

    public static JobManager getJobManager() {
        return jobManager;
    }

    public static DataQueue getDataQueue(){
        return dataQueue;
    }

    private static Configuration.Builder getDefaultBuilder(Context context) {
        Configuration.Builder builder = new Configuration.Builder(context)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBMANAGER";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        return builder;
    }
}
