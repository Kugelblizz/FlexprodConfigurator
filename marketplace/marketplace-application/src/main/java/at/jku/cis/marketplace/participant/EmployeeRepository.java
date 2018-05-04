package at.jku.cis.marketplace.participant;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

	Employee findByUsername(String username);
}