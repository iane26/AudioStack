import java.util.Locale;

/**
 * Anything playable in AudioStack. Stores the title and creator that every
 * media type shares and defines the one equality rule that searching,
 * removing, and counting depend on.
 *
 * Two items are equal when they are the same class and their titles and
 * creators match, ignoring case. MediaItem objects are immutable.
 *
 * @author AudioStack team
 * @version October 2026
 */
public abstract class MediaItem
{
    /** " (em dash) " between title and creator, written as a char code so
     *  the source file stays plain ASCII on every computer. */
    protected static final String DASH = " " + (char) 8212 + " ";

    private final String title;
    private final String creator;

    /**
     * Creates a media item with a trimmed title and creator.
     *
     * @param title   the title; must not be null or blank
     * @param creator the artist or show name; must not be null or blank
     * @throws IllegalArgumentException if either value is null or blank
     */
    protected MediaItem(String title, String creator)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
        if (creator == null || creator.trim().isEmpty())
        {
            throw new IllegalArgumentException("Creator cannot be blank.");
        }
        this.title = title.trim();
        this.creator = creator.trim();
    }

    /**
     * Gets the trimmed title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the trimmed creator (artist for a Song, show for a Podcast).
     *
     * @return the creator
     */
    public String getCreator()
    {
        return creator;
    }

    /**
     * Names the kind of media: "Song" or "Podcast".
     *
     * @return the media type name
     */
    public abstract String getType();

    /**
     * Two items are equal when they are the same class and their titles
     * and creators match, ignoring case.
     *
     * @param other the object to compare with
     * @return true if other is the same item
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || getClass() != other.getClass())
        {
            return false;
        }
        MediaItem that = (MediaItem) other;
        return title.equalsIgnoreCase(that.title)
            && creator.equalsIgnoreCase(that.creator);
    }

    /**
     * Built from the class, lowercase title, and lowercase creator so it
     * agrees with equals.
     *
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        int result = getClass().getName().hashCode();
        result = 31 * result + title.toLowerCase(Locale.ROOT).hashCode();
        result = 31 * result + creator.toLowerCase(Locale.ROOT).hashCode();
        return result;
    }

    /**
     * Formats the item as "[Type] Title (em dash) Creator".
     *
     * @return the formatted item
     */
    @Override
    public String toString()
    {
        return "[" + getType() + "] " + title + DASH + creator;
    }
}
