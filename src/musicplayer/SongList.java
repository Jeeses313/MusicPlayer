package musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.scene.media.Media;

public class SongList {

    private HashMap<String, String> list;
    private ArrayList<String> names;

    public SongList() {
        this.list = new HashMap<>();
    }

    public boolean init(String path) {
        if (path.equals("")) {
            return false;
        }
        try {
            File musicDir = new File(path);
            if (!musicDir.exists() || !musicDir.isDirectory()) {
                return false;
            }
            File[] musicFiles = musicDir.listFiles();
            this.initPart(musicFiles);
            this.names = this.list.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
            this.names.sort((name1, name2) -> name1.toLowerCase().compareTo(name2.toLowerCase()));
        } catch (Exception e) {

        }
        if (this.names.isEmpty()) {
            return false;
        }
        return true;
    }

    private void initPart(File[] musicFiles) throws Exception {
        for (File musicFile : musicFiles) {
            String name = musicFile.getName();
            if (!name.contains(".mp3") && !name.contains(".mp4") && !name.contains(".wav")) {
                if (musicFile.isDirectory()) {
                    this.initPart(musicFile.listFiles());
                }
                continue;
            }
            name = name.substring(0, name.length() - 4);
            this.list.put(name, musicFile.toURI().toURL().toString());
        }
    }

    public Media getSong(int i) {
        return new Media(this.list.get(this.names.get(i)));
    }

    public int getListSize() {
        return this.names.size();
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public int getIndex(String name) {
        return this.names.indexOf(name);
    }

    public String getSongName(int i) {
        return this.names.get(i);
    }

    public String getNext(String name) {
        if (this.getIndex(name) == this.names.size() - 1) {
            return this.getSongName(0);
        } else {
            return this.names.get(this.names.indexOf(name) + 1);
        }
    }

    public String getRandomSong(String currentSong) {
        String newSong = this.names.get(new Random().nextInt(this.names.size()));
        if (newSong.equals(currentSong)) {
            return this.getNext(newSong);
        }
        return newSong;
    }

    public Media getSong(String name) {
        return new Media(this.list.get(name));
    }

    public void shuffle() {
        Collections.shuffle(names);
    }

    public void unshuffle() {
        this.names.sort((name1, name2) -> name1.toLowerCase().compareTo(name2.toLowerCase()));
    }

}
