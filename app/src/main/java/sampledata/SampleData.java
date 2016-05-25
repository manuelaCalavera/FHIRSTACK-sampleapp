package sampledata;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import ca.uhn.fhir.context.FhirContext;

import org.hl7.fhir.dstu3.model.Questionnaire;

import ca.uhn.fhir.parser.IParser;

/**
 * Created by manny on 19.04.2016.
 */
public class SampleData extends AppCompatActivity {
    //survey stuff task/step identifiers
    public static final String INSTRUCTION = "identifier";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String NUTRITION = "nutrition";
    public static final String MULTI_STEP = "multi_step";
    public static final String SAMPLE_SURVEY = "sample_survey";
    public static final String FORM_STEP = "form_step";
    public static final String BASIC_INFO_HEADER = "basic_info_header";
    private static final String FORM_NAME = "form_name";
    public static final String FORM_AGE = "form_age";
    public static final String FORM_GENDER = "gender";
    public static final String FORM_MULTI_CHOICE = "multi_choice";
    public static final String FORM_DATE_OF_BIRTH = "date_of_birth";


    public static Questionnaire getQquestionnaireFromJson(FhirContext fhirContext, Resources res, int rawID) {


        IParser parser = fhirContext.newJsonParser();

        String json = getJasonAsString(res, rawID);

        Questionnaire questionnaire = parser.parseResource(Questionnaire.class, json);

        return questionnaire;

    }

    public static String getJasonAsString(Resources res, int rawID) {

        //InputStream is = res.openRawResource(R.raw.questionnaire_textvalues);
        InputStream is = res.openRawResource(rawID);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }


    public static String readFile(String path) {
        File file = new File(path);
        String content = "";
        try {

            content = getFileContents(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("readFile", content);
        return content;
    }

    public static String getFileContents(final File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        boolean done = false;

        while (!done) {
            final String line = reader.readLine();
            done = (line == null);

            if (line != null) {
                stringBuilder.append(line);
            }
        }

        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }
}
