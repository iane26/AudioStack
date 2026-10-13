import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests InputHelper with a Scanner built from a String, capturing what it
 * prints. One normal and one bad-input case per public method, following
 * spec Section 7.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class InputHelperTest
{
    private PrintStream originalOut;
    private ByteArrayOutputStream printed;

    /**
     * Captures System.out.
     */
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true));
    }

    /**
     * Restores System.out.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
    }

    /**
     * Builds an InputHelper that reads the given text.
     *
     * @param text the simulated keyboard input
     * @return the helper
     */
    private InputHelper helperFor(String text)
    {
        return new InputHelper(new Scanner(text));
    }

    /**
     * Counts how many times a phrase was printed.
     *
     * @param phrase the text to look for
     * @return the number of occurrences
     */
    private int timesPrinted(String phrase)
    {
        String out = printed.toString();
        int count = 0;
        int at = out.indexOf(phrase);
        while (at >= 0)
        {
            count++;
            at = out.indexOf(phrase, at + 1);
        }
        return count;
    }

    /** Spec: a valid Scanner creates the InputHelper. */
    @Test
    public void testConstructorNormal()
    {
        assertEquals("ok", helperFor("ok\n").readLine(""));
    }

    /** Spec: null throws IAE. */
    @Test
    public void testConstructorBadNull()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> new InputHelper(null));
        assertEquals("Scanner cannot be null.", e.getMessage());
    }

    /** Spec: the user types " hi " and gets "hi". */
    @Test
    public void testReadLineNormal()
    {
        assertEquals("hi", helperFor(" hi \n").readLine("Say: "));
        assertEquals(1, timesPrinted("Say: "));
    }

    /** Spec: no input left throws NoSuchElementException. */
    @Test
    public void testReadLineBadNoInput()
    {
        InputHelper helper = helperFor("");
        assertThrows(NoSuchElementException.class,
            () -> helper.readLine("Say: "));
    }

    /** Spec: the user types "7" and gets 7. */
    @Test
    public void testReadIntNormal()
    {
        assertEquals(7, helperFor("7\n").readInt("N: "));
        assertEquals(0, timesPrinted("Please enter a whole number."));
    }

    /** Spec: "abc" then "3" prints one error and returns 3; repeats. */
    @Test
    public void testReadIntBad()
    {
        assertEquals(3, helperFor("abc\n3\n").readInt("N: "));
        assertEquals(1, timesPrinted("Please enter a whole number."));
        assertEquals(4, helperFor("x\n\n2.5\n4\n").readInt("N: "));
        assertEquals(4, timesPrinted("Please enter a whole number."));
    }

    /** Spec: the user types "hello" and gets "hello". */
    @Test
    public void testReadNonBlankNormal()
    {
        assertEquals("hello", helperFor("hello\n").readNonBlank("T: "));
        assertEquals(0, timesPrinted("This cannot be blank."));
    }

    /** Spec: "", "  ", "ok" prints two errors and returns "ok". */
    @Test
    public void testReadNonBlankBad()
    {
        assertEquals("ok", helperFor("\n   \nok\n").readNonBlank("T: "));
        assertEquals(2, timesPrinted("This cannot be blank."));
    }
}
