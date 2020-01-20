package at.jku.cis.iVolunteer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.jku.cis.iVolunteer.mapper.meta.core.property.PropertyDefinitionToClassPropertyMapper;
import at.jku.cis.iVolunteer.marketplace.MarketplaceService;
import at.jku.cis.iVolunteer.marketplace.feedback.FeedbackRepository;
import at.jku.cis.iVolunteer.marketplace.meta.configurator.ConfiguratorRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.class_.ClassDefinitionRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.class_.ClassInstanceRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.property.PropertyDefinitionRepository;
import at.jku.cis.iVolunteer.marketplace.meta.core.relationship.RelationshipRepository;
import at.jku.cis.iVolunteer.marketplace.rule.DerivationRuleRepository;
import at.jku.cis.iVolunteer.marketplace.user.HelpSeekerRepository;
import at.jku.cis.iVolunteer.marketplace.user.VolunteerRepository;
import at.jku.cis.iVolunteer.marketplace.usermapping.UserMappingRepository;
import at.jku.cis.iVolunteer.model.feedback.Feedback;
import at.jku.cis.iVolunteer.model.feedback.FeedbackType;
import at.jku.cis.iVolunteer.model.meta.configurator.Configurator;
import at.jku.cis.iVolunteer.model.meta.core.clazz.ClassArchetype;
import at.jku.cis.iVolunteer.model.meta.core.clazz.ClassDefinition;
import at.jku.cis.iVolunteer.model.meta.core.clazz.achievement.AchievementClassDefinition;
import at.jku.cis.iVolunteer.model.meta.core.clazz.competence.CompetenceClassDefinition;
import at.jku.cis.iVolunteer.model.meta.core.clazz.competence.CompetenceClassInstance;
import at.jku.cis.iVolunteer.model.meta.core.clazz.function.FunctionClassDefinition;
import at.jku.cis.iVolunteer.model.meta.core.clazz.task.TaskClassInstance;
import at.jku.cis.iVolunteer.model.meta.core.property.PropertyType;
import at.jku.cis.iVolunteer.model.meta.core.property.definition.ClassProperty;
import at.jku.cis.iVolunteer.model.meta.core.property.definition.PropertyDefinition;
import at.jku.cis.iVolunteer.model.meta.core.relationship.Inheritance;
import at.jku.cis.iVolunteer.model.rule.AttributeAggregationOperatorType;
import at.jku.cis.iVolunteer.model.rule.AttributeSourceRuleEntry;
import at.jku.cis.iVolunteer.model.rule.ClassAggregationOperatorType;
import at.jku.cis.iVolunteer.model.rule.ClassSourceRuleEntry;
import at.jku.cis.iVolunteer.model.rule.DerivationRule;
import at.jku.cis.iVolunteer.model.rule.MappingOperatorType;
import at.jku.cis.iVolunteer.model.user.HelpSeeker;
import at.jku.cis.iVolunteer.model.user.Volunteer;
import jersey.repackaged.com.google.common.collect.Lists;

@Service
public class InitializationService {

	@Autowired private PropertyDefinitionToClassPropertyMapper propertyDefinitionToClassPropertyMapper;

	@Autowired private ClassDefinitionRepository classDefinitionRepository;
	@Autowired private RelationshipRepository relationshipRepository;
	@Autowired private PropertyDefinitionRepository propertyDefinitionsRepository;
	@Autowired private ConfiguratorRepository configuratorRepository;
	@Autowired private ClassInstanceRepository classInstanceRepository;
	@Autowired private PropertyDefinitionRepository propertyDefinitionRepository;
	@Autowired private MarketplaceService marketplaceService;
	@Autowired private FinalizationService finalizationService;
	@Autowired private DerivationRuleRepository derivationRuleRepository;
	@Autowired private VolunteerRepository volunteerRepository;
	@Autowired private HelpSeekerRepository helpSeekerRepository;
	@Autowired private FeedbackRepository feedbackRepository;
	@Autowired private UserMappingRepository userMappingRepository;

