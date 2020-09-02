package at.jku.cis.iVolunteer.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import at.jku.cis.iVolunteer.core.security.activation.CoreActivationService;
import at.jku.cis.iVolunteer.core.user.CoreUserRepository;
import at.jku.cis.iVolunteer.model.core.user.CoreUser;

@Service
public class CoreRegistrationService {

	// @Autowired private CoreHelpSeekerRepository coreHelpSeekerRepository;
	// @Autowired private CoreVolunteerRepository coreVolunteerRepository;

	@Autowired private CoreUserRepository coreUserRepository;
	@Autowired CoreActivationService coreActivationService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public void registerUser(CoreUser user) {
		encryptPassword(user);
		this.coreUserRepository.save(user);
		this.coreActivationService.createActivationAndSendLink(user);
		
	}

	// public void registerVolunteer(CoreVolunteer volunteer) {
	// encryptPassword(volunteer);
	// this.coreVolunteerRepository.save(volunteer);
	// }

	// public void registerHelpSeeker(CoreHelpSeeker helpSeeker) {
	// encryptPassword(helpSeeker);
	// this.coreHelpSeekerRepository.save(helpSeeker);
	// }

	private void encryptPassword(CoreUser user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	}

}
