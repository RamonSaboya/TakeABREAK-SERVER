package br.ufpe.cin.if678.communication;

public enum Action {

	ALL_ONLINE(1),
	LOGIN(2);
	
	private int ID;
	
	Action(int ID) {
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}

}
