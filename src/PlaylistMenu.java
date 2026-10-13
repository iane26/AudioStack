import java.util.NoSuchElementException;

/**
 * Handles the user interaction. Shows the menu, builds Song and Podcast
 * objects from input, calls Playlist and Library methods, and prints
 * results and error messages. It never touches a LinkedChain directly.
 *
 * Every action runs inside a try/catch for IllegalArgumentException, so a
 * rule violation prints its message and the menu keeps going. End of
 * input prints "Goodbye." and ends the session without a stack trace.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class PlaylistMenu
{
    private final Library library;
    private final InputHelper input;
    private final History history;

    /**
     * Creates a menu for the given library.
     *
     * @param library the playlists to edit; must not be null
     * @param input   the source of every user value; must not be null
     * @throws IllegalArgumentException if either argument is null
     */
    public PlaylistMenu(Library library, InputHelper input)
    {
        if (library == null || input == null)
        {
            throw new IllegalArgumentException(
                "Library and input cannot be null.");
        }
        this.library = library;
        this.input = input;
        this.history = new History();
    }

    /**
     * Runs the menu loop until the user quits or input ends.
     */
    public void run()
    {
        try
        {
            boolean running = true;
            while (running)
            {
                printMenu();
                int choice = input.readInt("Choose an option: ");
                try
                {
                    running = handle(choice);
                }
                catch (IllegalArgumentException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
        catch (NoSuchElementException e)
        {
            System.out.println(); // end the unfinished prompt line
        }
        System.out.println("Goodbye.");
    }

    /**
     * Carries out one menu choice.
     *
     * @param choice the option number the user entered
     * @return false if the user chose to quit, true otherwise
     */
    private boolean handle(int choice)
    {
        Playlist playlist = library.getCurrent();
        switch (choice)
        {
            case 0:
                return false;
            case 1:
                MediaItem added = readMediaItem();
                playlist.addItem(added);
                System.out.println("Added: " + added);
                history.record("Added " + added + " to "
                    + playlist.getName());
                break;
            case 2:
                int at = input.readInt(
                    "Position (1-" + (playlist.size() + 1) + "): ");
                MediaItem inserted = readMediaItem();
                playlist.insertItem(at, inserted);
                System.out.println("Inserted at " + at + ": " + inserted);
                history.record("Inserted " + inserted + " at " + at
                    + " in " + playlist.getName());
                break;
            case 3:
                System.out.println(playlist.getName() + ":");
                System.out.println(playlist.toDisplayString());
                break;
            case 4:
                if (playlist.isEmpty())
                {
                    System.out.println("The playlist is empty.");
                    break;
                }
                int view = input.readInt(
                    "Position (1-" + playlist.size() + "): ");
                System.out.println(view + ". " + playlist.getItem(view));
                break;
            case 5:
                if (playlist.isEmpty())
                {
                    System.out.println("Nothing to remove.");
                    break;
                }
                int pos = input.readInt(
                    "Position (1-" + playlist.size() + "): ");
                MediaItem removed = playlist.removeItemAt(pos);
                System.out.println("Removed: " + removed);
                history.record("Removed " + removed + " from "
                    + playlist.getName());
                break;
            case 6:
                if (playlist.isEmpty())
                {
                    System.out.println("Nothing to remove.");
                    break;
                }
                MediaItem target = readMediaItem();
                if (playlist.removeItem(target))
                {
                    System.out.println("Removed: " + target);
                    history.record("Removed " + target + " from "
                        + playlist.getName());
                }
                else
                {
                    System.out.println("Not in the playlist.");
                }
                break;
            case 7:
                MediaItem counted = readMediaItem();
                System.out.println("Count: " + playlist.countItem(counted));
                break;
            case 8:
                Playlist created = new Playlist(
                    input.readNonBlank("New playlist name: "));
                library.addPlaylist(created);
                library.switchTo(library.size());
                System.out.println("Created and switched to "
                    + created.getName() + ".");
                history.record("Created playlist " + created.getName());
                break;
            case 9:
                System.out.println(library.toDisplayString());
                Playlist chosen = library.switchTo(input.readInt(
                    "Switch to (1-" + library.size() + "): "));
                System.out.println("Now editing " + chosen.getName() + ".");
                history.record("Switched to playlist " + chosen.getName());
                break;
            case 10:
                System.out.println("Recent activity:");
                System.out.println(history.toDisplayString());
                break;
            default:
                System.out.println("Unknown option.");
                break;
        }
        return true;
    }

    /**
     * Prints the current playlist's name and the list of options.
     */
    private void printMenu()
    {
        Playlist playlist = library.getCurrent();
        System.out.println();
        System.out.println("=== AudioStack: " + playlist.getName() + " ("
            + playlist.size() + " items) ===");
        System.out.println(" 1. Add a song or podcast");
        System.out.println(" 2. Insert at a position");
        System.out.println(" 3. View the playlist");
        System.out.println(" 4. View one item");
        System.out.println(" 5. Remove by position");
        System.out.println(" 6. Remove an item");
        System.out.println(" 7. Count an item");
        System.out.println(" 8. New playlist");
        System.out.println(" 9. Switch playlist");
        System.out.println("10. Recent activity");
        System.out.println(" 0. Quit");
    }

    /**
     * Asks for a type, then reads the fields for that type and builds the
     * item. Re-prompts until the type is 1 or 2. Blank text re-prompts in
     * InputHelper; an episode number below 1 makes the Podcast constructor
     * throw, which run() prints.
     *
     * @return the new Song or Podcast
     */
    private MediaItem readMediaItem()
    {
        int type = input.readInt("Type (1 = Song, 2 = Podcast): ");
        while (type != 1 && type != 2)
        {
            System.out.println("Please enter 1 or 2.");
            type = input.readInt("Type (1 = Song, 2 = Podcast): ");
        }
        String title = input.readNonBlank("Title: ");
        if (type == 1)
        {
            String artist = input.readNonBlank("Artist: ");
            String album = input.readLine("Album (optional, Enter to skip): ");
            return new Song(title, artist, album);
        }
        String show = input.readNonBlank("Show: ");
        int episode = input.readInt("Episode number: ");
        return new Podcast(title, show, episode);
    }
}
