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

	public void onUserConnect(InetSocketAddress address, String username) {
		System.out.println("[LOG] USUÁRIO CONECTOU: " + username + " (" + address.getAddress().getHostAddress() + ":" + address.getPort() + ")");

		controller.getAddressToName().put(address, username);

		Pair<InetSocketAddress, String> data = new Pair<InetSocketAddress, String>(address, username);
		for (Map.Entry<InetSocketAddress, Pair<Writer, Thread>> entry : controller.getWriters()) {
			InetSocketAddress userAddress = entry.getKey();
			Writer writer = entry.getValue().getFirst();

			if (userAddress != address) {
				writer.queueAction(ServerAction.SEND_USER_CONNECTED, data);
			}
		}
	}

	public void onUserListRequest(InetSocketAddress address) {
		controller.getWriter(address).queueAction(ServerAction.SEND_USERS_LIST, controller.getAddressToName());
	}

	public void onGroupCreate(Pair<InetSocketAddress, String> data) {
		InetSocketAddress founder = data.getFirst();
		String name = data.getSecond();

		Group group = controller.getGroupManager().getGroup(name);

		if (group == null) {
			group = controller.getGroupManager().createGroup(founder, name);
		}

		System.out.println("Enviando grupo: " + group.getName());
		controller.getWriter(founder).queueAction(ServerAction.SEND_GROUP, group);
	}

	public void onGroupAddMember(Pair<String, InetSocketAddress> data) {
		String name = data.getFirst();
		InetSocketAddress user = data.getSecond();

		Group group = controller.getGroupManager().getGroup(name);
		group.addMember(user);

		controller.getWriter(group.getFounder()).queueAction(ServerAction.GROUP_ADD_MEMBER, new Pair<String, InetSocketAddress>(name, user));
		if (group.getMembersAmount() > 2) {
			for (InetSocketAddress member : group.getMembers().keySet()) {
				controller.getWriter(member).queueAction(ServerAction.GROUP_ADD_MEMBER, new Pair<String, InetSocketAddress>(name, user));
			}
		}
	}

	public void onGroupMessage(Tuple<String, InetSocketAddress, Object> tuple) {
		String name = tuple.getFirst();

		Group group = controller.getGroupManager().getGroup(name);

		controller.getWriter(group.getFounder()).queueAction(ServerAction.GROUP_MESSAGE, tuple);
		for (InetSocketAddress member : group.getMembers().keySet()) {
			controller.getWriter(member).queueAction(ServerAction.GROUP_MESSAGE, tuple);
		}
	}

}
