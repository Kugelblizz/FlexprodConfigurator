package at.jku.cis.iVolunteer.api.reset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.jku.cis.iVolunteer.marketplace._mapper.property.PropertyDefinitionToPropertyInstanceMapper;
import at.jku.cis.iVolunteer.marketplace.chart.StoredChartRepository;
import at.jku.cis.iVolunteer.marketplace.configurations.clazz.ClassConfigurationRepository;
import at.jku.cis.iVolunteer.marketplace.fake.configuratorReset.ClassesAndRelationshipsToReset;
import at.jku.cis.iVolunteer.marketplace.fake.configuratorReset.ClassesAndRelationshipsToResetRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.class_.ClassDefinitionRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.class_.ClassInstanceController;
import at.jku.cis.iVolunteer.marketplace.meta.core.property.PropertyDefinitionRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.relationship.RelationshipRepository;
import at.jku.cis.iVolunteer.marketplace.user.VolunteerRepository;
import at.jku.cis.iVolunteer.model.configurations.clazz.ClassConfiguration;
import at.jku.cis.iVolunteer.model.meta.core.clazz.ClassArchetype;
import at.jku.cis.iVolunteer.model.meta.core.clazz.ClassInstance;
import at.jku.cis.iVolunteer.model.meta.core.clazz.task.TaskClassInstance;
import at.jku.cis.iVolunteer.model.meta.core.property.definition.PropertyDefinition;
import at.jku.cis.iVolunteer.model.meta.core.property.instance.PropertyInstance;
import at.jku.cis.iVolunteer.model.user.Volunteer;

@Service
public class ResetService {

	@Autowired private VolunteerRepository volunteerRepository;
	@Autowired private PropertyDefinitionRepository propertyDefinitionRepository;
	@Autowired private PropertyDefinitionToPropertyInstanceMapper propertyDefinitionToPropertyInstanceMapper;
	@Autowired private ClassInstanceController classInstanceController;
	@Autowired private StoredChartRepository storedChartRepository;
	@Autowired private ClassesAndRelationshipsToResetRepository classesAndRelationshipsToResetRepository;
	@Autowired private ClassDefinitionRepository classDefinitionRepository;
	@Autowired private RelationshipRepository relationshipRepository;
	@Autowired private ClassConfigurationRepository configuratorRepository;
	@Autowired private ClassInstanceRepository classInstanceRepository;

	public void reset() {

		resetClassDefinitionsRelationshipsAndConfigurators();

		deleteSharedChart();
	}

	private void deleteSharedChart() {
		this.storedChartRepository.deleteAll();
	}

	private void resetClassDefinitionsRelationshipsAndConfigurators() {
		List<ClassesAndRelationshipsToReset> list = classesAndRelationshipsToResetRepository.findAll();
		ClassesAndRelationshipsToReset reset = list.get(0);

		classDefinitionRepository.deleteAll();
		classDefinitionRepository.save(reset.getClassDefinitions());

		relationshipRepository.deleteAll();
		relationshipRepository.save(reset.getRelationships());

		configuratorRepository.deleteAll();
		configuratorRepository.save(reset.getConfigurators());
	}

	public void pushTaskFromAPI() {
//		[{
//		    "_id": "test_2222",
//		    "_class": "at.jku.cis.iVolunteer.model.meta.core.clazz.task.TaskClassInstance",
//		    "name": "Medical Care Transport_test",
//		    "userId": "5df4c9d571186b28b3474941",
//		    "issuerId": "FFA",
//		    "published": false,
//		    "inUserRepository": false,
//		    "inIssuerInbox": false,
//		    "classArchetype": "TASK",
//		    "timestamp": "2020-01-15T10:02:39.834Z"
//		}]

		TaskClassInstance instance = new TaskClassInstance();
		instance.setId("task_from_api");
		instance.setClassArchetype(ClassArchetype.TASK);
		instance.setName("Sybos Tätigkeit");
		instance.setIssuerId("FFA");

		Volunteer user = volunteerRepository.findByUsername("mweixlbaumer");
		instance.setUserId(user.getId());


		instance.setTimestamp(new Date());

		List<PropertyInstance<Object>> properties = new ArrayList<>();

		PropertyDefinition<Object> nameDefinition = propertyDefinitionRepository.findOne("name");
		PropertyInstance<Object> nameInstance = propertyDefinitionToPropertyInstanceMapper.toTarget(nameDefinition);
		nameInstance.setValues(new ArrayList<>());
		nameInstance.getValues().add("Brandeinsatz");
		properties.add(nameInstance);
		instance.setProperties(properties);
		List<ClassInstance> instances = new ArrayList<>();
		instances.add(instance);

		classInstanceController.createNewClassInstances(instances);

	}

	public void addFahrtenspangeFake() {

		TaskClassInstance instance = new TaskClassInstance();
		instance.setId("fahrtenspange" + new Date().hashCode());
		instance.setClassArchetype(ClassArchetype.ACHIEVEMENT);
		instance.setName("Fahrtenspange Bronze");
		instance.setIssuerId("FFA");

		Volunteer user = volunteerRepository.findByUsername("mweixlbaumer");
		instance.setUserId(user.getId());


		instance.setTimestamp(new Date());

		List<PropertyInstance<Object>> properties = new ArrayList<>();

		PropertyDefinition<Object> nameDefinition = propertyDefinitionRepository.findOne("name");
		PropertyInstance<Object> nameInstance = propertyDefinitionToPropertyInstanceMapper.toTarget(nameDefinition);
		nameInstance.setValues(new ArrayList<>());
		nameInstance.getValues().add("Fahrtenspange Bronze");

		properties.add(nameInstance);

		instance.setProperties(properties);

		List<ClassInstance> instances = new ArrayList<>();
		instances.add(instance);

		classInstanceController.createNewClassInstances(instances);
	}

	public void createResetState() {
		classesAndRelationshipsToResetRepository.deleteAll();

		ClassesAndRelationshipsToReset classesAndRelationshipsToReset = new ClassesAndRelationshipsToReset();
		classesAndRelationshipsToReset.getClassDefinitions().addAll(classDefinitionRepository.findAll());
		classesAndRelationshipsToReset.getRelationships().addAll(relationshipRepository.findAll());
		classesAndRelationshipsToReset.getConfigurators().addAll(configuratorRepository.findAll());

		classesAndRelationshipsToResetRepository.save(classesAndRelationshipsToReset);
	}
}