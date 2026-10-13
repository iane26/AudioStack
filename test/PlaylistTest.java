import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests Playlist, following spec Section 7: one normal and one bad-input
 * or edge case per public method, plus empty-playlist and
 * remove-something-not-there cases. A, B, C, and X are distinct items;
 * [A, B] means the playlist holds A then B.
 *
 * Because insertItem and removeItemAt rebuild the chain, they are also
 * checked at the first, middle, and last positions.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class PlaylistTest
{
    private static final String D = " " + (char) 8212 + " ";

    private Playlist playlist;
    private MediaItem a;
    private MediaItem b;
    private MediaItem c;
    private MediaItem x;

    /**
     * Creates an empty playlist and four distinct items.
     */
    @BeforeEach
    public void setUp()
    {
        playlist = new Playlist("Road Trip");
        a = new Song("Hey Jude", "The Beatles", "");
        b = new Podcast("Pilot", "Serial", 1);
        c = new Song("Halo", "Beyonce", "");
        x = new Song("Not Here", "Nobody", "");
    }

    /**
     * Adds each item in order.
     *
     * @param list the items to add
     */
    private void fill(MediaItem... list)
    {
        for (MediaItem item : list)
        {
            playlist.addItem(item);
        }
    }

    /**
     * Asserts that the playlist holds exactly these items in order.
     *
     * @param expected the expected items
     */
    private void assertContents(MediaItem... expected)
    {
        assertEquals(expected.length, playlist.size());
        for (int i = 0; i < expected.length; i++)
        {
            assertSame(expected[i], playlist.getItem(i + 1));
        }
    }

    /**
     * Runs code that must throw IllegalArgumentException.
     *
     * @param code the code to run
     * @return the exception's message
     */
    private String messageOf(Executable code)
    {
        return assertThrows(IllegalArgumentException.class, code)
            .getMessage();
    }

    /** Spec: "Road Trip" creates an empty playlist. */
    @Test
    public void testConstructorNormal()
    {
        assertEquals("Road Trip", playlist.getName());
        assertTrue(playlist.isEmpty());
    }

    /** Spec: a blank or null name throws IAE. */
    @Test
    public void testConstructorBad()
    {
        assertEquals("Playlist name cannot be blank.",
            messageOf(() -> new Playlist("   ")));
        assertEquals("Playlist name cannot be blank.",
            messageOf(() -> new Playlist(null)));
    }

    /** Spec: the playlist named Road Trip returns "Road Trip". */
    @Test
    public void testGetNameNormal()
    {
        assertEquals("Road Trip", playlist.getName());
    }

    /** A name entered with spaces is trimmed. */
    @Test
    public void testGetNameEdgeTrimmed()
    {
        assertEquals("Gym", new Playlist("  Gym ").getName());
    }

    /** Spec: [A, B] has size 2. */
    @Test
    public void testSizeNormal()
    {
        fill(a, b);
        assertEquals(2, playlist.size());
    }

    /** Spec: an empty playlist has size 0. */
    @Test
    public void testSizeEdgeEmpty()
    {
        assertEquals(0, playlist.size());
    }

    /** Spec: a new playlist is empty. */
    @Test
    public void testIsEmptyNormal()
    {
        assertTrue(playlist.isEmpty());
    }

    /** Spec: after addItem(A) it is not empty. */
    @Test
    public void testIsEmptyEdgeAfterAdd()
    {
        fill(a);
        assertFalse(playlist.isEmpty());
    }

    /** Spec: adding A makes the size 1; order is kept. */
    @Test
    public void testAddItemNormal()
    {
        fill(a, b, c);
        assertContents(a, b, c);
    }

    /** Spec: addItem(null) throws IAE and the playlist is unchanged. */
    @Test
    public void testAddItemBadNull()
    {
        fill(a);
        assertEquals("Item cannot be null.",
            messageOf(() -> playlist.addItem(null)));
        assertContents(a);
    }

    /** Spec: insert X at 1 in [A, B] gives [X, A, B]; size + 1 appends. */
    @Test
    public void testInsertItemNormal()
    {
        fill(a, b);
        playlist.insertItem(1, x);
        assertContents(x, a, b);
        playlist.insertItem(4, c);
        assertContents(x, a, b, c);
    }

    /** Insert in the middle and into an empty playlist. */
    @Test
    public void testInsertItemEdgeMiddleAndEmpty()
    {
        playlist.insertItem(1, a);
        playlist.insertItem(2, c);
        playlist.insertItem(2, b);
        assertContents(a, b, c);
    }

    /** Spec: positions 0 and 4 on [A, B] throw; null throws; unchanged. */
    @Test
    public void testInsertItemBad()
    {
        fill(a, b);
        assertEquals("Position must be between 1 and 3.",
            messageOf(() -> playlist.insertItem(0, x)));
        assertEquals("Position must be between 1 and 3.",
            messageOf(() -> playlist.insertItem(4, x)));
        assertEquals("Item cannot be null.",
            messageOf(() -> playlist.insertItem(1, null)));
        assertContents(a, b);
    }

    /** Spec: getItem(2) on [A, B, C] returns B. */
    @Test
    public void testGetItemNormal()
    {
        fill(a, b, c);
        assertSame(b, playlist.getItem(2));
    }

    /** Spec: getItem(0) throws; getItem(1) on an empty playlist throws. */
    @Test
    public void testGetItemBad()
    {
        assertEquals("Position must be between 1 and 0.",
            messageOf(() -> playlist.getItem(1)));
        fill(a, b, c);
        assertEquals("Position must be between 1 and 3.",
            messageOf(() -> playlist.getItem(0)));
        assertEquals("Position must be between 1 and 3.",
            messageOf(() -> playlist.getItem(4)));
    }

    /** Spec: removeItemAt(3) on [A, B, C] returns C, leaving [A, B]. */
    @Test
    public void testRemoveItemAtNormal()
    {
        fill(a, b, c);
        assertSame(c, playlist.removeItemAt(3));
        assertContents(a, b);
        playlist.addItem(x);
        assertContents(a, b, x);
    }

    /** Removing the first and a middle item keeps the rest in order. */
    @Test
    public void testRemoveItemAtEdgeFirstAndMiddle()
    {
        fill(a, b, c, x);
        assertSame(a, playlist.removeItemAt(1));
        assertSame(c, playlist.removeItemAt(2));
        assertContents(b, x);
    }

    /** Spec: on an empty playlist it throws; position 0 throws. */
    @Test
    public void testRemoveItemAtBad()
    {
        assertThrows(IllegalArgumentException.class,
            () -> playlist.removeItemAt(1));
        fill(a, b);
        assertEquals("Position must be between 1 and 2.",
            messageOf(() -> playlist.removeItemAt(0)));
        assertContents(a, b);
    }

    /** Spec: removeItem(A) on [A, B, A] returns true, leaving [B, A]. */
    @Test
    public void testRemoveItemNormal()
    {
        fill(a, b, a);
        assertTrue(playlist.removeItem(
            new Song("HEY JUDE", "the beatles", "")));
        assertContents(b, a);
    }

    /** Spec: X is not in the playlist, so removeItem returns false. */
    @Test
    public void testRemoveItemBadNotThere()
    {
        fill(a, b);
        assertFalse(playlist.removeItem(x));
        assertContents(a, b);
    }

    /** Spec: removeItem on an empty playlist returns false. */
    @Test
    public void testRemoveItemBadEmpty()
    {
        assertFalse(playlist.removeItem(a));
    }

    /** Spec: removeItem(null) throws IAE. */
    @Test
    public void testRemoveItemBadNull()
    {
        assertEquals("Item cannot be null.",
            messageOf(() -> playlist.removeItem(null)));
    }

    /** Spec: countItem(A) on [A, B, A] returns 2. */
    @Test
    public void testCountItemNormal()
    {
        fill(a, b, a);
        assertEquals(2, playlist.countItem(a));
    }

    /** Spec: X gives 0; an empty playlist gives 0; null throws. */
    @Test
    public void testCountItemBad()
    {
        assertEquals(0, playlist.countItem(a));
        fill(a);
        assertEquals(0, playlist.countItem(x));
        assertEquals("Item cannot be null.",
            messageOf(() -> playlist.countItem(null)));
    }

    /** Spec: [A, B] shows "1. [Song] ..." then "2. [Podcast] ...". */
    @Test
    public void testToDisplayStringNormal()
    {
        fill(a, b);
        assertEquals("1. [Song] Hey Jude" + D + "The Beatles\n"
            + "2. [Podcast] Pilot" + D + "Serial #1",
            playlist.toDisplayString());
    }

    /** Spec: an empty playlist shows "(empty)". */
    @Test
    public void testToDisplayStringEdgeEmpty()
    {
        assertEquals("(empty)", playlist.toDisplayString());
    }
}
