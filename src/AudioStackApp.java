import java.util.Scanner;

/**
 * Application entry point. Creates the Scanner, InputHelper, a Library
 * with a starting playlist, and the PlaylistMenu, then starts the menu.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class AudioStackApp
{
    /**
     * Starts AudioStack. No arguments are needed.
     *
     * @param args ignored
     */
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        InputHelper input = new InputHelper(scanner);
        Library library = new Library(new Playlist("My Playlist"));
        PlaylistMenu menu = new PlaylistMenu(library, input);
        System.out.println("Welcome to AudioStack.");
        menu.run();
    }
}
