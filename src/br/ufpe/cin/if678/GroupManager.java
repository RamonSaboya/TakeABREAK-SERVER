package br.ufpe.cin.if678;

import java.util.HashMap;

import br.ufpe.cin.if678.business.Group;

public class GroupManager {

	private HashMap<String, Group> groups;

	public GroupManager() {
		this.groups = new HashMap<String, Group>();
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

}
