package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Kevin Chen
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        cycles = cycles.replaceAll("\\s+", "");
        if (cycles.length() >= 1 && cycles.charAt(cycles.length() - 1) != ')') {
            throw error("Wrongly formatted cycle entered");
        }
        _alphabet = alphabet;
        _condensed = cycles.replaceAll("[ (]", "").split("\\)");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < _condensed.length; i++) {
            for (int j = 0; j < cycle.length(); j++) {
                if (_condensed[i].indexOf(cycle.charAt(j)) != -1) {
                    throw error("Can't add cycle with "
                            + "character already in other cycle!");
                }
            }
        }
        _condensed[_condensed.length] = cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char temp = _alphabet.toChar(wrap(p));
        for (int i = 0; i < _condensed.length; i++) {
            if (_condensed[i].indexOf(temp) != -1) {
                int x = _condensed[i].indexOf(temp);
                if (x == _condensed[i].length() - 1) {
                    return _alphabet.toInt(_condensed[i].charAt(0));
                } else {
                    return _alphabet.toInt(_condensed[i].charAt(x + 1));
                }
            }
        }
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char temp = _alphabet.toChar(c);
        for (int i = 0; i < _condensed.length; i++) {
            if (_condensed[i].indexOf(temp) != -1) {
                int x = _condensed[i].indexOf(temp);
                if (x == 0) {
                    return _alphabet.toInt(_condensed[i].charAt
                            (_condensed[i].length() - 1));
                } else {
                    return _alphabet.toInt(_condensed[i].charAt(x - 1));
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int x = _alphabet.toInt(p);
        x = permute(x);
        return _alphabet.toChar(x);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int x = _alphabet.toInt(c);
        x = invert(x);
        return _alphabet.toChar(x);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            int x = permute(i);
            if (_alphabet.toChar(i) == _alphabet.toChar(x)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** An array of all Permutation cycles. */
    private String[] _condensed;
}
