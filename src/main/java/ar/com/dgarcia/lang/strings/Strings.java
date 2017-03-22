/**
 * Created on: Aug 31, 2013 6:59:53 PM by: Dario L. Garcia
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
package ar.com.dgarcia.lang.strings;

/**
 * Esta clase junta algunos métodos para operaciones con Strings
 * 
 * @author dgarcia
 */
public class Strings {

	private static final char LIM_ST_UCASE = 'A' - 1;
	private static final char LI_N_UCASE = 'Z' + 1;
	private static final char[] LC = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\010', '\011',
			'\012', '\013', '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026',
			'\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043',
			'\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053', '\054', '\055', '\056', '\057', '\060',
			'\061', '\062', '\063', '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073', '\074', '\075',
			'\076', '\077', '\100', '\141', '\142', '\143', '\144', '\145', '\146', '\147', '\150', '\151', '\152',
			'\153', '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163', '\164', '\165', '\166', '\167',
			'\170', '\171', '\172', '\133', '\134', '\135', '\136', '\137', '\140', '\141', '\142', '\143', '\144',
			'\145', '\146', '\147', '\150', '\151', '\152', '\153', '\154', '\155', '\156', '\157', '\160', '\161',
			'\162', '\163', '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\173', '\174', '\175', '\176',
			'\177' };

	/**
	 * Implementacion rapida y sin internacionalizacion para lower case tomada de
	 * http://sourceforge.net/p/faststringutil/code/106/tree/src/com/baculsoft/lang/StringUtil.java.<br>
	 * Solo convierte las letras de la tabla ascii a lower case. El resto de los chars los deja como
	 * vienen
	 * 
	 * @param s
	 * @return String.toLowerCase()
	 * @see String#toLowerCase()
	 */
	public static final String toAsciiLowerCase(final String s) {
		char[] c = null;
		int i = s.length();
		while (i-- > 0) {
			final char c1 = s.charAt(i);
			if (c1 > LIM_ST_UCASE && c1 < LI_N_UCASE) {
				final char c2 = LC[c1];
				if (c1 != c2) {
					c = s.toCharArray();
					c[i] = c2;
					break;
				}
			}
		}
		char c1;
		while (i-- > 0) {
			c1 = c[i];
			c[i] = toAsciiLowerCaseChar(c1);
		}
		return c == null ? s : new String(c);
	}

	/**
	 * Devuelve la version lower case del caracter pasado usando la tabla ascii como referencia.<br>
	 * Esta version es más rápida porque desconoce caracteres con acentos o simbolos internacionales
	 * 
	 * @param caracter
	 *            El caracter a obtener como lower
	 * @return El mismo char si es lower o no es alfa. El char modificado si es un alfa en upper
	 */
	public static char toAsciiLowerCaseChar(final char caracter) {
		if (caracter > LIM_ST_UCASE && caracter < LI_N_UCASE) {
			return LC[caracter];
		}
		return caracter;
	}

	/**
	 * Version a medida del equals que intenta optimizar la comparacion no siguiendo un orden lineal
	 * (las cadenas de palabras suelen tener los caracteres más significativos al principio o al
	 * final).<br>
	 * Al comparar desde los extremos primero, se evita recorrer todo para encontrar diferencias al
	 * final (o al principio).
	 * 
	 * @param thisString
	 *            Una cadena a comparar utilizada como referencia
	 * @param thatString
	 *            La cadena comparada que puede ser pasada a lower case segun el parametro
	 *            ignoreCase para la comparacion
	 * @param ignoreCase
	 *            Indica si se debe ignorar el case de la segunda cadena cuando un caracter no
	 *            coincide
	 * @return True si pueden considerarse la misma cadena (ignorando el case o no)
	 */
	public static boolean fastEquals(final String thisString, final String thatString, final boolean ignoreCase) {
		if (thisString == thatString) {
			return true;
		}
		final int longitud = thisString.length();
		if (longitud != thatString.length()) {
			return false;
		}
		// Uso dos indices para ir desde los extremos al centro
		int indiceFinal = longitud - 1;
		int indiceInicial = 0;

		while (indiceInicial < indiceFinal) {
			// Chequeamos el caracter del extremo final
			if (!internalEqualCharAt(thisString, thatString, indiceFinal, ignoreCase)) {
				// Ya sabemos que no son iguales porque difieren en el char
				return false;
			}

			// Chequeamos el caracter del extremo inicial
			if (!internalEqualCharAt(thisString, thatString, indiceInicial, ignoreCase)) {
				// Ya sabemos que no son iguales porque difieren en el char
				return false;
			}

			indiceInicial++;
			indiceFinal--;
		}
		// Si es longitud impar me va a quedar el caracter del medio por chequear
		if (indiceInicial == indiceFinal) {
			if (!internalEqualCharAt(thisString, thatString, indiceInicial, ignoreCase)) {
				// No es el mismo caracter
				return false;
			}
		}
		return true;
	}

	/**
	 * Compara el caracter del indice indicado en ambas cadenas para determinar si representan el
	 * mismo caracter
	 * 
	 * @param thisString
	 *            La cadena de referencia
	 * @param thatString
	 *            La cadena comparada
	 * @param indiceComparado
	 *            El indice del caracter a comparar
	 * @param ignoreCase
	 *            true si la segunda cadena puede pasarse a lower el caracter comparado
	 * @return true si representan el mismo caracter, false si sin distintos
	 */
	private static boolean internalEqualCharAt(final String thisString, final String thatString,
			final int indiceComparado, final boolean ignoreCase) {
		final char thisChar = thisString.charAt(indiceComparado);
		final char thatChar = thatString.charAt(indiceComparado);
		if (thisChar == thatChar) {
			// Nada maś que verificar
			return true;
		}
		// No es el mismo char
		if (!ignoreCase) {
			// No podemos ignorar la diferencia
			return false;
		}
		// Vemos si es el mismo char al pasar a lower
		final char thatLowerChar = toAsciiLowerCaseChar(thatChar);
		// Si despues de pasar a lower, hay diferencia entonces no son iguales
		return thisChar == thatLowerChar;
	}

}
