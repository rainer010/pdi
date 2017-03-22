/**
 * 08/07/2012 09:59:16 Copyright (C) 2011 Darío L. García
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
package ar.com.dgarcia.lang.conc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import ar.com.dgarcia.coding.exceptions.UnsuccessfulWaitException;

/**
 * Esta clase es la implementación de un mapa concurrente pero que permite la existencia de valores
 * null
 * 
 * @author D. García
 */
public class ConcurrentHashMapWithNull<K, V> implements Map<K, V>, ConcurrentMap<K, V>, ReadWriteOperable {

	private ReadWriteCoordinator coordinator;

	private HashMap<K, V> internalMap;

	public ConcurrentHashMapWithNull() {
		initialize();
	}

	/**
	 * El mapa utilizado internamente por este mapa para almacenar los valores
	 * 
	 * @return El mapa interno
	 */
	public HashMap<K, V> getInternalMap() {
		return internalMap;
	}

	/**
	 * Inicializa el estado necesario de esta instancia
	 */
	private void initialize() {
		coordinator = ReadWriteCoordinator.create();
		internalMap = new HashMap<K, V>();
	}

	/**
	 * Permite realizar una operación de lectura en este mapa que no modifica el estado del mismo
	 * 
	 * @see ReadWriteOperable#doReadOperation(Callable)
	 */
	
	public <T> T doReadOperation(final Callable<T> readOperation) throws UnsuccessfulWaitException {
		return coordinator.doReadOperation(readOperation);
	}

	/**
	 * Permite realizar una operación de modificación en este mapa
	 * 
	 * @see ReadWriteOperable#doWriteOperation(Callable)
	 */
	
	public <T> T doWriteOperation(final Callable<T> writeOperation) throws UnsuccessfulWaitException {
		return coordinator.doWriteOperation(writeOperation);
	}

	/**
	 * @see Map#size()
	 */
	
	public int size() {
		return doReadOperation(new Callable<Integer>() {
			
			public Integer call() throws Exception {
				return internalMap.size();
			}
		});
	}

	/**
	 * @see Map#isEmpty()
	 */
	
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * @see Map#get(Object)
	 */
	
	public V get(final Object key) {
		return doReadOperation(new Callable<V>() {
			
			public V call() throws Exception {
				return internalMap.get(key);
			}
		});
	}

	/**
	 * @see Object#equals(Object)
	 */
	
