package at.jku.cis.iVolunteer.configurator.meta.core.class_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import at.jku.cis.iVolunteer.configurator.model.meta.core.property.definition.treeProperty.TreePropertyDefinition;
import at.jku.cis.iVolunteer.configurator.model.meta.core.property.definition.treeProperty.TreePropertyEntry;
import at.jku.cis.iVolunteer.configurator.model.meta.core.property.definition.treeProperty.TreePropertyRelationship;

@Service
public class CollectionService {

	public static final String PATH_DELIMITER = Character.toString((char) 28);

	public List<TreePropertyEntry> collectTreePropertyDefinitions(TreePropertyDefinition treePropertyDefinition) {
		return getTreePropertyEntriesDFS(treePropertyDefinition.getId(), 0, new ArrayList<>(), treePropertyDefinition);
	}

	private List<TreePropertyEntry> getTreePropertyEntriesDFS(String rootId, int level, List<TreePropertyEntry> list,
			TreePropertyDefinition treePropertyDefinition) {
		Stack<TreePropertyRelationship> stack = new Stack<>();
		List<TreePropertyRelationship> relationships = treePropertyDefinition.getRelationships().stream()
				.filter(r -> r.getSourceId().equals(rootId)).collect(Collectors.toList());

		Collections.reverse(relationships);
		stack.addAll(relationships);

		if (stack == null || stack.size() <= 0) {
			return list;
		}

		while (!stack.isEmpty()) {
			TreePropertyRelationship relationship = stack.pop();
			TreePropertyEntry enumEntry = treePropertyDefinition.getEntries().stream()
					.filter(e -> e.getId().equals(relationship.getTargetId())).findFirst().get();
			enumEntry.setPosition(new int[level + 1]);
			enumEntry.setLevel(level);
			list.add(enumEntry);
			this.getTreePropertyEntriesDFS(enumEntry.getId(), level + 1, list, treePropertyDefinition);
		}

		return list;
	}

	

}
