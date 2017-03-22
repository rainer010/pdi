/**
 * 21/01/2012 21:39:45 Copyright (C) 2011 Darío L. García
 * 
 * <a rel="license" href="http://creativecommons.org/licenses/by/3.0/"><img
 * alt="Creative Commons License" style="border-width:0"
 * src="http://i.creativecommons.org/l/by/3.0/88x31.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" href="http://purl.org/dc/dcmitype/Text"
 * property="dct:title" rel="dct:type">Software</span> by <span
 * xmlns:cc="http://creativecommons.org/ns#" property="cc:attributionName">Darío García</span> is
 * licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative
 * Commons Attribution 3.0 Unported License</a>.
 */
package ar.com.dgarcia.colecciones.sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Esta clase representa un {@link Set} pero que permite operaciones concurrentes. Internamente está
 * implementado con un {@link ConcurrentHashMap} utilizando el mismo objeto como clave/valor
 * 
 * @author D. García
 */
public class ConcurrentHashSet<E> implements Set<E> {

	private final ConcurrentHashMap<E, E> internalMap = new ConcurrentHashMap<E, E>();

	/**
	 * @see Set#size()
	 */
	public int size() {
		return internalMap.size();
	}

	/**
	 * @see Set#isEmpty()
	 */
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	/**
	 * @see Set#contains(Object)
	 */
	public boolean contains(final Object o) {
		return internalMap.containsKey(o);
	}

	/**
	 * @see Set#iterator()
	 */
	public Iterator<E> iterator() {
		return getInternalSet().iterator();
	}

	/**
	 * Devuelve el set interno obtenido del mapa
	 * 
	 * @return El set real
	 */
	private Set<E> getInternalSet() {
		return internalMap.keySet();
	}

	/**
	 * @see Set#toArray()
	 */
	public Object[] toArray() {
		return getInternalSet().toArray();
	}

	/**
	 * @see Set#toArray(T[])
	 */
	public <T> T[] toArray(final T[] a) {
		return getInternalSet().toArray(a);
	}

	/**
	 * @see Set#add(Object)
	 */
	public boolean add(final E o) {
		final E previousValue = internalMap.putIfAbsent(o, o);
		final boolean noConteniaValor = previousValue == null;
		return noConteniaValor;
	}

	/**
	 * @see Set#remove(Object)
	 */
	public boolean remove(final Object o) {
		final E removed = internalMap.remove(o);
		final boolean conteniaValor = removed != null;
		return conteniaValor;
	}

	/**
	 * @see Set#containsAll(Collection)
	 */
	public boolean containsAll(final Collection<?> c) {
		final boolean containsAll = getInternalSet().containsAll(c);
		return containsAll;
	}

	/**
	 * @see Set#addAll(Collection)
	 */
	public boolean addAll(final Collection<? extends E> c) {
		boolean modified = false;
		for (final E e : c) {
			final boolean added = add(e);
			modified |= added;
		}
		return modified;
	}

	/**
	 * @see Set#retainAll(Collection)
	 */
	public boolean retainAll(final Collection<?> c) {
		final boolean retained = getInternalSet().retainAll(c);
		return retained;
	}

	/**
	 * @see Set#removeAll(Collection)
	 */
	public boolean removeAll(final Collection<?> c) {
		boolean modified = false;
		for (final Object object : c) {
			final boolean removed = remove(object);
			modified |= removed;
		}
		return modified;
	}

	/**
	 * @see Set#clear()
	 */
	public void clear() {
		internalMap.clear();
	}

	/**
	 * @see Object#toString()
	 */
	
	public String toString() {
		return getInternalSet().toString();
	}
}
