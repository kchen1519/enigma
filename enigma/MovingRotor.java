package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Kevin Chen
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new int[notches.length()];
        char[] temp = notches.toCharArray();
        for (int i = 0; i < temp.length; i++) {
            _notches[i] = alphabet().toInt(temp[i]);
        }
    }

    @Override
    boolean atNotch() {
        for (int x : _notches) {
            if (setting() == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set((setting() + 1) % alphabet().size());
    }

    /** The locations of the notches on the rotor. */
    private int[] _notches;
}
