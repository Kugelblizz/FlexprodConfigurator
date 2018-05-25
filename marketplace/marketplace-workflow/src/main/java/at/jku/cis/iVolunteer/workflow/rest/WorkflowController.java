package at.jku.cis.iVolunteer.workflow.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	private static final String WORKFLOW_ACTIVITY_LABEL = "label";

	@Autowired
	private TaskService taskService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private WorkflowStepService workflowStepService;

	@Autowired
	private WorkflowTypeService workfowTypeService;

	@GetMapping("/type")
	public List<WorkflowType> getWorkflowTypes() {
		return workfowTypeService.getWorkflowTypes();
	}

	@GetMapping("/processId")
	public String getProcessId(@RequestParam("taskId") String taskId) {
		return runtimeService.createExecutionQuery().variableValueEquals("taskId", taskId).singleResult()
				.getProcessInstanceId();
	}

	// curl -H "Content-Type: application/json" -d ''
	// http://localhost:8080/workflow/standard?taskId=abcdedfg
	@PostMapping("/{processKey}")
	public String startWorkflow(@PathVariable("processKey") String processKey, @RequestParam("taskId") String taskId) {
		Map<String, Object> params = new HashMap<>();
		params.put("taskId", taskId);
		return runtimeService.startProcessInstanceByKey(processKey, params).getProcessInstanceId();
	}

	// curl -H "Content-Type: application/json"
	// http://localhost:8080/workflow/standard/8/task
	@GetMapping("/{processKey}/{instanceId}/task")
	public List<WorkflowStep> getNextTasksByInstanceId(@PathVariable("processKey") String processKey,
			@PathVariable("instanceId") String instanceId) {
		return workflowStepService
				.getNextWorkflowSteps(retrieveActiveTaskByProcessKeyAndInstanceId(processKey, instanceId));
	}

	@PostMapping("/{processKey}/{instanceId}/task")
	public void completeTask(@PathVariable("processKey") String processKey,
			@PathVariable("instanceId") String instanceId, @RequestBody WorkflowStep workflowStep) {

		Task task = retrieveActiveTaskByProcessKeyAndInstanceId(processKey, instanceId);
		if (!StringUtils.equals(task.getId(), workflowStep.getTaskId())) {
			throw new UnsupportedOperationException();
		}

		Map<String, Object> params = new HashMap<>();
		params.put(WORKFLOW_ACTIVITY_LABEL, workflowStep.getLabel());
		if (workflowStep.getParams() != null) {
			workflowStep.getParams().forEach((name, value) -> params.put(name, value));
		}
		taskService.complete(workflowStep.getTaskId(), params);
	}

	@DeleteMapping("/{processKey}/{instanceId}")
	public void cancelWorkflow(@PathVariable("processKey") String processKey,
			@PathVariable("instanceId") String instanceId) {
		runtimeService.deleteProcessInstance(instanceId, "Workflow is aborted");
	}

	private Task retrieveActiveTaskByProcessKeyAndInstanceId(String processKey, String instanceId) {
		return taskService.createTaskQuery().processInstanceId(instanceId).active().singleResult();
	}
}