	@PostConstruct
	public void init() {
//		finalizationService.destroy(configuratorRepository, classDefinitionRepository, classInstanceRepository,
//				relationshipRepository, propertyDefinitionRepository, derivationRuleRepository);
		addTestConfigClasses();
		addConfigurators();
		addiVolunteerAPIClassDefinition();
//		addTestDerivationRule();
		this.addTestClassInstances();
	}

	private void addTestDerivationRule() {
		DerivationRule rule = new DerivationRule();
		rule.setName("myrule");

		AttributeSourceRuleEntry source = new AttributeSourceRuleEntry();
		source.setClassDefinitionId(classDefinitionRepository.findByName("PersonBadge").getId());
		source.setClassPropertyId(classDefinitionRepository.findByName("PersonBadge").getProperties().get(0).getId());
		source.setMappingOperatorType(MappingOperatorType.GE);
		source.setAggregationOperatorType(AttributeAggregationOperatorType.SUM);
		source.setValue("102");
		rule.setAttributeSourceRules(Lists.asList(source, new AttributeSourceRuleEntry[0]));

		ClassSourceRuleEntry cSource = new ClassSourceRuleEntry();
		cSource.setClassDefinitionId(classDefinitionRepository.findByName("PersonBadge").getId());
		cSource.setMappingOperatorType(MappingOperatorType.GE);
		cSource.setAggregationOperatorType(ClassAggregationOperatorType.COUNT);
		cSource.setValue("102");
		rule.setClassSourceRules(Lists.asList(cSource, new ClassSourceRuleEntry[0]));

		rule.setTarget(classDefinitionRepository.findByName("PersonCertificate").getId());
		rule.setMarketplaceId(marketplaceService.getMarketplaceId());
		derivationRuleRepository.save(rule);
	}

	private void addiVolunteerAPIClassDefinition() {
		ClassDefinition findByName = classDefinitionRepository.findByName("PersonRole");
		if (findByName == null) {
			createiVolunteerAPIPersonRoleClassDefinition();
			createiVolunteerAPIPersonBadgeClassDefinition();
			createiVolunteerAPIPersonCertificateClassDefinition();
			createiVolunteerAPIPersonTaskClassDefinition();
		}

	}

	private void createiVolunteerAPIPersonRoleClassDefinition() {
		FunctionClassDefinition functionDefinition = new FunctionClassDefinition();
		functionDefinition.setClassArchetype(ClassArchetype.FUNCTION);
		functionDefinition.setMarketplaceId(marketplaceService.getMarketplaceId());
		functionDefinition.setRoot(true);
		functionDefinition.setName("PersonRole");
		functionDefinition.setTimestamp(new Date());
		List<PropertyDefinition<Object>> properties = addPropertyDefinitions();
		functionDefinition.setProperties(
				propertyDefinitionToClassPropertyMapper.toTargets(filterPersonRoleProperties(properties)));
		classDefinitionRepository.save(functionDefinition);
	}

	private void createiVolunteerAPIPersonBadgeClassDefinition() {
		AchievementClassDefinition achievementDefinition = new AchievementClassDefinition();
		achievementDefinition.setClassArchetype(ClassArchetype.ACHIEVEMENT);
		achievementDefinition.setMarketplaceId(marketplaceService.getMarketplaceId());
		achievementDefinition.setRoot(true);
		achievementDefinition.setName("PersonBadge");
		achievementDefinition.setTimestamp(new Date());
		List<PropertyDefinition<Object>> properties = addPropertyDefinitions();
		achievementDefinition.setProperties(
				propertyDefinitionToClassPropertyMapper.toTargets(filterPersonBadgeProperties(properties)));
		classDefinitionRepository.save(achievementDefinition);
	}

