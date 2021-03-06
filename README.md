FHIRSTACK
-------
FHIRSTACK uses the [HAPI][hapi] FHIR library and [ResearchStack] in an attempt to bring the [C3-PRO] functionality to Android.

Combining [🔥 FHIR][fhir] and [ResearchStack], usually for data storage into [i2b2][], this framework allows you to use 
FHIR `Questionnaire` resources directly with a ResearchStack `ViewTaskActivity` and will return FHIR `QuestionnaireResponse` that 
you can send to your server.

### Usage

For now, this project contains a library module and an app module to show how to use the library. The code is commented with 
javadoc. A proper maven link and more instructions on how to use may follow soon.

#####The setup

In your app, create a subclass of `Application` and set it as your application in the AndroidManifest.
It seems that for now, multidex is needed to accomodate the large number of operations in the library's dependencies. So it is necessary to enable Multidex in the application and the gradle build file.
In your Application, initialize the FHIRStack with the application's context and your FHIR Server URL. You will also need to set up the some Research Stack options. For details about that visit the [ResearchStack website][researchstack].

The `Application`file should look something like this:
```java
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
        /**
         * Initialize FHIRStack:
         * FHIRStack will provide you with a FhirContext. This Object is expensive and you should
         * only have one instance in your app. Therefore, FHIRStack will keep it as a singleton.
         * Access it by calling FHIRStack.getFhirContext();
         * <p />
         * If you provide a context (your application) and an URL, FHIRStack
         * will create a DataQueue for you to create and read Resources from your server in a
         * background thread.
         * */
        FHIRStack.init(this, "http://fhirtest.uhn.ca/baseDstu3");

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
    }
}
```
The `AndroidManifest`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="yourID.fhirstack_sampleapp">

    <application
        android:name=".FHIRStackApplication"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Base.Theme.Backbone">

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```
The `build.gradle`:
```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.3"

    defaultConfig {
        applicationId "yourID.fhirstack_sampleapp"
        minSdkVersion 16
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
        // Enabling multidex support.
        multiDexEnabled true
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    dexOptions {
        javaMaxHeapSize "3g"
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'org.researchstack:backbone:1.0.0.rc4'
    compile 'org.researchstack:skin:1.0.0.rc4'
    compile 'com.android.support:multidex:1.0.1'
    compile project(':fhirstack')
}
```
##### The FHIRStack
Once set up (preferably in your Application class, so it survives the Activities' lifesycles), the FHIRStack will provide you with a HAPI `FhirContext` and a `DataQueue` if you have provided a FHIR Server URL.

You can access it anywhere in your app code, for example to get a `JsonParser`:
```java
FHIRStack.getFhirContext().newJsonParser();
```

Or to upload a resource:
```java
FHIRStack.getDataQueue.create(resource);
```
##### The QuestionnaireFragment
Use the `QuestionnaireFragment` to represent a Questionnaire and conduct a Survey based on it.
```java
private void launchSurvey(Questionnaire questionnaire) {
        /**
         * Looking up if a fragment for the given questionnaire has been created earlier. if so,
         * the survey is started, assuming that the TaskViewActivity has been created before!!
         * The questionnaire IDs are used for identification, assuming they are unique.
         * */
        QuestionnaireFragment fragment = (QuestionnaireFragment) getSupportFragmentManager().findFragmentByTag(questionnaire.getId());
        if (fragment != null) {
            /**
             * If the fragment has been added before, the TaskViewActivity is started
             * */
            fragment.startTaskViewActivity();
        } else {
            /**
             * If the fragment does not exist, we create it, add it to the fragment manager and
             * let it prepare the TaskViewActivity
             * */
            final QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
            questionnaireFragment.newInstance(questionnaire, new QuestionnaireFragment.QuestionnaireFragmentListener() {
                @Override
                public void whenTaskReady() {
                    /**
                     * Only when the task is ready, the survey is started
                     * */
                    questionnaireFragment.startTaskViewActivity();
                }

                @Override
                public void whenCompleted(QuestionnaireResponse questionnaireResponse) {
                    /**
                     * Where the response for a completed survey is received. Here it is printed
                     * to a TextView defined in the app layout.
                     * */
                    printQuestionnaireAnswers(questionnaireResponse);
                }

                @Override
                public void whenCancelledOrFailed() {
                    /**
                     * If the task can not be prepared, a backup plan is needed.
                     * Here the fragment is removed from the FragmentManager so it can be created
                     * again later
                     * TODO: proper error handling not yet implemented
                     * */
                    getSupportFragmentManager().beginTransaction().remove(questionnaireFragment).commit();
                }
            });

            /**
             * In order for the fragment to get the context and be found later on, it has to be added
             * to the fragment manager.
             * */
            getSupportFragmentManager().beginTransaction().add(questionnaireFragment, questionnaire.getId()).commit();
            /**
             * prepare the TaskViewActivity. As defined above, it will start the survey once the
             * TaskViewActivity is ready.
             * */
            questionnaireFragment.prepareTaskViewActivity();
        }
    }
```

### Versions

The library uses HAPI FHIR 1.5 for dstu3. Questionnaires in dstu2 (with group and question elements) will not work with this demo setup. 
Target Android sdk is 23, minimum sdk 16 due to ResearchStack.

### Issues

Implementation is ongoing, not everything is complete and nothing has been systematically tested.
- Answers to open choice questions ("open-choice", "http://hl7.org/fhir/answer-format") are not yet added to QuestionnaireResponse
- SampleData can not parse externally linked valueSets, they must be contained in the Questionnaire json-file
- EnableWhen conditions have only been tested with boolean answertypes
- No proper error handling implemented as of yet.

Modules
-------
The framework will consist of several modules that complement each other, similar to the C3-PRO framework.

### Questionnaires

Enables the conversion of a FHIR `Questionnaire` resource to a ResearchSTack `task` that can be presented to the user using a 
`ViewTaskActivity` and conversion back from a `TaskResult` to a FHIR `QuestionnaireResponse` resource.

### DataQueue

This module is in the works and will provide a FHIR server implementation used to move FHIR resources, created on device, to a FHIR 
server, without the need for user interaction nor -confirmation.

Licence
-------
This work will be [Apache 2][apache] licensed. A NOTICE.txt file will follow at some point, and don't forget to also add the licensing information of the submodules somewhere in your product:
- [ResearchStack][researchstack]
- [HAPI FHIR][hapi]
- [Android Priority Job Queue][jobqueue]

[hapi]: http://hapifhir.io
[researchstack]: http://researchstack.org
[C3-PRO]: http://c3-pro.org
[fhir]: http://hl7.org/fhir/
[researchkit]: http://researchkit.github.io
[i2b2]: https://www.i2b2.org
[apache]: http://www.apache.org/licenses/LICENSE-2.0
[jobqueue]: https://github.com/yigit/android-priority-jobqueue
