package enigma;

/**import java.util.HashMap;*/
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Kevin Chen
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        if (numRotors > 1) {
            _numRotors = new Rotor[numRotors];
        } else {
            throw error("Must have at least 2 rotor slots.");
        }
        if (pawls >= 0 && pawls < numRotors) {
            _pawls = pawls;
        } else {
            throw error("Must have at least 0 pawls, "
                    + "but cannot have as many pawls as rotors.");
        }
        _allRotors = allRotors;
    }

        /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors.length;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor x : _allRotors) {
                if (x.name().equalsIgnoreCase(rotors[i])) {
                    _numRotors[i] = x;
                    break;
                }
            }
            if (_numRotors[i] == null) {
                throw error("Invalid rotor.");
            }
        }
        if (!_numRotors[0].reflecting()) {
            throw error("First rotor must be a reflector!");
        }
        int numMove = 0;
        for (Rotor x : _numRotors) {
            if (x instanceof MovingRotor) {
                numMove += 1;
            }
        }
        if (numMove > _pawls) {
            throw error("Not enough pawls for moving rotors!");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        boolean upper = setting == setting.toUpperCase();
        if (!upper || setting.length() != _numRotors.length - 1) {
            throw error("Input must be Rotor-1 upper-case letters");
        }
        char[] check = setting.toCharArray();
        for (char x : check) {
            if (!_alphabet.contains(x)) {
                throw error("Initial setting character not in alphabet.");
            }
        }
        for (int i = 1; i < setting.length() + 1; i++) {
            _numRotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceAll();
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        for (int i = _numRotors.length - 1; i >= 0; i--) {
            c = _numRotors[i].convertForward(c);
        }
        for (int i = 1; i < _numRotors.length; i++) {
            c = _numRotors[i].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return result;
    }


    /** Advances all rotors according to notch position. */
    void advanceAll() {
        boolean[] advance = new boolean[_numRotors.length];
        for (int i = _numRotors.length - _pawls + 1;
             i < _numRotors.length; i++) {
            if (_numRotors[i].atNotch()) {
                advance[i - 1] = true;
                advance[i] = true;
            }
        }
        advance[advance.length - 1] = true;
        for (int i = 0; i < advance.length; i++) {
            if (advance[i]) {
                _numRotors[i].advance();
            }
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Rotors in their positions. */
    private Rotor[] _numRotors;

    /** Number of pawls in the machine. */
    private int _pawls;

    /** Collection of all rotors from which to choose. */
    private Collection<Rotor> _allRotors;

    /** Permutation that occurs once in the beginning
     * and again at the end. */
    private Permutation _plugboard;
}
