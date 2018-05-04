package at.jku.cis.marketplace.task.interaction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.jku.cis.marketplace.participant.Participant;
import at.jku.cis.marketplace.participant.Volunteer;
import at.jku.cis.marketplace.task.Task;

@Service
public class TaskInteractionService {

	@Autowired
	private TaskInteractionRepository taskInteractionRepository;

	public Set<Volunteer> findAssignedVolunteersByTask(Task task) {
		Set<Volunteer> volunteers = new HashSet<>();
		Map<Participant, List<TaskInteraction>> participant2TaskInteractions = findByTaskGroupedByParticipant(task);

		participant2TaskInteractions.entrySet().forEach(entry -> {
			List<TaskInteraction> interactions = entry.getValue();
			if (TaskVolunteerOperation.ASSIGNED == interactions.get(interactions.size() - 1).getOperation()) {
				volunteers.add((Volunteer) entry.getKey());
			}

		});

		return volunteers;
	}

	private Map<Participant, List<TaskInteraction>> findByTaskGroupedByParticipant(Task task) {
		List<TaskInteraction> taskInteractions = taskInteractionRepository.findByTask(task);
		return taskInteractions.stream().collect(Collectors.groupingBy(t -> t.getParticipant()));
	}
}