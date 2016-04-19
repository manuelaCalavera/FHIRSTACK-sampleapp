package sampledata;

/**
 * Created by manny on 19.04.2016.
 */
public class SampleData {
    public static String getPatientString(){
        return "<Patient xmlns=\"http://hl7.org/fhir\">"
                + "<text><status value=\"generated\" /><div xmlns=\"http://www.w3.org/1999/xhtml\">John Cardinal</div></text>"
                + "<identifier><system value=\"http://orionhealth.com/mrn\" /><value value=\"PRP1660\" /></identifier>"
                + "<name><use value=\"official\" /><family value=\"Cardinal\" /><given value=\"John\" /></name>"
                + "<gender><coding><system value=\"http://hl7.org/fhir/v3/AdministrativeGender\" /><code value=\"M\" /></coding></gender>"
                + "<address><use value=\"home\" /><line value=\"2222 Home Street\" /></address><active value=\"true\" />"
                + "</Patient>";
    }
}
