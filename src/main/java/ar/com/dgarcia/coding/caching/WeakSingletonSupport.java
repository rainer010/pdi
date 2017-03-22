/**
 * Created on: Sep 1, 2013 12:39:46 AM by: Dario L. Garcia
 * 
 * <a rel="license" href="http://creativecommons.org/licenses/by/2.5/ar/"><img
 * alt="Creative Commons License" style="border-width:0"
 * src="http://i.creativecommons.org/l/by/2.5/ar/88x31.png" /></a><br />
 * <span xmlns:dc="http://purl.org/dc/elements/1.1/"
 * href="http://purl.org/dc/dcmitype/InteractiveResource" property="dc:title"
 * rel="dc:type">Bean2Bean</span> by <a xmlns:cc="http://creativecommons.org/ns#"
 * href="https://sourceforge.net/projects/bean2bean/" property="cc:attributionName"
 * rel="cc:attributionURL">Dar&#237;o Garc&#237;a</a> is licensed under a <a rel="license"
 * href="http://creativecommons.org/licenses/by/2.5/ar/">Creative Commons Attribution 2.5 Argentina
 * License</a>.<br />
 * Based on a work at <a xmlns:dc="http://purl.org/dc/elements/1.1/"
 * href="https://bean2bean.svn.sourceforge.net/svnroot/bean2bean"
 * rel="dc:source">bean2bean.svn.sourceforge.net</a>
 */
package ar.com.dgarcia.coding.caching;

/**
 * Esta clase sirve de base para las instancias que representan singleton que pueden ser garbage
 * collecteados por la VM.<br>
 * Se define el {@link Object#equals(Object)} y {@link Object#hashCode()} para mantener la igualdad
 * aunque sean instancias distintas en el tiempo (sólo a modo de resguardo)
 * 
 * @author dgarcia
 */
public class WeakSingletonSupport {

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Usamos la clase como referencia de unicidad por ser variable las instancias
		final WeakSingletonSupport singleton = this;
		return singletonHashFor(singleton);
	}

	/**
	 * Devuelve el hashcode utilizado para las instancias que representan Singletons que pueden ser
	 * eliminados de memoria.<br>
	 * Se utiliza el hash de su clase como referencia
	 * 
	 * @param singleton
	 *            La instancia de la que se quiere obtener el hash
	 * @return El hash para la instancia
	 */
	public static int singletonHashFor(final Object singleton) {
		return singleton.getClass().hashCode();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		final WeakSingletonSupport singleton = this;
		return singletonEquals(obj, singleton);
	}

	/**
	 * Indica si la instancia pasada como obje representa el mismo singleton que el indicado.<br>
	 * Se utiliza la clase como referencia de igualdad. Son iguales si son de la misma clase
	 * 
	 * @param obj
	 *            El objeto a evaluar
	 * @param singleton
	 *            El singleton comparado
	 * @return true si representan el mismo singleton
	 */
	public static boolean singletonEquals(final Object obj, final Object singleton) {
		if (obj == null) {
			return false;
		}
		// Todas las instancias de esta clase representan lo mismo
		final boolean esDeLaMismaClase = obj.getClass().equals(singleton.getClass());
		return esDeLaMismaClase;
	}
}
