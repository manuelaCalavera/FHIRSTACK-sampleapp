package fhirstack;

import android.util.Log;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.DecimalAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IPrimitiveDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.model.primitive.IntegerDt;


/**
 * Created by manny on 02.05.2016.
 */
public class Question2Step {
    public static Step question2Step(Questionnaire.GroupQuestion groupQuestion) {

        String linkId = groupQuestion.getLinkId();
        String id = StringUtil.isNotNullOrEmpty(linkId) ? linkId : UUID.randomUUID().toString();

        //TODO create nice title and text

        String gText = groupQuestion.getText();
        String text = StringUtil.isNotNullOrEmpty(gText) ? gText : getTextForQuestion(groupQuestion);

        AnswerFormat fmt = getAnswerformat(groupQuestion);

        Step step = new QuestionStep(id, text, fmt);

        //if the answer is required, set optional to false, otherwise to true
        if (groupQuestion.getRequired() != null) {
            step.setOptional(!groupQuestion.getRequired());
        } else {
            step.setOptional(true);
        }
        return step;
    }


    private static AnswerFormat getAnswerformat(Questionnaire.GroupQuestion groupQuestion) {
        String questionType = groupQuestion.getType();

        /*
        *     FHIR Question Types
        *     --------------------------------------------------------------------
        *     BOOLEAN("boolean", "http://hl7.org/fhir/answer-format"),
        *     DECIMAL("decimal", "http://hl7.org/fhir/answer-format"),
        *     INTEGER("integer", "http://hl7.org/fhir/answer-format"),
        *     DATE("date", "http://hl7.org/fhir/answer-format"),
        *     DATE_TIME("dateTime", "http://hl7.org/fhir/answer-format"),
        *     INSTANT("instant", "http://hl7.org/fhir/answer-format"),
        *     TIME("time", "http://hl7.org/fhir/answer-format"),
        *     STRING("string", "http://hl7.org/fhir/answer-format"),
        *     TEXT("text", "http://hl7.org/fhir/answer-format"),
        *     URL("url", "http://hl7.org/fhir/answer-format"),
        *     CHOICE("choice", "http://hl7.org/fhir/answer-format"),
        *     OPEN_CHOICE("open-choice", "http://hl7.org/fhir/answer-format"),
        *     ATTACHMENT("attachment", "http://hl7.org/fhir/answer-format"),
        *     REFERENCE("reference", "http://hl7.org/fhir/answer-format"),
        *     QUANTITY("quantity", "http://hl7.org/fhir/answer-format");
        *
        *
        *     Researchstack Answer Formats
        *     --------------------------------------------------------------------
        *     BooleanAnswerFormat
        *     ChoiceAnswerFormat
        *     DateAnswerFormat
        *     DecimalAnswerFormat
        *     EmailAnswerFormat
        *     FormAnswerFormat
        *     IntegerAnswerFormat
        *     TextAnswerFormat
        *
        * */


        switch (questionType) {
            case "boolean":
                return new BooleanAnswerFormat("Yes", "No");
            case "decimal":
                // for decimal, there is no implementedStepBody.class, have to use integer for now
                return new IntegerAnswerFormat(0, 1000);
            case "integer":
                List<ExtensionDt> minVals = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/minValue");
                List<ExtensionDt> maxVals = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/maxValue");
                ExtensionDt deflt = defaultAnswer(groupQuestion);
                if (!minVals.isEmpty() && !maxVals.isEmpty()) {
                    IPrimitiveDatatype sMinVal = minVals.get(0).getValueAsPrimitive();
                    IPrimitiveDatatype sMaxVal = maxVals.get(0).getValueAsPrimitive();

                    int minVal = (int) sMinVal.getValue();
                    int maxVal = (int) sMaxVal.getValue();

                    int def = minVal;
                    if (deflt != null) {
                        IPrimitiveDatatype pDef = deflt.getValueAsPrimitive();
                        def = (int) pDef.getValue();
                    }
                    // scale answer format not yet available, so have to use Integer
                    //return new IntegerAnswerFormat(AnswerFormat. some scale answer style)
                    return new IntegerAnswerFormat(minVal, maxVal);
                } else {
                    return new IntegerAnswerFormat(0, 1000);
                }
            case "date":
                return new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
            case "dateTime":
                return new DateAnswerFormat(AnswerFormat.DateAnswerStyle.DateAndTime);
            //case "instant": return new DateAnswerFormat();
            //case "time":
            case "string":
                return new TextAnswerFormat(300);
            case "text":
                return new TextAnswerFormat();
            case "url":
                return new TextAnswerFormat();
            case "choice":
                return new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                        resolveAnswerChoices(groupQuestion));
            case "open-choice":
                return new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                        new Choice<>("Zero", 0),
                        new Choice<>("One", 1),
                        new Choice<>("Two", 2));
            //case "attachment":
            //case "reference":
            case "quantity":
                return new IntegerAnswerFormat(0, 1000);

        }

        return new TextAnswerFormat();


    }

    // checking for the questions min value
    private static int getQuestionMin(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-minOccurs");
        if (!list.isEmpty()) {
            return Integer.parseInt(list.get(0).getValue().toString());
        } else {
            return 1;
        }
    }

    private static int questionMaxOccurs(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-maxOccurs");
        if (!list.isEmpty()) {
            return Integer.parseInt(list.get(0).getValue().toString());
        } else {
            return 1;
        }
    }

    private static String questionInstruction(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-instruction");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static String questionHelpText(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-help");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static String numericAnswerUnit(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-units");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static ExtensionDt defaultAnswer(Questionnaire.GroupQuestion groupQuestion) {
        List<ExtensionDt> list = groupQuestion.getUndeclaredExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-defaultValue");
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private static String getTextForQuestion(Questionnaire.GroupQuestion groupQuestion) {
        String instr = questionInstruction(groupQuestion);
        String hlp = questionHelpText(groupQuestion);

        String txt = StringUtil.isNotNullOrEmpty(instr) ? instr : hlp;
        return StringUtil.isNotNullOrEmpty(txt) ? txt : "no Text";
    }


    private static Choice[] resolveAnswerChoices(Questionnaire.GroupQuestion groupQuestion) {

        // where we possibly find options
        List<CodingDt> option = groupQuestion.getOption();
        ResourceReferenceDt reference = groupQuestion.getOptions();

        // choices we are going to return

        /*
        * if options contains codings, we fill the options into the choiceList
        * */
        if (!option.isEmpty()) {
            List<Choice> choiceList = new ArrayList<Choice>();
            for (CodingDt c : option) {
                choiceList.add(new Choice(c.getDisplay(), c.getCode()));
            }
            return choiceList.toArray(new Choice[choiceList.size()]);
        }


        /*
        * if a reference exists, resolve valueSet
        * for now assuming that only internal references exist
        * */
        else if (reference.getResource() != null) {
            ValueSet vSet = (ValueSet) reference.getResource();

            /*
            * if the options are in the code system as concept
            * */
            List<ValueSet.CodeSystemConcept> conceptList = vSet.getCodeSystem().getConcept();
            if(!conceptList.isEmpty()){
                List<Choice> choiceList = new ArrayList<Choice>();
                for (ValueSet.CodeSystemConcept concept: conceptList){
                    String text = concept.getDisplay();
                    String code = concept.getCode();
                    choiceList.add(new Choice(text, code));
                }
                return choiceList.toArray(new Choice[choiceList.size()]);
            }



            List<ValueSet.ExpansionContains> expansion = vSet.getExpansion().getContains();
            if (!expansion.isEmpty()){
                List<Choice> choiceList = new ArrayList<Choice>();
                for (ValueSet.ExpansionContains contain : expansion){
                    String text = contain.getDisplay();
                    String code = contain.getCode();
                    choiceList.add(new Choice(text, code));
                }
                return choiceList.toArray(new Choice[choiceList.size()]);
            }
        }


        /*
        * noob error handling, don't try this at home, do it right
        * */
        else {
            Choice[] c = {new Choice("no choices found", "N/A")};
            return c;
        }
        Choice[] c = {new Choice("no choices found", "N/A")};
        return c;
    }
}
