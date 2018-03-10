package at.jku.csi.marketplace.task;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import at.jku.csi.marketplace.exception.BadRequestException;
import at.jku.csi.marketplace.security.LoginService;
import at.jku.csi.marketplace.task.interaction.TaskInteraction;
import at.jku.csi.marketplace.task.interaction.TaskInteractionRepository;

@RestController
public class TaskOperationController {

	@Autowired
	private LoginService loginService;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private TaskInteractionRepository taskInteractionRepository;

	@PostMapping("/task/{id}/start")
	public void startTask(@PathVariable("id") String id) {
		Task task = taskRepository.findOne(id);
		if (task == null || !isCreatedOrCanceledTask(task)) {
			throw new BadRequestException();
		}
		updateTaskStatus(task, TaskStatus.STARTED);
	}

	@PostMapping("/task/{id}/finish")
	public void finishTask(@PathVariable("id") String id) {
		Task task = taskRepository.findOne(id);
		if (task == null || !isStartedTask(task)) {
			throw new BadRequestException();
		}
		updateTaskStatus(task, TaskStatus.FINISHED);
	}

	@PostMapping("/task/{id}/cancel")
	public void cancelTask(@PathVariable("id") String id) {
		Task task = taskRepository.findOne(id);
		if (task == null || !isStartedTask(task)) {
			throw new BadRequestException();
		}
		updateTaskStatus(task, TaskStatus.CANCELED);
	}

	private void updateTaskStatus(Task task, TaskStatus taskStatus) {
		task.setStatus(taskStatus);
		Task updatedTask = taskRepository.save(task);
		insertTaskInteraction(updatedTask);
	}

	private void insertTaskInteraction(Task task) {
		TaskInteraction taskInteraction = new TaskInteraction();
		taskInteraction.setTask(task);
		taskInteraction.setParticipant(loginService.getLoggedInParticipant());
		taskInteraction.setTimestamp(new Date());
		taskInteraction.setOperation(task.getStatus());
		taskInteractionRepository.insert(taskInteraction);
	}

	private boolean isStartedTask(Task task) {
		return TaskStatus.STARTED == task.getStatus();
	}

	private boolean isCreatedOrCanceledTask(Task task) {
		return TaskStatus.CREATED == task.getStatus() || TaskStatus.CANCELED == task.getStatus();
	}

}
