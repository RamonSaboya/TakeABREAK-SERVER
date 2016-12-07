package br.ufpe.cin.if678.communication;

import java.net.InetSocketAddress;
import java.util.Map;

import br.ufpe.cin.if678.ServerController;
import br.ufpe.cin.if678.business.Group;
import br.ufpe.cin.if678.util.Pair;
import br.ufpe.cin.if678.util.Tuple;

public class Listener {

	private ServerController controller;

	public Listener(ServerController controller) {
		this.controller = controller;
	}

	public void onUserConnect(int ID, String username) {
		if (controller.getNameToID().containsKey(username)) {
			controller.getWriter(ID).queueAction(ServerAction.VERIFY_USERNAME, -1);
			return;
		}

		InetSocketAddress address = controller.getIDToNameAddress().get(ID).getSecond();

		controller.getWriter(ID).queueAction(ServerAction.VERIFY_USERNAME, ID);

		controller.getNameToID().put(username, ID);
		controller.getIDToNameAddress().replace(ID, new Pair<String, InetSocketAddress>(username, address));

		Tuple<Integer, String, InetSocketAddress> data = new Tuple<Integer, String, InetSocketAddress>(ID, username, address);
		for (Map.Entry<Integer, Pair<Writer, Thread>> entry : controller.getWriters()) {
			int userID = entry.getKey();
			Writer writer = entry.getValue().getFirst();

			if (userID != ID) {
				writer.queueAction(ServerAction.USER_CONNECTED, data);
			}
		}
	}

	public void onUserListRequest(int ID) {
		controller.getWriter(ID).queueAction(ServerAction.USERS_LIST_UPDATE, controller.getIDToNameAddress());
	}

	public void onGroupCreate(Pair<Integer, String> data) {
		int founder = data.getFirst();
		String name = data.getSecond();

		Group group = controller.getGroupManager().getGroup(name);

		if (group == null) {
			group = controller.getGroupManager().createGroup(founder, name);
		}

		controller.getWriter(founder).queueAction(ServerAction.SEND_GROUP, group);
	}

	public void onGroupAddMember(Tuple<Integer, String, Integer> data) {
		int requestFrom = data.getFirst();
		String name = data.getSecond();
		Integer user = data.getThird();

		Group group = controller.getGroupManager().getGroup(name);
		if (!group.isMember(user)) {
			group.addMember(user);
		}

		if (requestFrom != group.getFounderID()) {
			controller.getWriter(requestFrom).queueAction(ServerAction.GROUP_ADD_MEMBER, new Pair<String, Integer>(name, user));
		}

		controller.getWriter(group.getFounderID()).queueAction(ServerAction.GROUP_ADD_MEMBER, new Pair<String, Integer>(name, user));
		if (group.getMembersAmount() > 2) {
			for (int member : group.getMembers().keySet()) {
				if (member != requestFrom) {
					controller.getWriter(member).queueAction(ServerAction.GROUP_ADD_MEMBER, new Pair<String, Integer>(name, user));
				}
			}
		}
	}

	public void onGroupMessage(Tuple<String, Integer, Object> tuple) {
		String name = tuple.getFirst();

		Group group = controller.getGroupManager().getGroup(name);

		controller.getWriter(group.getFounderID()).queueAction(ServerAction.GROUP_MESSAGE, tuple);
		for (int member : group.getMembers().keySet()) {
			controller.getWriter(member).queueAction(ServerAction.GROUP_MESSAGE, tuple);
		}
	}

}
