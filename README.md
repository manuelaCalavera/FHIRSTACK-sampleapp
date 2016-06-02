FHIRSTACK
-------
FHIRSTACK uses the [HAPI][hapi] FHIR library and [ResearchStack] in an attempt to bring the [C3-PRO] functionality to Android.

Combining [🔥 FHIR][fhir] and [ResearchStack], usually for data storage into [i2b2][], this framework allows you to use 
FHIR 'Questionnaire' resources directly with a ResearchStack 'ViewTaskActivity' and will return FHIR 'QuestionnaireResponse' that 
you can send to your server.

#### Usage

For now, this project contains a library module and an app module to show how to use the library. The code is commented with 
javadoc. A proper maven link and more instructions on how to use may follow soon.


#### Versions

The library uses HAPI FHIR 1.5 for dstu3. Questionnaires in dstu2 (with group and question elements) will not work with this demo setup. 
Target Android sdk is 23, minimum sdk 16 due to ResearchStack.

Modules
-------
The framework will consist of several modules that complement each other, similar to the C3-PRO framework.

### Questionnaires

Enables the conversion of a FHIR `Questionnaire` resource to a ResearchSTack 'task' that can be presented to the user using a 
'ViewTaskActivity' and conversion back from a 'TaskResult' to a FHIR 'QuestionnaireResponse' resource.

### DataQueue

This module is in the works and will provide a FHIR server implementation used to move FHIR resources, created on device, to a FHIR 
server, without the need for user interaction nor -confirmation.



[hapi]: http://hapifhir.io
[researchstack]: http://researchstack.org
[C3-PRO]: http://c3-pro.org
[fhir]: http://hl7.org/fhir/
[researchkit]: http://researchkit.github.io
[i2b2]: https://www.i2b2.org
