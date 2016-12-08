package br.ufpe.cin.if678;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufpe.cin.if678.business.Group;

public class GroupManager {

	private HashMap<String, Group> groups;

	public GroupManager() {
		this.groups = new HashMap<String, Group>();
	}

	public HashMap<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(HashMap<String, Group> groups) {
		this.groups = groups;
	}

	public Group getGroup(String name) {
		return groups.get(name);
	}

	public boolean groupExists(String name) {
		return groups.containsKey(name);
	}

	public Group createGroup(int founderID, String name) {
		groups.put(name, new Group(name, founderID));

		return groups.get(name);
	}

	public List<Group> getSubscriptions(int ID) {
		List<Group> groups = new ArrayList<Group>();

		for (Group group : this.groups.values()) {
			if (group.isMember(ID)) {
				groups.add(group);
			}
		}

		return groups;
	}

}
