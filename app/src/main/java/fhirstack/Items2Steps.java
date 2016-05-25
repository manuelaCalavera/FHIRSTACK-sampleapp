package fhirstack;

import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.dstu3.model.Questionnaire;


/**
 * Created by manny on 02.05.2016.
 */
public class Items2Steps {

    public static List<Step> items2Steps(List<Questionnaire.QuestionnaireItemComponent> items) {
        List<Step> steps = new ArrayList<Step>();

        for (Questionnaire.QuestionnaireItemComponent item : items) {
            Questionnaire.QuestionnaireItemType qType = item.getType();

            if (item.getType() == Questionnaire.QuestionnaireItemType.GROUP) {
                List<Step> newSteps = items2Steps(item.getItem());
                if (item.hasEnableWhen()) {
                    List<ResultRequirement> reqs = getRequirementsFor(item);
                    for (Step step : newSteps) {
                        if (step instanceof QuestionStep || step instanceof ConditionalQuestionStep) {
                            ConditionalQuestionStep newStep = (ConditionalQuestionStep) step;
                            newStep.addRequirements(reqs);
                            steps.add(newStep);
                        } else if (step instanceof InstructionStep || step instanceof ConditionalInstructionStep) {
                            ConditionalInstructionStep newStep = (ConditionalInstructionStep) step;
                            newStep.addRequirements(reqs);
                            steps.add(newStep);
                        }
                    }
                } else {
                    steps.addAll(newSteps);
                }
            } else {
                Step newStep = Items2Steps.item2Step(item);

                if (item.hasEnableWhen()) {
                    List<ResultRequirement> reqs = getRequirementsFor(item);
                    ((ConditionalStep) newStep).addRequirements(reqs);
                }
                steps.add(newStep);
            }

        }
        return steps;
    }


    public static Step item2Step(Questionnaire.QuestionnaireItemComponent item) {

        String linkId = item.getLinkId();
        String id = StringUtil.isNotNullOrEmpty(linkId) ? linkId : UUID.randomUUID().toString();

        //TODO create nice title and text

        String itemText = item.getText();
        String text = StringUtil.isNotNullOrEmpty(itemText) ? itemText : getTextForItem(item);
        if (!StringUtil.isNotNullOrEmpty(id)) {
            id = itemText;
        }

        if (item.getType() == Questionnaire.QuestionnaireItemType.DISPLAY) {
            if (item.hasEnableWhen()) {
                return new ConditionalInstructionStep(id, "", itemText);
            } else {
                return new InstructionStep(id, "", itemText);
            }
        } else {

            AnswerFormat fmt = getAnswerformat(item);

            if (item.hasEnableWhen()) {
                ConditionalQuestionStep step = new ConditionalQuestionStep(id, text, fmt);
                step.setOptional(!item.getRequired());
                return step;

            } else {
                QuestionStep step = new QuestionStep(id, text, fmt);
                step.setOptional(!item.getRequired());
                return step;
            }
        }
    }

