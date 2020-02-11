package at.jku.cis.iVolunteer.marketplace.task;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import at.jku.cis.iVolunteer.model.meta.core.clazz.ClassInstance;
import at.jku.cis.iVolunteer.model.meta.core.clazz.competence.CompetenceClassInstance;
import at.jku.cis.iVolunteer.model.task.Task;
import at.jku.cis.iVolunteer.model.volunteer.profile.VolunteerTaskEntry;

@Service
public class ContractorPublishingRestClient {

	private static final String TASK = "task";
	private static final String FINISHED_TASK_ENTRY = "finishedTaskEntry";
	private static final String COMPETENCE_ENTRY = "competenceEntry";
	private static final String CLASS_INSTANCE = "classInstance";
	private static final String CLASS_INSTANCES = "classInstances";
	
	private static final String CONTRACTOR_URI = "{0}/trustifier/contractor/{1}";

	private static final String AUTHORIZATION = "Authorization";

	@Value("${trustifier.uri}") private URI trustifierUri;

	@Autowired private RestTemplate restTemplate;

	public String publishTask(Task task, String authorization) {
		String requestURI = buildContractorRequestURI(TASK);
		return restTemplate.postForObject(requestURI, buildEntity(task, authorization), String.class);
	}

	public String publishTaskEntry(VolunteerTaskEntry vte, String authorization) {
		String requestURI = buildContractorRequestURI(FINISHED_TASK_ENTRY);
		return restTemplate.postForObject(requestURI, buildEntity(vte, authorization), String.class);
	}

	public String publishCompetenceEntry(CompetenceClassInstance competenceInstance, String authorization) {
		String requestURI = buildContractorRequestURI(COMPETENCE_ENTRY);
		return restTemplate.postForObject(requestURI, buildEntity(competenceInstance, authorization), String.class);
	}

	public String publishClassInstance(ClassInstance classInstance, String authorization) {
		String requestURI = buildContractorRequestURI(CLASS_INSTANCE);
		return restTemplate.postForObject(requestURI, buildEntity(classInstance, authorization), String.class);
	}
	
	public String publishClassInstances(List<ClassInstance> classInstances, String authorization) {
		String requestURI = buildContractorRequestURI(CLASS_INSTANCES);
		return restTemplate.postForObject(requestURI, buildEntity(classInstances, authorization), String.class);
	}
	
	private String buildContractorRequestURI(String requestPath) {
		return format(CONTRACTOR_URI, trustifierUri, requestPath);
	}

	private HttpEntity<?> buildEntity(Object body, String authorization) {
		return new HttpEntity<>(body, buildAuthorizationHeader(authorization));
	}

	private HttpHeaders buildAuthorizationHeader(String authorization) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(AUTHORIZATION, authorization);
		return headers;
	}
}