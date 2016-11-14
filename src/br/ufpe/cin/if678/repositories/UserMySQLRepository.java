package br.ufpe.cin.if678.repositories;

import java.util.Iterator;

import br.ufpe.cin.if678.business.User;

public class UserMySQLRepository implements Repository<User> {

	@Override
	public void insert(User obj) {
	}

	@Override
	public void update(User obj) {
	}

	@Override
	public User search(int ID) {
		return null;
	}

	@Override
	public boolean exists(User obj) {
		return false;
	}

	@Override
	public void remove(User obj) {
	}

	@Override
	public Iterator<User> getIterator() {
		return null;
	}

}
