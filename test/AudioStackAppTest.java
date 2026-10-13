import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests AudioStackApp.main by replacing System.in and System.out
 * (spec Section 7).
 *
 * @author AudioStack team
 * @version October 2026
 */
public class AudioStackAppTest
{
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream printed;

    /**
     * Captures System.out and remembers System.in.
     */
    @BeforeEach
    public void setUp()
    {
        originalIn = System.in;
        originalOut = System.out;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true));
    }

    /**
     * Restores System.in and System.out.
     */
    @AfterEach
    public void tearDown()
    {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Runs main with the given keyboard input.
     *
     * @param script the simulated keyboard input
     */
    private void runMain(String script)
    {
        System.setIn(new ByteArrayInputStream(script.getBytes()));
        AudioStackApp.main(new String[0]);
    }

    /** Spec: started with no arguments, the menu appears. */
    @Test
    public void testMainNormal()
    {
        runMain("0\n");
        String out = printed.toString();
        assertTrue(out.contains("Welcome to AudioStack."));
        assertTrue(out.contains("=== AudioStack: My Playlist (0 items) ==="));
        assertTrue(out.contains("Goodbye."));
    }

    /** Spec: input ends immediately; it exits without a stack trace. */
    @Test
    public void testMainEdgeNoInput()
    {
        assertDoesNotThrow(() -> runMain(""));
        assertTrue(printed.toString().contains("Goodbye."));
    }
}
