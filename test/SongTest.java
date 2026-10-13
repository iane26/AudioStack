import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests Song and the MediaItem methods it inherits (MediaItem is
 * abstract, so it is tested through Song). One normal and one bad-input
 * or edge case per public method, following spec Section 7.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class SongTest
{
    /** The em dash separator, as a char code to keep this file ASCII. */
    private static final String D = " " + (char) 8212 + " ";

    private final Song heyJude =
        new Song("Hey Jude", "The Beatles", "Abbey Road");

    /** Spec: " Hey Jude ", " The Beatles " are stored trimmed. */
    @Test
    public void testConstructorNormal()
    {
        Song s = new Song(" Hey Jude ", " The Beatles ", "Abbey Road");
        assertEquals("Hey Jude", s.getTitle());
        assertEquals("The Beatles", s.getArtist());
    }

    /** Spec: a blank title or artist throws IAE. */
    @Test
    public void testConstructorBad()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> new Song("   ", "The Beatles", ""));
        assertEquals("Title cannot be blank.", e.getMessage());
        e = assertThrows(IllegalArgumentException.class,
            () -> new Song("Hey Jude", "", ""));
        assertEquals("Creator cannot be blank.", e.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> new Song(null, "The Beatles", ""));
        assertThrows(IllegalArgumentException.class,
            () -> new Song("Hey Jude", null, ""));
    }

    /** Spec: a Song titled Hey Jude returns "Hey Jude". */
    @Test
    public void testGetTitleNormal()
    {
        assertEquals("Hey Jude", heyJude.getTitle());
    }

    /** Spec: a title entered with spaces comes back trimmed. */
    @Test
    public void testGetTitleEdgeTrimmed()
    {
        assertEquals("Hey Jude",
            new Song("  Hey Jude  ", "X", "").getTitle());
    }

    /** Spec: a Song by The Beatles returns "The Beatles". */
    @Test
    public void testGetCreatorNormal()
    {
        assertEquals("The Beatles", heyJude.getCreator());
    }

    /** Spec: a creator entered with spaces comes back trimmed. */
    @Test
    public void testGetCreatorEdgeTrimmed()
    {
        assertEquals("The Beatles",
            new Song("X", "  The Beatles ", "").getCreator());
    }

    /** Spec: a Song returns "Song". */
    @Test
    public void testGetTypeNormal()
    {
        assertEquals("Song", heyJude.getType());
    }

    /** Spec: a Podcast returns "Podcast" (the other subclass). */
    @Test
    public void testGetTypeEdgePodcast()
    {
        assertEquals("Podcast", new Podcast("Pilot", "Serial", 1).getType());
    }

    /** Spec: a Song by The Beatles returns "The Beatles". */
    @Test
    public void testGetArtistNormal()
    {
        assertEquals("The Beatles", heyJude.getArtist());
    }

    /** Spec: getArtist matches getCreator. */
    @Test
    public void testGetArtistEdgeMatchesCreator()
    {
        assertEquals(heyJude.getCreator(), heyJude.getArtist());
    }

    /** Spec: a Song on Abbey Road returns "Abbey Road". */
    @Test
    public void testGetAlbumNormal()
    {
        assertEquals("Abbey Road", heyJude.getAlbum());
    }

    /** Spec: a null album is stored as "". */
    @Test
    public void testGetAlbumBadNull()
    {
        assertEquals("",
            new Song("Hey Jude", "The Beatles", null).getAlbum());
    }

    /** Spec: "Hey jude" and "Hey Jude" by the same artist are equal. */
    @Test
    public void testEqualsNormalIgnoresCase()
    {
        assertTrue(heyJude.equals(new Song("Hey jude", "the beatles", "")));
    }

    /** Spec: null and a Podcast with the same title are not equal. */
    @Test
    public void testEqualsBad()
    {
        assertFalse(heyJude.equals(null));
        assertFalse(heyJude.equals(
            new Podcast("Hey Jude", "The Beatles", 1)));
        assertFalse(heyJude.equals(new Song("Let It Be", "The Beatles", "")));
    }

    /** Spec: equal items differing only in case share a hash code. */
    @Test
    public void testHashCodeNormal()
    {
        assertEquals(heyJude.hashCode(),
            new Song("HEY JUDE", "the beatles", "").hashCode());
    }

    /** Spec: two unequal items are not equal. */
    @Test
    public void testHashCodeEdgeUnequal()
    {
        assertNotEquals(heyJude, new Song("Let It Be", "The Beatles", ""));
    }

    /** Spec: "[Song] Hey Jude (em dash) The Beatles". */
    @Test
    public void testToStringNormal()
    {
        assertEquals("[Song] Hey Jude" + D + "The Beatles",
            heyJude.toString());
    }

    /** Spec: a Song with an empty album still formats correctly. */
    @Test
    public void testToStringEdgeEmptyAlbum()
    {
        assertEquals("[Song] Hey Jude" + D + "The Beatles",
            new Song("Hey Jude", "The Beatles", null).toString());
    }
}
