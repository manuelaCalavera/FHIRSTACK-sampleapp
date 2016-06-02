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
import java.lang.reflect.Field;
import java.util.ArrayList;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.hl7.fhir.dstu3.model.Questionnaire;

import manny.fhirstack_sampleapp.R;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 19.04.2016.
 * <p/>
 * This class can provide sampledate to be used with FHIRSTACK. This is only for demonstration
 * purposes. Use your own resources for your apps.
 */
public class SampleData extends AppCompatActivity {

    /**
     * returns a Questionnaire from the jason with corresponding to the rawID from the "raw" resource
     * folder. A FhirContext is needed for the parser, so we don't have to create a new context
     * every time a file has to be parsed.
     * */
    public static Questionnaire getQuestionnaireFromJson(FhirContext fhirContext, Resources res, int rawID) {

        IParser parser = fhirContext.newJsonParser();

        String json = getJasonAsString(res, rawID);

        return parser.parseResource(Questionnaire.class, json);

    }

    /**
     * loads the content of the file corresponding to the rawID into a string.
     * */
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

    /**
     * get the names of all the files in the raw resource folder starting with "questionnaire"
     */
    public static String[] getAllRawResources() {
        Field fields[] = R.raw.class.getDeclaredFields();
        ArrayList<String> nameList = new ArrayList();

        try {
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.getName().startsWith("questionnaire")) {
                    nameList.add(f.getName());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nameList.isEmpty()) {
            nameList.add("no files found");
        }
        String[] names = nameList.toArray(new String[nameList.size()]);
        return names;
    }
}
