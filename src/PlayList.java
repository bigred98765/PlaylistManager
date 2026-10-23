
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class PlayList {

    // Displays the available commands.
    private static void printMenu() {
        System.out.println("*** Playlist Manager! ***");
        System.out.println("Commands:");
        System.out.println("add");
        System.out.println("remove");
        System.out.println("count");
        System.out.println("play");
        System.out.println("shuffle");
        System.out.println("reverse");
        System.out.println("save");
        System.out.println("load");
        System.out.println("quit");
    }

    // Adds a new song to the end of the playlist.
    private static void addSong(MyDoubleLinkedList<Song> playlist,
                                Scanner scanner) {
        System.out.print("Enter artist: ");
        String artist = scanner.nextLine();

        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        playlist.add(new Song(artist, title));
    }

    // Removes the first matching song from the playlist.
    private static void removeSong(MyDoubleLinkedList<Song> playlist,
                                   Scanner scanner) {
        System.out.print("Enter artist: ");
        String artist = scanner.nextLine();

        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        for (int i = 0; i < playlist.count(); i++) {
            Song song = playlist.get(i);

            if (song.getArtist().equalsIgnoreCase(artist)
                    && song.getTitle().equalsIgnoreCase(title)) {
                playlist.remove(i);
                System.out.println("Song removed.");
                return;
            }
        }

        System.out.println("Song not found.");
    }

    // Prints the playlist in its current order.
    private static void playSongs(MyDoubleLinkedList<Song> playlist) {
        if (playlist.isEmpty()) {
            System.out.println("The playlist is empty.");
            return;
        }

        for (Song song : playlist) {
            System.out.println(song);
        }
    }

    // Randomizes the song order using Fisher-Yates shuffling.
    // The list remains a doubly linked list.
    private static void shuffle(MyDoubleLinkedList<Song> playlist,
                                Random random) {
        int size = playlist.count();

        if (size < 2) {
            System.out.println("Not enough songs to shuffle.");
            return;
        }

        // Save references temporarily for efficient random selection.
        Song[] songs = new Song[size];

        for (int i = 0; i < size; i++) {
            songs[i] = playlist.get(i);
        }

        // Shuffle the temporary array.
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            Song temp = songs[i];
            songs[i] = songs[j];
            songs[j] = temp;
        }

        // Put the songs back into the linked list.
        for (int i = 0; i < size; i++) {
            playlist.set(i, songs[i]);
        }

        System.out.println("Playlist shuffled.");
    }

    // Saves each song using two lines: artist, then title.
    private static void savePlaylist(MyDoubleLinkedList<Song> playlist,
                                     Scanner scanner) {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Invalid filename.");
            return;
        }

        try {
            Path path = Paths.get(filename);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path, StandardCharsets.UTF_8)) {

                for (Song song : playlist) {
                    writer.write(song.getArtist());
                    writer.newLine();
                    writer.write(song.getTitle());
                    writer.newLine();
                }
            }

            System.out.println("Playlist saved.");
        } catch (IOException | RuntimeException exception) {
            System.out.println("Unable to save playlist: "
                    + exception.getMessage());
        }
    }

    // Loads a playlist previously saved by this program.
    // Existing songs are replaced only after a successful read.
    private static void loadPlaylist(MyDoubleLinkedList<Song> playlist,
                                     Scanner scanner) {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Invalid filename.");
            return;
        }

        MyDoubleLinkedList<Song> loadedSongs =
                new MyDoubleLinkedList<>();

        try {
            Path path = Paths.get(filename);

            try (BufferedReader reader = Files.newBufferedReader(
                    path, StandardCharsets.UTF_8)) {

                String artist;

                while ((artist = reader.readLine()) != null) {
                    String title = reader.readLine();

                    if (title == null) {
                        System.out.println(
                                "Invalid playlist file format.");
                        return;
                    }

                    loadedSongs.add(new Song(artist, title));
                }
            }

            playlist.clear();
            playlist.addAll(loadedSongs);

            System.out.println("Playlist loaded.");
        } catch (IOException | RuntimeException exception) {
            System.out.println("Unable to load playlist: "
                    + exception.getMessage());
        }
    }

    public static void main(String[] args) {
        MyDoubleLinkedList<Song> playlist =
                new MyDoubleLinkedList<>();

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        printMenu();

        boolean running = true;

        while (running && scanner.hasNextLine()) {
            System.out.print("\n: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "add":
                    addSong(playlist, scanner);
                    break;

                case "remove":
                    removeSong(playlist, scanner);
                    break;

                case "count":
                    System.out.println(playlist.count());
                    break;

                case "play":
                    playSongs(playlist);
                    break;

                case "shuffle":
                    shuffle(playlist, random);
                    break;

                case "reverse":
                    playlist.reverse();
                    break;

                case "save":
                    savePlaylist(playlist, scanner);
                    break;

                case "load":
                    loadPlaylist(playlist, scanner);
                    break;

                case "quit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Unknown command. Try again.");
            }
        }
    }
}