    private static AnswerFormat getAnswerformat(Questionnaire.QuestionnaireItemComponent item) {

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


        switch (item.getType()) {
            case BOOLEAN:
                return new BooleanAnswerFormat("Yes", "No");
            case DECIMAL:
                // for decimal, there is no implementedStepBody.class, have to use integer for now
                return new IntegerAnswerFormat(0, 1000);
            case INTEGER:
                List<Extension> minVals = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/minValue");
                List<Extension> maxVals = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/maxValue");
                Extension dflt = defaultAnswer(item);
                if (!minVals.isEmpty() && !maxVals.isEmpty()) {
                    //Type sMinVal = minVals.get(0).getValue();
                    String sMinVal = minVals.get(0).getValue().primitiveValue();
                    String sMaxVal = maxVals.get(0).getValue().primitiveValue();

                    int minVal = Integer.parseInt(sMinVal);
                    int maxVal = Integer.parseInt(sMaxVal);

                    int def = minVal;
                    if (dflt != null) {
                        String sDef = dflt.getValue().primitiveValue();
                        def = Integer.parseInt(sDef);
                    }
                    // scale answer format not yet available, so have to use Integer
                    //return new IntegerAnswerFormat(AnswerFormat. some scale answer style)
                    return new IntegerAnswerFormat(minVal, maxVal);
                } else {
                    return new IntegerAnswerFormat(0, 1000);
                }
            case DATE:
                return new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
            case DATETIME:
                //not implementedStepBody
                //return new DateAnswerFormat(AnswerFormat.DateAnswerStyle.DateAndTime);
                return new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
            //case "instant": return new DateAnswerFormat();
            //case "time":
            case TIME:
                //not implemented yet
                return new TextAnswerFormat(5);
            case STRING:
                return new TextAnswerFormat(300);
            case TEXT:
                return new TextAnswerFormat();
            case CHOICE:
                return new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                        resolveAnswerChoices(item));
            case OPENCHOICE:
                return new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                        resolveAnswerChoices(item));
            //case "attachment":
            //case "reference":
            case QUANTITY:
                return new IntegerAnswerFormat(0, 1000);
            default:
                return new TextAnswerFormat();
        }
    }

    // checking for the questions min value
    private static int getQuestionMin(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-minOccurs");
        if (!list.isEmpty()) {
            return Integer.parseInt(list.get(0).getValue().toString());
        } else {
            return 1;
        }
    }

    private static int questionMaxOccurs(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-maxOccurs");
        if (!list.isEmpty()) {
            return Integer.parseInt(list.get(0).getValue().toString());
        } else {
            return 1;
        }
    }

    private static String questionInstruction(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-instruction");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static String questionHelpText(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-help");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static String numericAnswerUnit(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-units");
        if (!list.isEmpty()) {
            return list.get(0).getValue().toString();
        } else {
            return null;
        }
    }

    private static Extension defaultAnswer(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> list = item.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-defaultValue");
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private static String getTextForItem(Questionnaire.QuestionnaireItemComponent item) {
        String instr = questionInstruction(item);
        String hlp = questionHelpText(item);

        String txt = StringUtil.isNotNullOrEmpty(instr) ? instr : hlp;
        return StringUtil.isNotNullOrEmpty(txt) ? txt : "no Text";
    }


    private static Choice[] resolveAnswerChoices(Questionnaire.QuestionnaireItemComponent item) {

        // where we possibly find options
        List<Questionnaire.QuestionnaireItemOptionComponent> option = item.getOption();
        Reference reference = item.getOptions();

        // choices we are going to return

        /*
        * if options contains codings, we fill the options into the choiceList
        * */
        if (!option.isEmpty()) {
            List<Choice> choiceList = new ArrayList<Choice>();
            for (Questionnaire.QuestionnaireItemOptionComponent c : option) {
                String text = c.getValue().primitiveValue();
                Type value = c.getValue();
                choiceList.add(new Choice(text, value));
            }
            return choiceList.toArray(new Choice[choiceList.size()]);
        }


        /*
        * if a reference exists, resolve valueSet
        * for now assuming that only internal references exist
        * */


        else if (reference.getResource() != null) {
            ValueSet vSet = (ValueSet) reference.getResource();

            // this happens with included options // valueset contained
            List<ValueSet.ConceptSetComponent> includes = vSet.getCompose().getInclude();
            if (!includes.isEmpty()) {
                List<Choice> choiceList = new ArrayList<Choice>();
                for (ValueSet.ConceptSetComponent include : includes) {
                    List<ValueSet.ConceptReferenceComponent> concepts = include.getConcept();
                    for (ValueSet.ConceptReferenceComponent concept : concepts) {
                        String text = concept.getDisplay();
                        String code = concept.getCode();
                        choiceList.add(new Choice(text, code));
                    }
                }
                return choiceList.toArray(new Choice[choiceList.size()]);
            }


            // does this happen at all?
            List<ValueSet.ValueSetExpansionContainsComponent> expansion = vSet.getExpansion().getContains();
            if (!expansion.isEmpty()) {
                List<Choice> choiceList = new ArrayList<Choice>();
                for (ValueSet.ValueSetExpansionContainsComponent contain : expansion) {
                    String text = contain.getDisplay();
                    String code = contain.getCode();
                    choiceList.add(new Choice(text, code));
                }
                return choiceList.toArray(new Choice[choiceList.size()]);
            }
            /* old version:
            List<ValueSet.ExpansionContains> expansion = vSet.getExpansion().getContains();
            if (!expansion.isEmpty()) {
                List<Choice> choiceList = new ArrayList<Choice>();
                for (ValueSet.ExpansionContains contain : expansion) {
                    String text = contain.getDisplay();
                    String code = contain.getCode();
                    choiceList.add(new Choice(text, code));
                }
                return choiceList.toArray(new Choice[choiceList.size()]);
            }*/

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

    private static List<ResultRequirement> getRequirementsFor(Questionnaire.QuestionnaireItemComponent item) {
        List<ResultRequirement> reqs = new ArrayList<>();
        for (Questionnaire.QuestionnaireItemEnableWhenComponent enableWhen : item.getEnableWhen()) {

            String question = enableWhen.getQuestion();
            Type answer = enableWhen.getAnswer();

            reqs.add(new ResultRequirement(question, answer));
        }
        return reqs;
    }
}
