package musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import javafx.scene.media.Media;

public class SongList {

    private HashMap<String, String> songPaths;
    private ArrayList<String> folderNames;
    private HashMap<String, ArrayList<String>> folderContents;

    public SongList() {
        this.songPaths = new HashMap<>();
        this.folderNames = new ArrayList<>();
        this.folderContents = new HashMap<>();
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
            folderNames.add("All");
            folderContents.put("All", new ArrayList<>());
            Stack<String> folderStack = new Stack<>();
            folderStack.add("All");
            File[] musicFiles = musicDir.listFiles();
            this.initPart(musicFiles, folderStack);
            Collections.sort(folderNames.subList(1, folderNames.size()));
            for (String folderName : folderNames) {
                this.folderContents.get(folderName).sort((name1, name2) -> name1.toLowerCase().compareTo(name2.toLowerCase()));
            }
        } catch (Exception e) {

        }
        if (this.folderContents.get("All").isEmpty()) {
            return false;
        }
        return true;
    }

    private void initPart(File[] musicFiles, Stack<String> folderStack) throws Exception {
        for (File musicFile : musicFiles) {
            String name = musicFile.getName();
            if (!name.contains(".mp3") && !name.contains(".mp4") && !name.contains(".wav")) {
                if (musicFile.isDirectory()) {
                    folderNames.add(name);
                    folderContents.put(name, new ArrayList<>());
                    folderStack.push(name);
                    this.initPart(musicFile.listFiles(), folderStack);
                    folderStack.pop();
                }
                continue;
            }
            name = name.substring(0, name.length() - 4);
            this.songPaths.put(name, musicFile.toURI().toURL().toString());
            for (String folderName : folderStack) {
                folderContents.get(folderName).add(name);
            }
        }
    }

    public Media getSong(int i, String folder) {
        return new Media(this.songPaths.get(this.folderContents.get(folder).get(i)));
    }

    public ArrayList<String> getFolderNames() {
        return folderNames;
    }

    public ArrayList<String> getFolderContents(String folderName) {
        return folderContents.getOrDefault(folderName, new ArrayList<>());
    }

    public int getListSize(String folder) {
        return this.folderContents.get(folder).size();
    }

    public int getIndex(String name, String folder) {
        return this.folderContents.get(folder).indexOf(name);
    }

    public String getSongName(int i, String folder) {
        return this.folderContents.get(folder).get(i);
    }

    public String getNext(String name, String folder) {
        if (this.getIndex(name, folder) == this.folderContents.get(folder).size() - 1) {
            return this.folderContents.get(folder).get(0);
        } else {
            return this.folderContents.get(folder).get(this.folderContents.get(folder).indexOf(name) + 1);
        }
    }

    public String getRandomSong(String currentSong, String folder) {
        String newSong = this.folderContents.get(folder).get(new Random().nextInt(this.folderContents.get(folder).size()));
        if (newSong.equals(currentSong)) {
            return this.getNext(newSong, folder);
        }
        return newSong;
    }

    public Media getSong(String name) {
        return new Media(this.songPaths.get(name));
    }

    public void shuffle() {
        for (String folderName : folderNames) {
            Collections.shuffle(this.folderContents.get(folderName));
        }
    }

    public void unshuffle() {
        for (String folderName : folderNames) {
            this.folderContents.get(folderName).sort((name1, name2) -> name1.toLowerCase().compareTo(name2.toLowerCase()));
        }
    }

}
