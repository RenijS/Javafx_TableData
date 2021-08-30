package sample;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MusicCatalogDS {
    private List<MusicAlbum> albums = new ArrayList<>();

    private Random random = new Random();
    // for generating music album id's

    //	Note that the client, for example a JavaFX program, will be
    // responsible for creating the MusicAlbum instance to pass
    //	as the parameter
    //
    public void add(MusicAlbum album) throws Exception
    {
        boolean pre = get(album.getId()) == null;

        if(!pre)
        {
            throw new Exception("Album ID is not new!");
        }

        albums.add(album);
    }

    // Search for the music album with the given id
    // (get one music album)
    //
    public MusicAlbum get(String id)
    {
        for (MusicAlbum album : albums)
        {
            if (album.getId().equalsIgnoreCase(id))
            {
                return album;
            }
        }

        return null;
    }

    //	get all the music albums
    //
    public List<MusicAlbum> getAll()
    {
        return albums;
    }

    //	remove a music album
    // And return the deleted object, which may be useful for the client
    // Return null if the album to be deleted does not exist
    //
    public MusicAlbum remove(String id)
    {
        MusicAlbum album = get(id);
        albums.remove(album);

        return album;
    }

    //	update a music album
    //
    public void update(MusicAlbum album) throws Exception
    {

        if (remove(album.getId()) == null)
        {
            throw new Exception("Album ID does not exist!");
        }

        add(album);
    }

    // A method to generate a unique random id (a string beginning with A)
    //	Used mainly for testing purpose
    //
    public String generateId()
    {
        String id;
        // repeat until we get a new id
        do
        {
            int n = 1 + random.nextInt(albums.size() * 2 + 1); 	// n = 1 to 2*size
            int numberOfDigits = (int) Math.ceil(Math.log10(n + 0.5));
            String formatter = "A%" + numberOfDigits + "d";
            id = String.format(formatter, n);
        }
        while (get(id) != null);

        return id;
    }

    public void saveData(String fileName) throws IOException
    {
        // If fileName is the name of an existing directory, the statement
        // throws an IOException.
        // If fileName is the name of an exiting file, the file is overwritten.
        // Otherwise, a new file is created
        //
        PrintWriter out = new PrintWriter(new File(fileName));

        for (MusicAlbum album : albums)
        {
            out.println(serializeAlbum(album));
        }

        out.close();
    }

    // Return a string containing the data of the album. The content of the string
    // will allow us to reconstruct the object. (Hence tthe name 'serialize')
    //
    public String serializeAlbum(MusicAlbum album)
    {
        return String.format(
                "%s;%s;%s;%b;%d",
                album.getId(),
                album.getName(),
                album.getGenre(),
                album.isCompilation(),
                album.getTrackCount());
    }

    public void loadData(String fileName) throws Exception
    {
        albums = new ArrayList<>();
        Scanner in = new Scanner(new File(fileName));

        while (in.hasNext())
        {
            // split the next line with the character ";"
            String line = in.nextLine();
            String[] tokens = line.split(";");

            // construct a new MusicAlbum using the resulting tokens.
            // If a line doesn't contain enough data or the data is invalid,
            // exception will be thrown.
            //
            albums.add(new MusicAlbum(
                    tokens[0],
                    tokens[1],
                    tokens[2],
                    Boolean.parseBoolean(tokens[3]),
                    Integer.parseInt(tokens[4])
            ));
        }
    }

    // Return a string to display albums in the colletion
    // Each album is on a separate line
    //
    public String toString()
    {
        String s = "";
        for (MusicAlbum ma : albums)
        {
            s += serializeAlbum(ma) + "\n";
        }
        return s;
    }
}
