import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests Podcast. One normal and one bad-input or edge case per public
 * method, following spec Section 7.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class PodcastTest
{
    /** The em dash separator, as a char code to keep this file ASCII. */
    private static final String D = " " + (char) 8212 + " ";

    private final Podcast pilot = new Podcast("Pilot", "Serial", 1);

    /** Spec: "Pilot", "Serial", episode 1 creates a Podcast. */
    @Test
    public void testConstructorNormal()
    {
        assertEquals("Pilot", pilot.getTitle());
        assertEquals("Serial", pilot.getShowName());
        assertEquals(1, pilot.getEpisodeNumber());
    }

    /** Spec: episode 0 throws IAE; blank fields throw too. */
    @Test
    public void testConstructorBad()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> new Podcast("Pilot", "Serial", 0));
        assertEquals("Episode number must be at least 1.", e.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> new Podcast("", "Serial", 1));
        assertThrows(IllegalArgumentException.class,
            () -> new Podcast("Pilot", "  ", 1));
    }

    /** Spec: an episode of Serial returns "Serial". */
    @Test
    public void testGetShowNameNormal()
    {
        assertEquals("Serial", pilot.getShowName());
    }

    /** Spec: getShowName matches getCreator. */
    @Test
    public void testGetShowNameEdgeMatchesCreator()
    {
        assertEquals(pilot.getCreator(), pilot.getShowName());
    }

    /** Spec: episode 12 returns 12. */
    @Test
    public void testGetEpisodeNumberNormal()
    {
        assertEquals(12, new Podcast("X", "Serial", 12).getEpisodeNumber());
    }

    /** Spec: the lowest legal episode, 1, returns 1. */
    @Test
    public void testGetEpisodeNumberEdgeOne()
    {
        assertEquals(1, pilot.getEpisodeNumber());
    }

    /** A Podcast's type is "Podcast". */
    @Test
    public void testGetTypeNormal()
    {
        assertEquals("Podcast", pilot.getType());
    }

    /** The type ignores what the title says. */
    @Test
    public void testGetTypeEdge()
    {
        assertEquals("Podcast", new Podcast("Song", "Song", 1).getType());
    }

    /** Same title, show, and episode, ignoring case, are equal. */
    @Test
    public void testEqualsNormal()
    {
        assertTrue(pilot.equals(new Podcast("PILOT", "serial", 1)));
    }

    /** Spec: two episodes of one show with different numbers differ. */
    @Test
    public void testEqualsBad()
    {
        assertFalse(pilot.equals(new Podcast("Pilot", "Serial", 2)));
        assertFalse(pilot.equals(null));
        assertFalse(pilot.equals(new Song("Pilot", "Serial", "")));
    }

    /** Equal episodes share a hash code. */
    @Test
    public void testHashCodeNormal()
    {
        assertEquals(pilot.hashCode(),
            new Podcast("pilot", "SERIAL", 1).hashCode());
    }

    /** Different episodes of the same show are unequal. */
    @Test
    public void testHashCodeEdgeUnequal()
    {
        assertNotEquals(pilot, new Podcast("Pilot", "Serial", 2));
    }

    /** Spec: "[Podcast] Title (em dash) Show #ep". */
    @Test
    public void testToStringNormal()
    {
        assertEquals("[Podcast] Pilot" + D + "Serial #1", pilot.toString());
    }

    /** Values entered with spaces print trimmed. */
    @Test
    public void testToStringEdgeTrimmed()
    {
        assertEquals("[Podcast] Pilot" + D + "Serial #30",
            new Podcast("  Pilot", "Serial  ", 30).toString());
    }
}
