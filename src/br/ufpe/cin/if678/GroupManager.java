package br.ufpe.cin.if678;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class GroupManager {

	private ServerController controller;

	private HashMap<String, Group> groups;

	public GroupManager(ServerController controller) {
		this.controller = controller;

		this.groups = new HashMap<String, Group>();
	}

	public Group getGroup(String name) {
		return groups.get(name);
	}

	public boolean groupExists(String name) {
		return groups.containsKey(name);
	}

	public Group createGroup(InetSocketAddress founder, String name) {
		return groups.put(name, new Group(name, founder));
	}

}
