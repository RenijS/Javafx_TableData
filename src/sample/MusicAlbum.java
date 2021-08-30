package sample;

public class MusicAlbum implements HasKey<String> {
    private String id;
    private String name;
    private String genre;
    private boolean isCompilation;
    private int trackCount;

    public MusicAlbum(String id, String name, String genre, boolean isCompilation, int trackCount)
    {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.isCompilation = isCompilation;
        this.trackCount = trackCount;
    }

    @Override
    public String getKey()
    {
        return getId();
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getGenre()
    {
        return genre;
    }

    public boolean isCompilation()
    {
        return isCompilation;
    }

    public int getTrackCount()
    {
        return trackCount;
    }

    public String toString()
    {
        return "" +
                id + "/" + name + "/" + genre + "/" + isCompilation + "/" + trackCount;
    }
}
