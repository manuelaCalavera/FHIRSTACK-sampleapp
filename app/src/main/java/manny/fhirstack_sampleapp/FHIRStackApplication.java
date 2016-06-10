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

import ch.usz.fhirstack.FHIRStack;
import ch.usz.fhirstack.dataqueue.DataQueue;

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
 */
public class FHIRStackApplication extends Application {

    /* with HAPI it seemed necessary to enable multidex to accomodate the large
    number of operations provided by the package. It seems to work without it now. Leaving it here
    for now just in case.*/
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
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
         * Initialize FHIRStack:
         * FHIRStack will provide you with a FhirContext. This Object is expensive and you should
         * only have one instance in your app. Therefore, FHIRStack will keep it as a singleton.
         * Access it by calling FHIRStack.getFhirContext();
         * <p />
         * If you provide a context (your application) and an URL, FHIRStack
         * will create a DataQueue for you to create and read Resources from your server in a
         * background service.
         * */
        FHIRStack.init(this, "http://fhirtest.uhn.ca/baseDstu3");
    }
}
