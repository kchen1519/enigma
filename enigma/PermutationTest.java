package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Kevin Chen
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testPermuteFirst() {
        perm = new Permutation("(AHDC) (POL) (KQW)", UPPER);
        assertEquals(perm.permute(0), 7);
        assertEquals(perm.permute(3), 2);
        assertEquals(perm.permute(2), 0);
    }

    @Test
    public void testPermuteSecond() {
        perm = new Permutation("(AHDC) (POL) (KQW)", UPPER);
        assertEquals(perm.permute('A'), 'H');
        assertEquals(perm.permute('D'), 'C');
        assertEquals(perm.permute('C'), 'A');
    }

    @Test
    public void testInvertFirst() {
        perm = new Permutation("(AHDC) (POL) (KQW)", UPPER);
        assertEquals(perm.invert(7), 0);
        assertEquals(perm.invert(2), 3);
        assertEquals(perm.invert(0), 2);
    }

    @Test
    public void testInvertSecond() {
        perm = new Permutation("(AHDC) (POL) (KQW)", UPPER);
        assertEquals(perm.invert('H'), 'A');
        assertEquals(perm.invert('C'), 'D');
        assertEquals(perm.invert('A'), 'C');
    }

    @Test
    public void testDerangement() {
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER);
        assertEquals(perm.derangement(), true);
        perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXY)", UPPER);
        assertEquals(perm.derangement(), false);
        perm = new Permutation("(ABCDEFGHIJK) (LMNOPQRSTUVWXY)", UPPER);
        assertEquals(perm.derangement(), false);
        perm = new Permutation("(ABCDEFGHIJK) (LMNOPQRSTUVWXYZ)", UPPER);
        assertEquals(perm.derangement(), true);
    }
}
