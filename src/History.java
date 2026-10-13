/**
 * A log of what the user has done this session, newest first.
 *
 * The entries live in a LinkedChain&lt;String&gt;, the third element type
 * stored in the same provided generic class (after MediaItem and
 * Playlist). History uses the chain's add(), which puts each entry at the
 * front in O(1), so the newest entry is always first.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class History
{
    private final LinkedChain<String> entries;

    /**
     * Creates an empty history.
     */
    public History()
    {
        entries = new LinkedChain<String>();
    }

    /**
     * Records an action as the newest entry. O(1).
     *
     * @param action what happened; must not be null or blank
     * @throws IllegalArgumentException if action is null or blank
     */
    public void record(String action)
    {
        if (action == null || action.trim().isEmpty())
        {
            throw new IllegalArgumentException("Action cannot be blank.");
        }
        entries.add(action.trim());
    }

    /**
     * Lists every recorded action, newest first, one per line, such as
     * "1. Created playlist Gym".
     *
     * @return the listing, or "(no activity yet)" if nothing is recorded
     */
    public String toDisplayString()
    {
        if (entries.isEmpty())
        {
            return "(no activity yet)";
        }
        String[] all = entries.toArray(new String[entries.size()]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.length; i++)
        {
            if (i > 0)
            {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(all[i]);
        }
        return sb.toString();
    }
}
