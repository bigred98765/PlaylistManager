public class Song {
    private String artist;
    private String title;

    // Creates a song with an artist and title.
    public Song(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    // Returns the artist's name.
    public String getArtist() {
        return artist;
    }

    // Returns the song title.
    public String getTitle() {
        return title;
    }

    // Formats the song for playlist playback.
    @Override
    public String toString() {
        return artist + " - " + title;
    }
}