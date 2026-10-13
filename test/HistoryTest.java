import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests History. One normal and one bad-input or edge case per public
 * method.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class HistoryTest
{
    private History history;

    /**
     * Creates an empty history.
     */
    @BeforeEach
    public void setUp()
    {
        history = new History();
    }

    /** A new history can record entries. */
    @Test
    public void testConstructorNormal()
    {
        history.record("first");
        assertEquals("1. first", history.toDisplayString());
    }

    /** A new history has no activity. */
    @Test
    public void testConstructorEdgeEmpty()
    {
        assertEquals("(no activity yet)", history.toDisplayString());
    }

    /** record trims each entry and keeps the newest first. */
    @Test
    public void testRecordNormal()
    {
        history.record("first");
        history.record("  second ");
        assertEquals("1. second\n2. first", history.toDisplayString());
    }

    /** A blank or null action throws and is not recorded. */
    @Test
    public void testRecordBad()
    {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> history.record("  "));
        assertEquals("Action cannot be blank.", e.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> history.record(null));
        assertEquals("(no activity yet)", history.toDisplayString());
    }

    /** toDisplayString numbers every entry, newest first. */
    @Test
    public void testToDisplayStringNormal()
    {
        history.record("a");
        history.record("b");
        history.record("c");
        assertEquals("1. c\n2. b\n3. a", history.toDisplayString());
    }

    /** An empty history shows "(no activity yet)". */
    @Test
    public void testToDisplayStringEdgeEmpty()
    {
        assertEquals("(no activity yet)", history.toDisplayString());
    }
}
