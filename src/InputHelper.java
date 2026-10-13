import java.util.Scanner;

/**
 * Owns keyboard input format. Every read uses nextLine(), and numbers are
 * converted with Integer.parseInt, so a bad token or a leftover newline is
 * never stuck in the Scanner's buffer. readInt and readNonBlank re-prompt
 * until the input is well-formed, then return a clean value.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class InputHelper
{
    private final Scanner in;

    /**
     * Creates an input helper that reads from the given Scanner.
     *
     * @param in the Scanner to read from; must not be null
     * @throws IllegalArgumentException if in is null
     */
    public InputHelper(Scanner in)
    {
        if (in == null)
        {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        this.in = in;
    }

    /**
     * Prints the prompt and returns the next line, trimmed.
     *
     * @param prompt the text to show before reading
     * @return the trimmed line, possibly ""
     * @throws java.util.NoSuchElementException if there is no more input
     */
    public String readLine(String prompt)
    {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    /**
     * Reads a whole number, printing "Please enter a whole number." and
     * asking again until the line parses as an int. Negative numbers are
     * accepted; range rules belong to Playlist.
     *
     * @param prompt the text to show before each attempt
     * @return the number entered
     * @throws java.util.NoSuchElementException if input ends first
     */
    public int readInt(String prompt)
    {
        while (true)
        {
            String line = readLine(prompt);
            try
            {
                return Integer.parseInt(line);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads non-blank text, printing "This cannot be blank." and asking
     * again until the trimmed line is not empty.
     *
     * @param prompt the text to show before each attempt
     * @return the trimmed, non-blank text
     * @throws java.util.NoSuchElementException if input ends first
     */
    public String readNonBlank(String prompt)
    {
        while (true)
        {
            String line = readLine(prompt);
            if (!line.isEmpty())
            {
                return line;
            }
            System.out.println("This cannot be blank.");
        }
    }
}