	public boolean equals(final Object o) {
		return doReadOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return internalMap.equals(o);
			}
		});
	}

	/**
	 * @see Map#containsKey(Object)
	 */
	
	public boolean containsKey(final Object key) {
		return doReadOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return internalMap.containsKey(key);
			}
		});
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#hashCode()
	 */
	
	public int hashCode() {
		return doReadOperation(new Callable<Integer>() {
			
			public Integer call() throws Exception {
				return internalMap.hashCode();
			}
		});
	}

	
	public String toString() {
		return internalMap.toString();
	}

	/**
	 * @see Map#containsValue(Object)
	 */
	
	public boolean containsValue(final Object value) {
		return doReadOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return internalMap.containsValue(value);
			}
		});
	}

	/**
	 * @see Object#clone()
	 */
	
	public Object clone() {
		final ConcurrentHashMapWithNull<K, V> copia = new ConcurrentHashMapWithNull<K, V>();
		doReadOperation(new Callable<Void>() {
			
			public Void call() throws Exception {
				copia.putAll(internalMap);
				return null;
			}
		});
		return copia;
	}

	/**
	 * Devuelve una copia de los keys actuales cuya modificación no tiene efecto en este mapa
	 * 
	 * @see Map#keySet()
	 */
	
	public Set<K> keySet() {
		return doReadOperation(new Callable<Set<K>>() {
			
			public Set<K> call() throws Exception {
				final Set<K> internalSet = internalMap.keySet();
				final HashSet<K> copia = new HashSet<K>(internalSet);
				return copia;
			}
		});
	}

	/**
	 * Devuelve una copia de los valores actuales
	 * 
	 * @see Map#values()
	 */
	
	public Collection<V> values() {
		return doReadOperation(new Callable<Collection<V>>() {
			
			public Collection<V> call() throws Exception {
				final Collection<V> internalCollection = internalMap.values();
				final ArrayList<V> copia = new ArrayList<V>(internalCollection);
				return copia;
			}
		});
	}

	/**
	 * Devuelve una copia de solo lectura
	 * 
	 * @see Map#entrySet()
	 */
	
	public Set<Entry<K, V>> entrySet() {
		return doReadOperation(new Callable<Set<Entry<K, V>>>() {
			
			public Set<Entry<K, V>> call() throws Exception {
				final Set<Entry<K, V>> internalSet = internalMap.entrySet();
				final HashSet<Entry<K, V>> copia = new HashSet<Entry<K, V>>(internalSet);
				return copia;
			}
		});
	}

	/**
	 * @see Map#put(Object, Object)
	 */
	
	public V put(final K key, final V value) {
		return doWriteOperation(new Callable<V>() {
			
			public V call() throws Exception {
				return internalMap.put(key, value);
			}
		});
	}

	/**
	 * @see Map#putAll(Map)
	 */
	
	public void putAll(final Map<? extends K, ? extends V> m) {
		doWriteOperation(new Callable<Void>() {
			
			public Void call() throws Exception {
				internalMap.putAll(m);
				return null;
			}
		});
	}

	/**
	 * @see Map#remove(Object)
	 */
	
	public V remove(final Object key) {
		return doWriteOperation(new Callable<V>() {
			
			public V call() throws Exception {
				return internalMap.remove(key);
			}
		});
	}

	/**
	 * 
	 * @see HashMap#clear()
	 */
	
	public void clear() {
		doWriteOperation(new Callable<Void>() {
			
			public Void call() throws Exception {
				internalMap.clear();
				return null;
			}
		});
	}

	/**
	 * @see ConcurrentMap#putIfAbsent(Object, Object)
	 */
	
	public V putIfAbsent(final K key, final V value) {
		return doWriteOperation(new Callable<V>() {
			
			public V call() throws Exception {
				if (!internalMap.containsKey(key)) {
					return internalMap.put(key, value);
				} else {
					return internalMap.get(key);
				}
			}
		});
	}

	/**
	 * @see ConcurrentMap#remove(Object, Object)
	 */
	
	public boolean remove(final Object key, final Object value) {
		return doWriteOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				if (internalHasKeyAndValue(key, value)) {
					internalMap.remove(key);
					return true;
				} else {
					return false;
				}
			}

		});
	}

	/**
	 * Indica si el mapa interno tiene una clave y valor como los indicados
	 * 
	 * @param key
	 *            La clave a evaluar
	 * @param value
	 *            El valor evaluado
	 * @return true si tiene la key y el valor indicados
	 */
	private boolean internalHasKeyAndValue(final Object key, final Object value) {
		return internalMap.containsKey(key) && internalMap.get(key).equals(value);
	}

	/**
	 * Indica si este mapa tiene la clave con el valor indicado
	 * 
	 * @param key
	 *            La clave a evaluar
	 * @param value
	 *            El valor a comparar
	 * @return true si tiene la clave y está mapeada al valor pasado (comparando con equals)
	 */
	public boolean hasKeyAndValue(final Object key, final Object value) {
		return doReadOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return internalHasKeyAndValue(key, value);
			}
		});
	}

	/**
	 * @see ConcurrentMap#replace(Object, Object,
	 *      Object)
	 */
	
	public boolean replace(final K key, final V oldValue, final V newValue) {
		return doWriteOperation(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				if (internalHasKeyAndValue(key, oldValue)) {
					internalMap.put(key, newValue);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * @see ConcurrentMap#replace(Object, Object)
	 */
	
	public V replace(final K key, final V value) {
		return doWriteOperation(new Callable<V>() {
			
			public V call() throws Exception {
				if (internalMap.containsKey(key)) {
					return internalMap.put(key, value);
				} else {
					return null;
				}
			}
		});
	}

}
