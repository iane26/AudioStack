import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests Library (stretch goal: multiple playlists). One normal and one
 * bad-input or edge case per public method.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class LibraryTest
{
    private Library library;
    private Playlist roadTrip;
    private Playlist gym;

    /**
     * Creates a library holding only "Road Trip".
     */
    @BeforeEach
    public void setUp()
    {
        roadTrip = new Playlist("Road Trip");
        gym = new Playlist("Gym");
        library = new Library(roadTrip);
    }

    /** The first playlist is current and the only one. */
    @Test
    public void testConstructorNormal()
    {
        assertSame(roadTrip, library.getCurrent());
        assertEquals(1, library.size());
    }

    /** A null first playlist throws. */
    @Test
    public void testConstructorBadNull()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> new Library(null));
        assertEquals("Playlist cannot be null.", e.getMessage());
    }

    /** getCurrent follows switchTo. */
    @Test
    public void testGetCurrentNormal()
    {
        library.addPlaylist(gym);
        library.switchTo(2);
        assertSame(gym, library.getCurrent());
    }

    /** Adding a playlist does not switch to it. */
    @Test
    public void testGetCurrentEdgeAddDoesNotSwitch()
    {
        library.addPlaylist(gym);
        assertSame(roadTrip, library.getCurrent());
    }

    /** size counts playlists. */
    @Test
    public void testSizeNormal()
    {
        library.addPlaylist(gym);
        assertEquals(2, library.size());
    }

    /** A rejected add leaves the size unchanged. */
    @Test
    public void testSizeEdgeRejectedAdd()
    {
        assertThrows(IllegalArgumentException.class,
            () -> library.addPlaylist(null));
        assertEquals(1, library.size());
    }

    /** addPlaylist appends to the end. */
    @Test
    public void testAddPlaylistNormal()
    {
        library.addPlaylist(gym);
        assertSame(gym, library.switchTo(2));
    }

    /** addPlaylist(null) throws. */
    @Test
    public void testAddPlaylistBadNull()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> library.addPlaylist(null));
        assertEquals("Playlist cannot be null.", e.getMessage());
    }

    /** switchTo changes and returns the current playlist. */
    @Test
    public void testSwitchToNormal()
    {
        library.addPlaylist(gym);
        assertSame(gym, library.switchTo(2));
        assertSame(roadTrip, library.switchTo(1));
    }

    /** A bad position throws and the current playlist is unchanged. */
    @Test
    public void testSwitchToBad()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> library.switchTo(2));
        assertEquals("Position must be between 1 and 1.", e.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> library.switchTo(0));
        assertSame(roadTrip, library.getCurrent());
    }

    /** The listing numbers playlists and marks the current one. */
    @Test
    public void testToDisplayStringNormal()
    {
        library.addPlaylist(gym);
        gym.addItem(new Song("Halo", "Beyonce", ""));
        library.switchTo(2);
        assertEquals("1. Road Trip (0 items)\n2. Gym (1 items) <- current",
            library.toDisplayString());
    }

    /** With one playlist, it is listed and marked current. */
    @Test
    public void testToDisplayStringEdgeOne()
    {
        assertEquals("1. Road Trip (0 items) <- current",
            library.toDisplayString());
    }
}
