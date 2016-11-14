package br.ufpe.cin.if678.repositories;

import java.util.Iterator;

public interface Repository<T> {

	public void insert(T obj);

	public void update(T obj);

	public T search(int ID);

	public boolean exists(T obj);

	public void remove(T obj);

	public Iterator<T> getIterator();

}
