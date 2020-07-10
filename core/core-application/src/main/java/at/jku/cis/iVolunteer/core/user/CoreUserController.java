package at.jku.cis.iVolunteer.core.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.jku.cis.iVolunteer.core.marketplace.MarketplaceRepository;
import at.jku.cis.iVolunteer.core.user.CoreUserRepository;
import at.jku.cis.iVolunteer.core.user.CoreUserService;
import at.jku.cis.iVolunteer.model.core.user.CoreUser;
import at.jku.cis.iVolunteer.model.marketplace.Marketplace;
import at.jku.cis.iVolunteer.model.user.UserRole;

@RestController
public class CoreUserController {

	@Autowired private CoreUserService coreUserService;

	@GetMapping("/user/all")
	private List<CoreUser> findAll() {
		return coreUserService.findAll();
	}

	@GetMapping("/user/all/tenant/{tenantId}")
	private List<CoreUser> getAllByTenantId(@PathVariable("tenantId") String tenantId) {
		return coreUserService.getAllByTenantId(tenantId);
	}

	@GetMapping("/user/all/rote/{role}")
	private List<CoreUser> getAllByUserRole(@PathVariable("role") UserRole userRole) {
		return coreUserService.getAllByUserRole(userRole);
	}

	@GetMapping("/all/rote/{role}/tenant/{tenantId}")
	private List<CoreUser> getAllByTenantIdAndUserRole(@PathVariable("role") UserRole userRole,
			@PathVariable("tenantId") String tenantId) {
		return coreUserService.getAllByTenantIdAndUserRole(userRole, tenantId);
	}

	@GetMapping("/user/{userId}")
	private CoreUser getByUserId(@PathVariable("userId") String userId) {
		return coreUserService.getByUserId(userId);
	}

	@GetMapping("/user/name/{username}")
	private CoreUser getByUserName(@PathVariable("username") String username) {
		return coreUserService.getByUserName(username);
	}

	@GetMapping("/find-by-ids")
	private List<CoreUser> getByUserId(@RequestBody List<String> userIds) {
		return coreUserService.getByUserId(userIds);
	}

	@GetMapping("/user/{userId}/marketplaces")
	private List<Marketplace> findRegisteredMarketplaces(@PathVariable("userId") String userId) {
		return coreUserService.findRegisteredMarketplaces(userId);
	}
	
	@PostMapping("/user/{userId}/register/{marketplaceId")
	private CoreUser registerToMarketplace(@PathVariable("userId") String userId, @PathVariable("marketplaceId") String marketplaceId, @RequestHeader("Authorization") String authorization) {
		return coreUserService.registerToMarketplace(userId, marketplaceId, authorization);
	}
	
	@PostMapping("/user/new")
	private CoreUser addNewUser(@RequestBody CoreUser user) {
		return coreUserService.addNewUser(user);
	}

	@PutMapping("/user/update")
	private CoreUser updateUser(@RequestBody CoreUser user) {
		return coreUserService.updateUser(user);
	}

	
	@PutMapping("/user/{userId}/subscribe/{marketplaceId}/{tenantId}/{role}")
	private CoreUser subscribeUserToTenant(@PathVariable("userId") String userId, @PathVariable("marketplaceId") String marketplaceId, @PathVariable("tenantId") String tenantId, @PathVariable("role") UserRole role) {
		return coreUserService.subscribeUserToTenant(userId, marketplaceId, tenantId, role);
	}

	@PutMapping("/user/{userId}/unsubscribe/{marketplaceId}/{tenantId}/{role}")
	private CoreUser unsubscribeUserFromTenant(@PathVariable("userId") String userId, @PathVariable("marketplaceId") String marketplaceId, @PathVariable("tenantId") String tenantId, @PathVariable("role") UserRole role) {
		return coreUserService.unsubscribeUserFromTenant(userId, marketplaceId, tenantId, role);
	}


}