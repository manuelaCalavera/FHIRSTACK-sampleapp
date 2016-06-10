package ch.usz.fhirstack.dataqueue.jobs;

import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Created by manny on 07.06.2016.
 */
public interface QueryPoster {
    public void runQuery(IGenericClient client);
}
