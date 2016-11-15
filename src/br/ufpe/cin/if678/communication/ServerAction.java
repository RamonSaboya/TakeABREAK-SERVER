package br.ufpe.cin.if678.communication;

/**
 * Mapa de possíveis ações de comunicação servidor-cliente
 * 
 * @author Ramon
 */
public enum ServerAction {

	ALL_ONLINE(1);

	private int ID; // ID associado a ação

	/**
	 * Construtor do enum
	 * 
	 * @param ID ID da ação
	 */
	ServerAction(int ID) {
		this.ID = ID;
	}

	/**
	 * Retorna o ID da ação
	 * 
	 * @return ID da ação
	 */
	public int getID() {
		return ID;
	}

}
