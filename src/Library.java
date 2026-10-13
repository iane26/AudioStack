/**
 * Holds every playlist the user has made and tracks which one is being
 * edited. (Stretch goal from our scope: multiple playlists.)
 *
 * The playlists live in a LinkedChain&lt;Playlist&gt;: the same provided
 * generic class Playlist uses for its items, reached only through its
 * public methods. A library always has at least one playlist.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class Library
{
    private final LinkedChain<Playlist> playlists;
    private Playlist current;

    /**
     * Creates a library whose first playlist is also the current one.
     *
     * @param first the starting playlist; must not be null
     * @throws IllegalArgumentException if first is null
     */
    public Library(Playlist first)
    {
        requirePlaylist(first);
        playlists = new LinkedChain<Playlist>();
        playlists.addToEnd(first);
        current = first;
    }

    /**
     * Gets the playlist being edited.
     *
     * @return the current playlist, never null
     */
    public Playlist getCurrent()
    {
        return current;
    }

    /**
     * Gets the number of playlists.
     *
     * @return the number of playlists, always 1 or more
     */
    public int size()
    {
        return playlists.size();
    }

    /**
     * Adds a playlist to the end of the library. Does not switch to it.
     *
     * @param playlist the playlist to add; must not be null
     * @throws IllegalArgumentException if playlist is null
     */
    public void addPlaylist(Playlist playlist)
    {
        requirePlaylist(playlist);
        playlists.addToEnd(playlist);
    }

    /**
     * Makes the playlist at a 1-based position the current one.
     *
     * @param position the position, from 1 to size()
     * @return the new current playlist
     * @throws IllegalArgumentException if the position is out of range;
     *         the current playlist is unchanged
     */
    public Playlist switchTo(int position)
    {
        if (position < 1 || position > playlists.size())
        {
            throw new IllegalArgumentException(
                "Position must be between 1 and " + playlists.size() + ".");
        }
        current = playlists.get(position - 1);
        return current;
    }

    /**
     * Builds a numbered listing of playlists with their sizes, marking the
     * current one, such as "1. Road Trip (2 items) <- current".
     *
     * @return the listing, one playlist per line
     */
    public String toDisplayString()
    {
        Playlist[] all = playlists.toArray(new Playlist[playlists.size()]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.length; i++)
        {
            if (i > 0)
            {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(all[i].getName())
                .append(" (").append(all[i].size()).append(" items)");
            if (all[i] == current)
            {
                sb.append(" <- current");
            }
        }
        return sb.toString();
    }

    /**
     * Rejects a null playlist.
     *
     * @param playlist the playlist to check
     * @throws IllegalArgumentException if playlist is null
     */
    private void requirePlaylist(Playlist playlist)
    {
        if (playlist == null)
        {
            throw new IllegalArgumentException("Playlist cannot be null.");
        }
    }
}
