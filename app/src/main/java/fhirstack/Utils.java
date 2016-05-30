package fhirstack;

/**
 * FHIRSTACK / C3PRO_Android
 * <p/>
 * Created by manny on 02.05.2016.
 * <p/>
 * This is a helper class providing tools to check Strings for content.
 */
class StringUtil {
    public static boolean isNotNullOrEmpty (String str){
        return (str != null) && !str.isEmpty();
    }
}