	private void createiVolunteerAPIPersonCertificateClassDefinition() {
		AchievementClassDefinition achievementDefinition = new AchievementClassDefinition();
		achievementDefinition.setClassArchetype(ClassArchetype.ACHIEVEMENT);
		achievementDefinition.setMarketplaceId(marketplaceService.getMarketplaceId());
		achievementDefinition.setRoot(true);
		achievementDefinition.setName("PersonCertificate");
		achievementDefinition.setTimestamp(new Date());
		List<PropertyDefinition<Object>> properties = addPropertyDefinitions();
		achievementDefinition.setProperties(
				propertyDefinitionToClassPropertyMapper.toTargets(filterPersonCertificateProperties(properties)));
		classDefinitionRepository.save(achievementDefinition);
	}

	private void createiVolunteerAPIPersonTaskClassDefinition() {
		AchievementClassDefinition achievementDefinition = new AchievementClassDefinition();
		achievementDefinition.setClassArchetype(ClassArchetype.TASK);
		achievementDefinition.setMarketplaceId(marketplaceService.getMarketplaceId());
		achievementDefinition.setRoot(true);
		achievementDefinition.setName("PersonTask");
		achievementDefinition.setTimestamp(new Date());
		List<PropertyDefinition<Object>> properties = addPropertyDefinitions();
		achievementDefinition.setProperties(
				propertyDefinitionToClassPropertyMapper.toTargets(filterPersonTaskProperties(properties)));
		classDefinitionRepository.save(achievementDefinition);
	}

	private List<PropertyDefinition<Object>> filterPersonRoleProperties(List<PropertyDefinition<Object>> properties) {
		// @formatter:off
		return properties.stream()
				.filter(p -> p.getName().equals("roleID") 
						|| p.getName().equals("roleType")
						|| p.getName().equals("roleName") 
						|| p.getName().equals("roleDescription")
						|| p.getName().equals("organisationID") 
						|| p.getName().equals("organisationName")
						|| p.getName().equals("organisationType") 
						|| p.getName().equals("dateFrom")
						|| p.getName().equals("dateTo") 
						|| p.getName().equals("iVolunteerSource"))
				.collect(Collectors.toList());
		// @formatter:on
	}

	private List<PropertyDefinition<Object>> filterPersonBadgeProperties(List<PropertyDefinition<Object>> properties) {
		// @formatter:off
		return properties.stream()
				.filter(p -> p.getName().equals("badgeID") 
						|| p.getName().equals("badgeName")
						|| p.getName().equals("badgeDescription") 
						|| p.getName().equals("badgeIssuedOn")
						|| p.getName().equals("badgeIcon")
						|| p.getName().equals("iVolunteerUUID")
						|| p.getName().equals("iVolunteerSource"))
				.collect(Collectors.toList());
		// @formatter:on
	}

	private List<PropertyDefinition<Object>> filterPersonCertificateProperties(
			List<PropertyDefinition<Object>> properties) {
		// @formatter:off
		return properties.stream()
				.filter(p -> p.getName().equals("certificateID") 
						|| p.getName().equals("certificateName")
						|| p.getName().equals("certificateDescription") 
						|| p.getName().equals("certificateIssuedOn")
						|| p.getName().equals("certificateValidUntil") 
						|| p.getName().equals("certificateIcon")
						|| p.getName().equals("iVolunteerUUID") 
						|| p.getName().equals("iVolunteerSource"))
				.collect(Collectors.toList());
		// @formatter:on
	}

	private List<PropertyDefinition<Object>> filterPersonTaskProperties(List<PropertyDefinition<Object>> properties) {
		// @formatter:off
		return properties.stream()
				.filter(p -> p.getName().equals("taskId") 
						|| p.getName().equals("taskName")
						|| p.getName().equals("taskType1") 
						|| p.getName().equals("taskType2")
						|| p.getName().equals("taskType3") 
						|| p.getName().equals("taskType4")
						|| p.getName().equals("taskDescription") 
						|| p.getName().equals("Zweck")
						|| p.getName().equals("Rolle")
						|| p.getName().equals("Rang")
						|| p.getName().equals("Phase")
						|| p.getName().equals("Arbeitsteilung")
						|| p.getName().equals("Ebene")
						|| p.getName().equals("taskCountAll")
						|| p.getName().equals("taskDateFrom") 
						|| p.getName().equals("taskDateTo")
						|| p.getName().equals("taskDuration") 
						|| p.getName().equals("taskLocation")
						|| p.getName().equals("taskGeoInformation")
						|| p.getName().equals("iVolunteerUUID")
						|| p.getName().equals("iVolunteerSource"))
				.collect(Collectors.toList());
		// @formatter:on
	}

