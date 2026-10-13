import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests PlaylistMenu by running it on scripted input (spec Section 7) and
 * checking what it prints and what ends up in the library. Each script
 * line is one thing the user types.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class PlaylistMenuTest
{
    private static final String D = " " + (char) 8212 + " ";
    /** Keystrokes that add the song "Hey Jude" by The Beatles. */
    private static final String ADD_HEY_JUDE =
        "1\n1\nHey Jude\nThe Beatles\n\n";
    /** Keystrokes that add the podcast "Pilot", Serial #1. */
    private static final String ADD_PILOT = "1\n2\nPilot\nSerial\n1\n";

    private PrintStream originalOut;
    private ByteArrayOutputStream printed;
    private Library library;
    private Playlist roadTrip;

    /**
     * Captures System.out and creates a library holding "Road Trip".
     */
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true));
        roadTrip = new Playlist("Road Trip");
        library = new Library(roadTrip);
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
     * Runs the menu on a script and returns everything it printed.
     *
     * @param script the simulated keyboard input
     * @return the printed output
     */
    private String run(String script)
    {
        new PlaylistMenu(library, new InputHelper(new Scanner(script)))
            .run();
        return printed.toString();
    }

    /**
     * Counts how many times a phrase appears in the output.
     *
     * @param out    the output
     * @param phrase the text to look for
     * @return the number of occurrences
     */
    private int count(String out, String phrase)
    {
        int n = 0;
        int at = out.indexOf(phrase);
        while (at >= 0)
        {
            n++;
            at = out.indexOf(phrase, at + 1);
        }
        return n;
    }

    /** Spec: valid objects create the menu. */
    @Test
    public void testConstructorNormal()
    {
        assertTrue(run("0\n").contains("=== AudioStack: Road Trip"));
    }

    /** Spec: either argument null throws IAE. */
    @Test
    public void testConstructorBadNull()
    {
        InputHelper input = new InputHelper(new Scanner(""));
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> new PlaylistMenu(null, input));
        assertEquals("Library and input cannot be null.", e.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> new PlaylistMenu(library, null));
    }

    /** Spec: add an item, view all, quit; the item is listed. */
    @Test
    public void testRunNormalMvpPath()
    {
        String out = run(ADD_HEY_JUDE + ADD_PILOT + "3\n0\n");
        assertTrue(out.contains("1. [Song] Hey Jude" + D + "The Beatles\n"
            + "2. [Podcast] Pilot" + D + "Serial #1"));
        assertTrue(out.contains("Goodbye."));
        assertEquals(2, roadTrip.size());
    }

    /**
     * Spec: every keyboard bad input from the scope in one session. Each
     * message prints once per bad input, nothing crashes, and rejected
     * actions leave the playlist unchanged.
     */
    @Test
    public void testRunBadInputsFromScope()
    {
        String out = run(
            "x\n"                                  // non-number option
            + "99\n"                               // unknown option
            + "5\n"                                // remove at, empty
            + "6\n"                                // remove item, empty
            + "1\n2\nPilot\nSerial\n0\n"           // episode 0
            + "1\n1\n\nHey Jude\nThe Beatles\n\n"  // blank title, then ok
            + "2\n99\n1\nX\nY\n\n"                 // insert past the end
            + "2\n-1\n1\nX\nY\n\n"                 // insert below 1
            + "4\n99\n"                            // view past the end
            + "4\n0\n"                             // view position 0
            + "5\n7\n"                             // remove past the end
            + "6\n1\nNope\nNobody\n\n"             // remove item not there
            + "0\n");
        assertEquals(1, count(out, "Please enter a whole number."));
        assertEquals(1, count(out, "Unknown option."));
        assertEquals(2, count(out, "Nothing to remove."));
        assertEquals(1, count(out, "Episode number must be at least 1."));
        assertEquals(1, count(out, "This cannot be blank."));
        assertEquals(2, count(out, "Position must be between 1 and 2."));
        assertEquals(3, count(out, "Position must be between 1 and 1."));
        assertEquals(1, count(out, "Not in the playlist."));
        assertTrue(out.contains("Goodbye."));
        assertEquals(1, roadTrip.size());
    }

    /** Spec: end of input prints "Goodbye." and exits. */
    @Test
    public void testRunBadEndOfInput()
    {
        String out = run("1\n1\nHey Jude\n");
        assertTrue(out.contains("Goodbye."));
        assertTrue(roadTrip.isEmpty());
    }

    /** Option 2 inserts at a position. */
    @Test
    public void testRunInsert()
    {
        run(ADD_HEY_JUDE + "2\n1\n1\nHalo\nBeyonce\n\n0\n");
        assertEquals("Halo", roadTrip.getItem(1).getTitle());
    }

    /** Option 4 shows one item; on an empty playlist it says so. */
    @Test
    public void testRunViewAt()
    {
        String out = run("4\n" + ADD_HEY_JUDE + "4\n1\n0\n");
        assertEquals(1, count(out, "The playlist is empty."));
        assertTrue(out.contains("1. [Song] Hey Jude" + D + "The Beatles"));
    }

    /** Option 5 removes by position. */
    @Test
    public void testRunRemoveAt()
    {
        run(ADD_HEY_JUDE + ADD_PILOT + "5\n1\n0\n");
        assertEquals("Pilot", roadTrip.getItem(1).getTitle());
    }

    /** Option 6 removes a matching item, ignoring case. */
    @Test
    public void testRunRemoveItem()
    {
        run(ADD_HEY_JUDE + "6\n1\nhey jude\nTHE BEATLES\n\n0\n");
        assertTrue(roadTrip.isEmpty());
    }

    /** Option 7 counts matches, including 0. */
    @Test
    public void testRunCount()
    {
        String out = run(ADD_HEY_JUDE + ADD_HEY_JUDE
            + "7\n1\nHey Jude\nThe Beatles\n\n"
            + "7\n2\nPilot\nSerial\n1\n0\n");
        assertTrue(out.contains("Count: 2"));
        assertTrue(out.contains("Count: 0"));
    }

    /** Options 8 and 9 create, switch, and reject a bad position. */
    @Test
    public void testRunNewAndSwitchPlaylist()
    {
        String out = run("8\nGym\n9\n1\n9\n5\n0\n");
        assertEquals(2, library.size());
        assertSame(roadTrip, library.getCurrent());
        assertTrue(out.contains("Created and switched to Gym."));
        assertTrue(out.contains("Now editing Road Trip."));
        assertTrue(out.contains("Position must be between 1 and 2."));
    }

    /** Option 10 shows activity newest first, or none yet. */
    @Test
    public void testRunHistory()
    {
        String out = run("10\n" + ADD_HEY_JUDE + "8\nGym\n10\n0\n");
        assertTrue(out.contains("(no activity yet)"));
        assertTrue(out.contains("1. Created playlist Gym\n2. Added [Song] "
            + "Hey Jude" + D + "The Beatles to Road Trip"));
    }

    /** A type other than 1 or 2 re-prompts. */
    @Test
    public void testRunBadMediaType()
    {
        String out = run("1\n3\n1\nHey Jude\nThe Beatles\n\n0\n");
        assertEquals(1, count(out, "Please enter 1 or 2."));
        assertEquals(1, roadTrip.size());
    }
}
