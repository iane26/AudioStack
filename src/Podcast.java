/**
 * One podcast episode. The creator is the show name. Two episodes of the
 * same show with different episode numbers are different items.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class Podcast extends MediaItem
{
    private final int episodeNumber;

    /**
     * Creates a podcast episode.
     *
     * @param title         the episode title; must not be null or blank
     * @param show          the show name; must not be null or blank
     * @param episodeNumber the episode number; must be 1 or more
     * @throws IllegalArgumentException if title or show is null or blank,
     *         or episodeNumber is below 1
     */
    public Podcast(String title, String show, int episodeNumber)
    {
        super(title, show);
        if (episodeNumber < 1)
        {
            throw new IllegalArgumentException(
                "Episode number must be at least 1.");
        }
        this.episodeNumber = episodeNumber;
    }

    /**
     * Gets the show name, which is the creator.
     *
     * @return the show name
     */
    public String getShowName()
    {
        return getCreator();
    }

    /**
     * Gets the episode number.
     *
     * @return the episode number, always 1 or more
     */
    public int getEpisodeNumber()
    {
        return episodeNumber;
    }

    /**
     * Names this media type.
     *
     * @return "Podcast"
     */
    @Override
    public String getType()
    {
        return "Podcast";
    }

    /**
     * MediaItem equality plus a matching episode number.
     *
     * @param other the object to compare with
     * @return true if other is the same episode
     */
    @Override
    public boolean equals(Object other)
    {
        return super.equals(other)
            && episodeNumber == ((Podcast) other).episodeNumber;
    }

    /**
     * Includes the episode number so it agrees with equals.
     *
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return 31 * super.hashCode() + episodeNumber;
    }

    /**
     * Formats the episode as "[Podcast] Title (em dash) Show #12".
     *
     * @return the formatted episode
     */
    @Override
    public String toString()
    {
        return super.toString() + " #" + episodeNumber;
    }
}