	private List<PropertyDefinition<Object>> addPropertyDefinitions() {
		List<PropertyDefinition<Object>> propertyDefinitions = new ArrayList<>();
		addCrossCuttingProperties(propertyDefinitions);
		addPersonRoleProperties(propertyDefinitions);
		addPersonBadgeProperties(propertyDefinitions);
		addPersonCertificateProperties(propertyDefinitions);
		addPersonTaskProperties(propertyDefinitions);
		return propertyDefinitionsRepository.save(propertyDefinitions);
	}

	private void addCrossCuttingProperties(List<PropertyDefinition<Object>> propertyDefinitions) {
		propertyDefinitions.add(new PropertyDefinition<Object>("iVolunteerUUID", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("iVolunteerSource", PropertyType.TEXT));
	}

	private void addPersonRoleProperties(List<PropertyDefinition<Object>> propertyDefinitions) {
		propertyDefinitions.add(new PropertyDefinition<Object>("roleID", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("roleType", PropertyType.TEXT));
//		propertyDefinitions.add(new PropertyDefinition<Object>("roleName", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("roleDescription", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("organisationID", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("organisationName", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("organisationType", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("dateFrom", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("dateTo", PropertyType.DATE));
	}

	private void addPersonBadgeProperties(List<PropertyDefinition<Object>> propertyDefinitions) {
		propertyDefinitions.add(new PropertyDefinition<Object>("badgeID", PropertyType.TEXT));
//		propertyDefinitions.add(new PropertyDefinition<Object>("badgeName", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("badgeDescription", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("badgeIssuedOn", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("badgeIcon", PropertyType.TEXT));
	}

	private void addPersonCertificateProperties(List<PropertyDefinition<Object>> propertyDefinitions) {
		propertyDefinitions.add(new PropertyDefinition<Object>("certificateID", PropertyType.TEXT));
//		propertyDefinitions.add(new PropertyDefinition<Object>("certificateName", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("certificateDescription", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("certificateIssuedOn", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("certificateValidUntil", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("certificateIcon", PropertyType.TEXT));
	}

	private void addPersonTaskProperties(List<PropertyDefinition<Object>> propertyDefinitions) {
		propertyDefinitions.add(new PropertyDefinition<Object>("taskId", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskName", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskType1", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskType2", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskType3", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskType4", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskDescription", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Zweck", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Rolle", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Rang", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Phase", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Arbeitsteilung", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("Ebene", PropertyType.TEXT));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskDateFrom", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskDateTo", PropertyType.DATE));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskDuration", PropertyType.FLOAT_NUMBER));
		propertyDefinitions.add(new PropertyDefinition<Object>("taskLocation", PropertyType.TEXT));
//			TODO task geoinformation to geo object
		propertyDefinitions.add(new PropertyDefinition<Object>("taskGeoInformation", PropertyType.TEXT));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addTestConfigClasses() {

		CompetenceClassDefinition c1 = new CompetenceClassDefinition();
		c1.setId("test1");
		c1.setName("Class 1");
		c1.setProperties(new ArrayList<ClassProperty<Object>>());
		c1.setRoot(true);

		PropertyDefinition npd = new StandardPropertyDefinitions.NameProperty();
		ClassProperty<Object> ncp = propertyDefinitionToClassPropertyMapper.toTarget(npd);
		c1.getProperties().add(ncp);

		PropertyDefinition sdpd = new StandardPropertyDefinitions.StartDateProperty();
		ClassProperty<Object> sdcp = propertyDefinitionToClassPropertyMapper.toTarget(sdpd);
		c1.getProperties().add(sdcp);

		PropertyDefinition dpd = new StandardPropertyDefinitions.DescriptionProperty();
		ClassProperty<Object> dcp = propertyDefinitionToClassPropertyMapper.toTarget(dpd);
		c1.getProperties().add(dcp);

		c1.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c2 = new CompetenceClassDefinition();
		c2.setId("test2");
		c2.setName("Class 2");
		c2.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c3 = new CompetenceClassDefinition();
		c3.setId("test3");
		c3.setName("Class 3");
		c3.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c4 = new CompetenceClassDefinition();
		c4.setId("test4");
		c4.setName("Class 4");
		c4.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c5 = new CompetenceClassDefinition();
		c5.setId("test5");
		c5.setName("Class 5");
		c5.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c6 = new CompetenceClassDefinition();
		c6.setId("test6");
		c6.setName("Class 6");
		c6.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c7 = new CompetenceClassDefinition();
		c7.setId("test7");
		c7.setName("Class 7");
		c7.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c8 = new CompetenceClassDefinition();
		c8.setId("test8");
		c8.setName("Class 8");
		c8.setClassArchetype(ClassArchetype.COMPETENCE);

		CompetenceClassDefinition c9 = new CompetenceClassDefinition();
		c9.setId("test9");
		c9.setName("Class 9");
		c9.setClassArchetype(ClassArchetype.COMPETENCE);

//		{from: 1, to: 3},
//		  {from: 1, to: 2},
//		  {from: 2, to: 4},
//		  {from: 2, to: 5},
//		  {from: 3, to: 3},
//		  {from: 6, to: 6},

		Inheritance i1 = new Inheritance(c1.getId(), c3.getId(), c1.getId());
		i1.setId("test_i1");
		Inheritance i2 = new Inheritance(c1.getId(), c2.getId(), c1.getId());
		i2.setId("test_i2");
		Inheritance i3 = new Inheritance(c2.getId(), c4.getId(), c2.getId());
		i3.setId("test_i3");
		Inheritance i4 = new Inheritance(c2.getId(), c5.getId(), c2.getId());
		i4.setId("test_i4");
//		Inheritance i5 = new Inheritance(c3.getId(), c3.getId(), c3.getId());
//		i5.setId("test_i5");
//		Inheritance i6 = new Inheritance(c6.getId(), c6.getId(), c6.getId());
//		i6.setId("test_i6");

		Inheritance i7 = new Inheritance(c5.getId(), c7.getId(), c5.getId());
		i7.setId("test_i7");
		Inheritance i8 = new Inheritance(c5.getId(), c8.getId(), c5.getId());
		i8.setId("test_i8");
		Inheritance i9 = new Inheritance(c4.getId(), c9.getId(), c4.getId());
		i9.setId("test_i9");

		if (!relationshipRepository.exists(i1.getId())) {
			relationshipRepository.save(i1);
		}
		if (!relationshipRepository.exists(i2.getId())) {
			relationshipRepository.save(i2);
		}
		if (!relationshipRepository.exists(i3.getId())) {
			relationshipRepository.save(i3);
		}
		if (!relationshipRepository.exists(i4.getId())) {
			relationshipRepository.save(i4);
		}
//		if (!relationshipRepository.exists(i5.getId())) {
//			relationshipRepository.save(i5);
//		}

		if (!relationshipRepository.exists(i7.getId())) {
			relationshipRepository.save(i7);
		}
		if (!relationshipRepository.exists(i8.getId())) {
			relationshipRepository.save(i8);
		}
		if (!relationshipRepository.exists(i9.getId())) {
			relationshipRepository.save(i9);
		}

		if (!classDefinitionRepository.exists(c1.getId())) {
			classDefinitionRepository.save(c1);
		}

		if (!classDefinitionRepository.exists(c2.getId())) {
			classDefinitionRepository.save(c2);
		}

		if (!classDefinitionRepository.exists(c3.getId())) {
			classDefinitionRepository.save(c3);
		}

		if (!classDefinitionRepository.exists(c4.getId())) {
			classDefinitionRepository.save(c4);
		}

		if (!classDefinitionRepository.exists(c5.getId())) {
			classDefinitionRepository.save(c5);
		}

		if (!classDefinitionRepository.exists(c6.getId())) {
			classDefinitionRepository.save(c6);
		}

		if (!classDefinitionRepository.exists(c7.getId())) {
			classDefinitionRepository.save(c7);
		}

		if (!classDefinitionRepository.exists(c8.getId())) {
			classDefinitionRepository.save(c8);
		}

		if (!classDefinitionRepository.exists(c9.getId())) {
			classDefinitionRepository.save(c9);
		}
	}

	private void addConfigurators() {
		Configurator c1 = new Configurator();
		c1.setName("Slot1");
		c1.setId("slot1");
		c1.setDate(new Date());

		c1.setRelationshipIds(new ArrayList<>());
		c1.getRelationshipIds().add("test_i1");
		c1.getRelationshipIds().add("test_i2");
		c1.getRelationshipIds().add("test_i3");
		c1.getRelationshipIds().add("test_i4");
//		c1.getRelationshipIds().add("test_i5");
//		c1.getRelationshipIds().add("test_i6");
		c1.getRelationshipIds().add("test_i7");
		c1.getRelationshipIds().add("test_i8");
		c1.getRelationshipIds().add("test_i9");

		c1.setClassDefinitionIds(new ArrayList<>());
		c1.getClassDefinitionIds().add("test1");
		c1.getClassDefinitionIds().add("test2");
		c1.getClassDefinitionIds().add("test3");
		c1.getClassDefinitionIds().add("test4");
		c1.getClassDefinitionIds().add("test5");
		c1.getClassDefinitionIds().add("test6");
		c1.getClassDefinitionIds().add("test7");
		c1.getClassDefinitionIds().add("test8");
		c1.getClassDefinitionIds().add("test9");

		Configurator c2 = new Configurator();
		c2.setName("Slot2");
		c2.setId("slot2");
		c2.setDate(new Date());

		Configurator c3 = new Configurator();
		c3.setName("Slot3");
		c3.setId("slot3");

		c3.setDate(new Date());

		Configurator c4 = new Configurator();
		c4.setName("Slot4");
		c4.setId("slot4");

		c4.setDate(new Date());

		Configurator c5 = new Configurator();
		c5.setName("Slot5");
		c5.setId("slot5");
		c5.setDate(new Date());

		if (!configuratorRepository.exists(c1.getId())) {
			configuratorRepository.save(c1);
		}

		if (!configuratorRepository.exists(c2.getId())) {
			configuratorRepository.save(c2);
		}

		if (!configuratorRepository.exists(c3.getId())) {
			configuratorRepository.save(c3);
		}

		if (!configuratorRepository.exists(c3.getId())) {
			configuratorRepository.save(c3);
		}

		if (!configuratorRepository.exists(c4.getId())) {
			configuratorRepository.save(c4);
		}
		if (!configuratorRepository.exists(c5.getId())) {
			configuratorRepository.save(c5);
		}
	}

	private void addTestClassInstances() {
		TaskClassInstance ti1 = new TaskClassInstance();
		ti1.setId("ti1");
		ti1.setName("Shopping Elementaries");
		Volunteer volunteer = volunteerRepository.findByUsername("mweissenbek");
		HelpSeeker oerk = helpSeekerRepository.findByUsername("OERK");
		HelpSeeker mvs = helpSeekerRepository.findByUsername("MVS");
		HelpSeeker ffa = helpSeekerRepository.findByUsername("FFA");
		HelpSeeker efa = helpSeekerRepository.findByUsername("EFA");

		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (oerk != null) {
			ti1.setIssuerId(oerk.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));
		ti1.setInUserRepository(true);

		classInstanceRepository.save(ti1);

		ti1.setId("ti2");
		ti1.setName("Equipment Service");
		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (mvs != null) {
			ti1.setIssuerId(mvs.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));

		classInstanceRepository.save(ti1);

		ti1.setId("ti3");
		ti1.setName("Shopping Elementaries");
		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (efa != null) {
			ti1.setIssuerId(efa.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));

		classInstanceRepository.save(ti1);

		ti1.setId("ti4");
		ti1.setName("Shopping Elementaries");
		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (oerk != null) {
			ti1.setIssuerId(oerk.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));

		classInstanceRepository.save(ti1);

		ti1.setId("ti5");
		ti1.setName("Donation Collection");
		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (efa != null) {
			ti1.setIssuerId(efa.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));

		classInstanceRepository.save(ti1);

		ti1.setId("ti6");
		ti1.setName("Medical Care Transport");
		if (volunteer != null) {
			ti1.setUserId(volunteer.getId());
		}
		if (oerk != null) {
			ti1.setIssuerId(oerk.getId());
		}
		ti1.setTimestamp(new Date(System.currentTimeMillis()));

		classInstanceRepository.save(ti1);

		Feedback f1 = new Feedback();
		f1.setId("f1");
		f1.setName("Firetruck Driver Renewed");
		f1.setFeedbackType(FeedbackType.KUDOS);
		if (volunteer != null) {
			f1.setUserId(volunteer.getId());
		}
		if (oerk != null) {
			f1.setIssuerId(oerk.getId());
		}
		f1.setTimestamp(new Date(System.currentTimeMillis()));
		f1.setFeedbackValue(1);
		f1.setInUserRepository(false);

		feedbackRepository.save(f1);

		f1.setId("f2");
		f1.setName("Yearly Feedback");
		f1.setFeedbackType(FeedbackType.STARRATING);
		if (volunteer != null) {
			f1.setUserId(volunteer.getId());
		}
		if (ffa != null) {
			f1.setIssuerId(ffa.getId());
		}
		f1.setTimestamp(new Date(System.currentTimeMillis()));
		f1.setFeedbackValue(5);

		feedbackRepository.save(f1);

		CompetenceClassInstance ci1 = new CompetenceClassInstance();
		ci1.setId("ci1");
		ci1.setName("Diligence");
		if (volunteer != null) {
			ci1.setUserId(volunteer.getId());
		}
		if (mvs != null) {
			ci1.setIssuerId(mvs.getId());
		}
		ci1.setTimestamp(new Date(System.currentTimeMillis()));
		ci1.setInUserRepository(true);

		classInstanceRepository.save(ci1);

		ci1.setId("ci2");
		ci1.setName("Teamwork");
		if (volunteer != null) {
			ci1.setUserId(volunteer.getId());
		}
		if (ffa != null) {
			ci1.setIssuerId(ffa.getId());
		}
		ci1.setTimestamp(new Date(System.currentTimeMillis()));
		classInstanceRepository.save(ci1);

		ci1.setId("ci3");
		ci1.setName("Communication Skills");
		if (volunteer != null) {
			ci1.setUserId(volunteer.getId());
		}
		if (efa != null) {
			ci1.setIssuerId(efa.getId());
		}
		ci1.setTimestamp(new Date(System.currentTimeMillis()));
		classInstanceRepository.save(ci1);

		ci1.setId("ci4");
		ci1.setName("Project Management");
		if (volunteer != null) {
			ci1.setUserId(volunteer.getId());
		}
		if (ffa != null) {
			ci1.setIssuerId(ffa.getId());
		}
		ci1.setTimestamp(new Date(System.currentTimeMillis()));
		classInstanceRepository.save(ci1);

		ci1.setId("ci5");
		ci1.setName("Firetruck Driver");
		if (volunteer != null) {
			ci1.setUserId(volunteer.getId());
		}
		if (ffa != null) {
			ci1.setIssuerId(ffa.getId());
		}
		ci1.setTimestamp(new Date(System.currentTimeMillis()));
		ci1.setInUserRepository(false);
		classInstanceRepository.save(ci1);
	}
}