package sampledata;

import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.common.io.Files;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

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
import ca.uhn.fhir.model.dstu2.resource.CarePlan;
import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.parser.IParser;
import manny.fhirstack_sampleapp.FHIRStackApplication;
import manny.fhirstack_sampleapp.R;

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


    public static String getPatientString() {
        return "<Patient xmlns=\"http://hl7.org/fhir\">"
                + "<text><status value=\"generated\" /><div xmlns=\"http://www.w3.org/1999/xhtml\">John Cardinal</div></text>"
                + "<identifier><system value=\"http://orionhealth.com/mrn\" /><value value=\"PRP1660\" /></identifier>"
                + "<name><use value=\"official\" /><family value=\"Cardinal\" /><given value=\"John\" /></name>"
                + "<gender><coding><system value=\"http://hl7.org/fhir/v3/AdministrativeGender\" /><code value=\"M\" /></coding></gender>"
                + "<address><use value=\"home\" /><line value=\"2222 Home Street\" /></address><active value=\"true\" />"
                + "</Patient>";
    }


    public static Task getTask() {
        InstructionStep instructionStep = new InstructionStep(INSTRUCTION,
                "Selection Survey",
                "This survey can help us understand your eligibility for the fitness study");

        //string added to resources
        instructionStep.setStepTitle(R.string.survey);


        TextAnswerFormat format = new TextAnswerFormat();
        QuestionStep ageStep = new QuestionStep(NAME, "What is your name?", format);
        ageStep.setStepTitle(R.string.survey);

        DateAnswerFormat dateFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        QuestionStep dateStep = new QuestionStep(DATE, "Enter a date", dateFormat);
        dateStep.setStepTitle(R.string.survey);

        // Create a Boolean step to include in the task.
        QuestionStep booleanStep = new QuestionStep(NUTRITION);
        booleanStep.setStepTitle(R.string.survey);
        booleanStep.setTitle("Do you take nutritional supplements?");
        booleanStep.setAnswerFormat(new BooleanAnswerFormat("yes", "no"));
        booleanStep.setOptional(false);

        QuestionStep multiStep = new QuestionStep(MULTI_STEP);
        multiStep.setStepTitle(R.string.survey);
        AnswerFormat multiFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                new Choice<>("Zero", 0),
                new Choice<>("One", 1),
                new Choice<>("Two", 2));
        multiStep.setTitle("Select multiple");
        multiStep.setAnswerFormat(multiFormat);
        multiStep.setOptional(false);

        // Create a task wrapping the steps.
        OrderedTask task = new OrderedTask(SAMPLE_SURVEY, instructionStep, ageStep, dateStep,
                //formStep,
                booleanStep, multiStep);

        return task;
    }

    public static Questionnaire getQquestionnaireFromJson(FhirContext fhirContext, Resources res, int rawID) {


        IParser parser = fhirContext.newJsonParser();

        String json =getJasonAsString(res, rawID);

        Questionnaire questionnaire = parser.parseResource(Questionnaire.class, json);

        return questionnaire;

    }

    public static String getJasonAsString(Resources res, int rawID){

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

        /*
        @NonNull
        private FormStep createFormStep()
        {
            FormStep formStep = new FormStep(FORM_STEP, "Form", "Form groups multi-entry in one page");
            ArrayList<QuestionStep> formItems = new ArrayList<>();

            QuestionStep basicInfoHeader = new QuestionStep(BASIC_INFO_HEADER,
                    "Basic Information",
                    new UnknownAnswerFormat());
            formItems.add(basicInfoHeader);

            TextAnswerFormat format = new TextAnswerFormat();
            format.setIsMultipleLines(false);
            QuestionStep nameItem = new QuestionStep(FORM_NAME, "Name", format);
            formItems.add(nameItem);

            QuestionStep ageItem = new QuestionStep(FORM_AGE, "Age", new IntegerAnswerFormat(18, 90));
            formItems.add(ageItem);

            AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    new Choice<>("Male", 0),
                    new Choice<>("Female", 1));
            QuestionStep genderFormItem = new QuestionStep(FORM_GENDER, "Gender", genderFormat);
            formItems.add(genderFormItem);

            AnswerFormat multiFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                    new Choice<>("Zero", 0),
                    new Choice<>("One", 1),
                    new Choice<>("Two", 2));
            QuestionStep multiFormItem = new QuestionStep(FORM_MULTI_CHOICE, "Test Multi", multiFormat);
            formItems.add(multiFormItem);

            AnswerFormat dateOfBirthFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
            QuestionStep dateOfBirthFormItem = new QuestionStep(FORM_DATE_OF_BIRTH,
                    "Birthdate",
                    dateOfBirthFormat);
            formItems.add(dateOfBirthFormItem);

            // ... And so on, adding additional items
            formStep.setFormSteps(formItems);
            return formStep;
        }
    */

}
