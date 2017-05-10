package enigma;

import static enigma.EnigmaException.*;

/* Extra Credit Only */

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Kevin Chen
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabet = chars.trim().toUpperCase();
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if C is in this alphabet. */
    boolean contains(char c) {
        for (int i = 0; i < size(); i++) {
            if (_alphabet.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet.charAt(index);
    }

    /** Returns the index of character C, which must be in the alphabet. */
    int toInt(char c) {
        return _alphabet.indexOf(c);
    }

    /** This is the alphabet. */
    private String _alphabet;
}
