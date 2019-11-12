package at.jku.cis.iVolunteer.marketplace.meta.configurator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.jku.cis.iVolunteer.model.meta.configurator.Configurator;

@RestController
public class ConfiguratorController {

	@Autowired
	ConfiguratorRepository configuratorRepository;
	
	@GetMapping("meta/configurator/all")
	List<Configurator> getAllConfigurators(@RequestParam(value = "sorted", required = false) String sortType) {
		
		if (sortType.equalsIgnoreCase("asc")) {
			return configuratorRepository.findAllWithSort(new Sort(Sort.Direction.ASC, "date"));

		} else if (sortType.equalsIgnoreCase("desc")) {
			return configuratorRepository.findAllWithSort(new Sort(Sort.Direction.DESC, "date"));
		} 
		
		return configuratorRepository.findAll();
	}
	
	@GetMapping("meta/configurator/{id}")
	Configurator getConfiguratorById(@PathVariable("id") String id) {
		return configuratorRepository.findOne(id);
	}
	
	@GetMapping("meta/configurator/by-name/{name}")
	List<Configurator> getConfiguratorByName(@PathVariable("name") String name) {
		return configuratorRepository.findByName(name);
	}
	
	@PostMapping("meta/configurator/new-empty")
	Configurator createNewEmptyConfigurator(@RequestBody String[] params) {
		if (params.length != 2) {
			return null;
		}
		
		Configurator c = new Configurator();
		c.setName(params[0]);
		c.setDescription(params[1]);
		
		return configuratorRepository.save(c);
	}
	
	@PostMapping("meta/configurator/new")
	Configurator createNewConfigurator(@RequestBody Configurator newConfigurator) {
		return saveConfigurator(newConfigurator);
	}
	
	@PutMapping("meta/configurator/save")
	Configurator saveConfigurator(@RequestBody Configurator updatedConfigurator) {
		
		return configuratorRepository.save(updatedConfigurator);
	}
	
	@DeleteMapping("meta/configurator/{id}/delete")
	void deleteConfigurator(@PathVariable("id") String id) {
		configuratorRepository.delete(id);
	}
	
	
}
