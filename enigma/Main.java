package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Kevin Chen
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }


    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _machine = readConfig();
        String first = _input.nextLine();
        if (first.charAt(0) != '*') {
            throw error("Input file must start with settings.");
        }
        setUp(_machine, first);
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (next.length() == 0) {
                _output.write("\n".getBytes(), 0, "\n".getBytes().length);
            } else if (next.charAt(0) == '*') {
                setUp(_machine, next);
            } else {
                next = next.replaceAll("\\s+", "").toUpperCase();
                next = _machine.convert(next);
                next = convertLine(next);
                byte[] msgBytes = next.getBytes();
                _output.write(msgBytes, 0, msgBytes.length);
                _output.write("\n".getBytes(), 0, "\n".getBytes().length);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            if (_alphabet.contains(' ')) {
                throw error("Invalid or non-existent alphabet in config file.");
            }
            String numParts = _config.nextLine();
            char testParts = numParts.replaceAll("\\s+", "").charAt(0);
            if (testParts < '0' || testParts > '9') {
                throw error("Invalid setting for rotor quantity and pawls.");
            }
            numParts = numParts.trim();
            int indexWhite = numParts.indexOf(" ");
            int numRotors = Integer.parseInt(numParts.substring(0, indexWhite));
            int pawls = Integer.parseInt(numParts.substring(indexWhite + 1));
            ArrayList<Rotor> allRotor = new ArrayList<>();

            while (_config.hasNextLine()) {
                String next = _config.nextLine().trim();
                int typeIndex = next.indexOf(" ") + 1;
                if (next.charAt(typeIndex) == 'M') {
                    allRotor.add(readRotor(next.trim()));
                } else if (next.charAt(typeIndex) == 'N') {
                    allRotor.add(readRotor(next.trim()));
                } else {
                    String second = _config.nextLine();
                    second = second.substring(second.indexOf("("));
                    next = next.trim();
                    int whiteIndex = next.indexOf(" ");
                    String name = next.substring(0, whiteIndex);
                    next = next.substring(next.indexOf("("));
                    String perm = next + " " + second;
                    allRotor.add(new Reflector(name,
                            new Permutation(perm, _alphabet)));
                }
            }
            return new Machine(_alphabet, numRotors, pawls, allRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     *  Takes in a @param configLine that describes the
     *  settings of a Rotor. */
    private Rotor readRotor(String configLine) {
        try {
            int whiteIndex = configLine.indexOf(" ");
            String name = configLine.substring(0, whiteIndex);
            whiteIndex = whiteIndex + 1;
            int nextWhite = configLine.indexOf(" ", whiteIndex);
            if (configLine.charAt(whiteIndex) == 'M') {
                String notches = configLine.substring
                        (whiteIndex + 1, nextWhite);
                Permutation x =
                        new Permutation(configLine.substring
                                (configLine.indexOf("(")), _alphabet);
                return new MovingRotor(name, x, notches);
            } else {
                Permutation x =
                        new Permutation(configLine.substring
                                (configLine.indexOf("(")), _alphabet);
                return new FixedRotor(name, x);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        settings = settings.substring(2);
        String[] setting = settings.split(" ");
        String[] used = new String[M.numRotors()];
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < used.length; j++) {
                if (setting[i].equals(used[j])) {
                    throw error("Cannot reuse rotors!");
                }
            }
            rotors[i] = setting[i];
            used[i] = setting[i];
        }
        if (setting.length <= M.numRotors()) {
            throw error("Not enough arguments provided");
        }
        String rotorSetting = setting[M.numRotors()];
        if (setting.length - 1 > M.numRotors()) {
            String perm = "";
            for (int i = M.numRotors() + 1; i < setting.length; i++) {
                perm += setting[i];
                perm += " ";
            }
            M.setPlugboard(new Permutation(perm, _alphabet));
        }
        M.insertRotors(rotors);
        M.setRotors(rotorSetting);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters).
     *  @returns the converted msg in the proper format. */
    private String convertLine(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 5) {
            result += msg.substring(i, Math.min(i + 5, msg.length()));
            if (i + 5 < msg.length()) {
                result += " ";
            }
        }
        return result;
    }

    /** The German enigma machine. */
    private Machine _machine;

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
