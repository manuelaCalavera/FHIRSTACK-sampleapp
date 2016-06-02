package manny.fhirstack_sampleapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.DatabaseHelper;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.storage.file.UnencryptedProvider;

import ca.uhn.fhir.context.FhirContext;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 28.04.2016.
 * <p/>
 * This is a sample application to show how to set up your app for using ResearchStack and FHIR.
 * The HAPI library is not yet optimized for use in android. It makes sense to create only one
 * FhirContext here and provide it where necessary in your app. This helps keeping the use of
 * resources minimal.
 * ResearchStack configurations are copied from the ResearchStack sample app
 *
 */
public class FHIRStackApplication extends Application {

    private FhirContext fhirContext;

    /* with older version of HAPI it seemed necessary to enable multidex to accomodate the large
    number of operations provided by the package. It seems to work without it now. Leaving it here
    for now just in case.
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    */

    @Override
    public void onCreate()
    {
        super.onCreate();

        // ResearchStack: Customize your pin code preferences
        PinCodeConfig pinCodeConfig = new PinCodeConfig(); // default pin config (4-digit, 1 min lockout)

        // ResearchStack: Customize encryption preferences
        EncryptionProvider encryptionProvider = new UnencryptedProvider(); // No pin, no encryption

        // ResearchStack: If you have special file handling needs, implement FileAccess
        FileAccess fileAccess = new SimpleFileAccess();

        // ResearchStack: If you have your own custom database, implement AppDatabase
        AppDatabase database = new DatabaseHelper(this,
                DatabaseHelper.DEFAULT_NAME,
                null,
                DatabaseHelper.DEFAULT_VERSION);

        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, fileAccess, database);

        /**
         * One FHIR context to be used anywhere in your app to parse jsons etc.
         * Access it anywehre:
         * FHIRStackApplication myApp = (FHIRStackApplication) getApplication();
         * FhirContext myContext = myApp.getFhirContext();
         * */
        this.fhirContext = FhirContext.forDstu3();
    }

    /**
     * Returns a FhirContext that can be used anywhere in the app. It is recommended not to create
     * a new FhirContext every time you need one because they take up quite a lot of resources.
     * */
    public FhirContext getFhirContext(){
        return this.fhirContext;
    }
}
