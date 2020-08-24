import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class SampleClient {

	public static long responseTimePerReq = 0;

	public static void main(String[] theArgs) {

		// Create a FHIR client
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
		if (client == null)
			return;
		// client.registerInterceptor(new LoggingInterceptor(false));
		client.registerInterceptor(new Monitor());
		// Search for Patient resources
		URL url = SampleClient.class.getResource("/last_names.txt");
		File file = new File(url.getFile());

		BufferedReader br = null;
		String lastName;
		try {
			for (int i = 1; i <= 3; i++) {
				responseTimePerReq = 0;
				br = new BufferedReader(new FileReader(file));
				while ((lastName = br.readLine()) != null) {
					if (i < 3)
						client.search().forResource("Patient").where(Patient.FAMILY.matches().value(lastName))
								.returnBundle(Bundle.class).execute();
					else
						client.search().forResource("Patient").where(Patient.FAMILY.matches().value(lastName))
								.returnBundle(Bundle.class).cacheControl(new CacheControlDirective().setNoCache(true)).execute();
				}
				System.out.println("Loop " + i + " Average Response Time=  " + responseTimePerReq / 20 + " ms");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Monitor implements IClientInterceptor {
	@Override
	public void interceptRequest(IHttpRequest theRequest) {
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		SampleClient.responseTimePerReq += theResponse.getRequestStopWatch().getMillisAndRestart();
	}
}