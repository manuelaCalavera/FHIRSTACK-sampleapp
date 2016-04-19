package manny.fhirstack_sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ca.uhn.fhir.context.FhirContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FhirContext ctxDstu2 = FhirContext.forDstu2();
        Log.d("FHIR", "context created!");

    }
}
