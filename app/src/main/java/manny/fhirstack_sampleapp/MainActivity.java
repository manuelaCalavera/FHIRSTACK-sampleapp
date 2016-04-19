package manny.fhirstack_sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.parser.IParser;
import sampledata.SampleData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FhirContext ctxDstu2 = FhirContext.forDstu2();
        Log.d("FHIR", "context created!");

        IParser parser = ctxDstu2.newXmlParser();

        String sampleString = SampleData.getPatientString();

        Patient patient = parser.parseResource(Patient.class, sampleString);

        ((TextView)findViewById (R.id.displayText)).setText (patient.getName().get(0).getFamily().get(0).getValue());


    }
}
