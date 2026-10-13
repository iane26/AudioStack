/**
 * One named, ordered playlist of songs and podcast episodes. Playlist owns
 * the playlist rules: it uses 1-based positions and converts them to the
 * chain's 0-based indexes, range-checks positions, rejects null items,
 * counts matches, and builds the numbered display.
 *
 * The items live in the provided LinkedChain&lt;MediaItem&gt;, used
 * unchanged and reached only through its public methods. Duplicates are
 * allowed and order is exactly what the user chose.
 *
 * The provided chain has no insert-at-index or remove-at-index method, so
 * insertItem and removeItemAt copy the items out with toArray(), clear the
 * chain, and refill it with the provided add(), which puts each entry at
 * the front in O(1). Refilling from the last item back to the first keeps
 * the original order, so the whole operation stays O(n).
 *
 * @author AudioStack team
 * @version October 2026
 */
public class Playlist
{
    private final String name;
    private final LinkedChain<MediaItem> items;

    /**
     * Creates an empty playlist.
     *
     * @param name the playlist name; must not be null or blank
     * @throws IllegalArgumentException if name is null or blank
     */
    public Playlist(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Playlist name cannot be blank.");
        }
        this.name = name.trim();
        this.items = new LinkedChain<MediaItem>();
    }

    /**
     * Gets the playlist name.
     *
     * @return the trimmed name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the number of items.
     *
     * @return the number of items
     */
    public int size()
    {
        return items.size();
    }

    /**
     * Checks whether the playlist has no items.
     *
     * @return true if there are no items
     */
    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    /**
     * Appends an item to the end of the playlist.
     *
     * @param item the item to add; must not be null
     * @throws IllegalArgumentException if item is null
     */
    public void addItem(MediaItem item)
    {
        requireItem(item);
        items.addToEnd(item);
    }

    /**
     * Inserts an item at a 1-based position. Position size() + 1 appends.
     *
     * @param position the position, from 1 to size() + 1
     * @param item     the item to insert; must not be null
     * @throws IllegalArgumentException if item is null or the position is
     *         out of range; the playlist is unchanged
     */
    public void insertItem(int position, MediaItem item)
    {
        requireItem(item);
        checkPosition(position, items.size() + 1);
        if (position == 1)
        {
            items.add(item); // provided add() puts it at the front
            return;
        }
        MediaItem[] old = toArray();
        MediaItem[] result = new MediaItem[old.length + 1];
        for (int i = 0; i < result.length; i++)
        {
            if (i < position - 1)
            {
                result[i] = old[i];
            }
            else if (i == position - 1)
            {
                result[i] = item;
            }
            else
            {
                result[i] = old[i - 1];
            }
        }
        refill(result);
    }

    /**
     * Gets the item at a 1-based position.
     *
     * @param position the position, from 1 to size()
     * @return the item at that position
     * @throws IllegalArgumentException if the position is out of range,
     *         including any position on an empty playlist
     */
    public MediaItem getItem(int position)
    {
        checkPosition(position, items.size());
        return items.get(position - 1);
    }

    /**
     * Removes and returns the item at a 1-based position.
     *
     * @param position the position, from 1 to size()
     * @return the removed item
     * @throws IllegalArgumentException if the position is out of range,
     *         including any position on an empty playlist; the playlist is
     *         unchanged
     */
    public MediaItem removeItemAt(int position)
    {
        checkPosition(position, items.size());
        if (position == 1)
        {
            return items.remove(); // provided remove() takes the front
        }
        MediaItem[] old = toArray();
        MediaItem removed = old[position - 1];
        MediaItem[] result = new MediaItem[old.length - 1];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (i < position - 1) ? old[i] : old[i + 1];
        }
        refill(result);
        return removed;
    }

    /**
     * Removes the first item equal to the given one.
     *
     * @param item the item to match; must not be null
     * @return true if an item was removed, false if none matched
     * @throws IllegalArgumentException if item is null
     */
    public boolean removeItem(MediaItem item)
    {
        requireItem(item);
        return items.remove(item);
    }

    /**
     * Counts how many items equal the given one.
     *
     * @param item the item to match; must not be null
     * @return the number of matches, 0 if none
     * @throws IllegalArgumentException if item is null
     */
    public int countItem(MediaItem item)
    {
        requireItem(item);
        return items.count(item);
    }

    /**
     * Builds a numbered listing, one item per line, in one pass over
     * toArray() instead of calling getItem() n times (which would be
     * O(n^2)).
     *
     * @return the listing, or "(empty)" if there are no items
     */
    public String toDisplayString()
    {
        if (items.isEmpty())
        {
            return "(empty)";
        }
        MediaItem[] all = toArray();
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

    /**
     * Copies the items out of the chain in order.
     *
     * @return a new array of the items
     */
    private MediaItem[] toArray()
    {
        return items.toArray(new MediaItem[items.size()]);
    }

    /**
     * Replaces the chain's contents with the given items, in order.
     * Adds from the last item to the first with the provided add(), which
     * puts each one at the front in O(1), so this is O(n).
     *
     * @param newOrder the items in the order they should end up
     */
    private void refill(MediaItem[] newOrder)
    {
        items.clear();
        for (int i = newOrder.length - 1; i >= 0; i--)
        {
            items.add(newOrder[i]);
        }
    }

    /**
     * Rejects a null item.
     *
     * @param item the item to check
     * @throws IllegalArgumentException if item is null
     */
    private void requireItem(MediaItem item)
    {
        if (item == null)
        {
            throw new IllegalArgumentException("Item cannot be null.");
        }
    }

    /**
     * Checks that a position is from 1 to max.
     *
     * @param position the 1-based position
     * @param max      the largest allowed position
     * @throws IllegalArgumentException if the position is out of range
     */
    private void checkPosition(int position, int max)
    {
        if (position < 1 || position > max)
        {
            throw new IllegalArgumentException(
                "Position must be between 1 and " + max + ".");
        }
    }
}
