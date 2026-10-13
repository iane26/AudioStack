/**
 * One music track. The creator is the artist, and the album is optional.
 * The album is not part of equality: the same track on two albums counts
 * as the same song.
 *
 * @author AudioStack team
 * @version October 2026
 */
public class Song extends MediaItem
{
    private final String album;

    /**
     * Creates a song.
     *
     * @param title  the song title; must not be null or blank
     * @param artist the artist; must not be null or blank
     * @param album  the album, or null for none (stored as "")
     * @throws IllegalArgumentException if title or artist is null or blank
     */
    public Song(String title, String artist, String album)
    {
        super(title, artist);
        this.album = (album == null) ? "" : album.trim();
    }

    /**
     * Gets the artist, which is the creator.
     *
     * @return the artist
     */
    public String getArtist()
    {
        return getCreator();
    }

    /**
     * Gets the album.
     *
     * @return the album, or "" if there is none; never null
     */
    public String getAlbum()
    {
        return album;
    }

    /**
     * Names this media type.
     *
     * @return "Song"
     */
    @Override
    public String getType()
    {
        return "Song";
    }
}
