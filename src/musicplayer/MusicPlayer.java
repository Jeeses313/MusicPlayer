package musicplayer;

import com.sun.glass.ui.Screen;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MusicPlayer extends Application {

    private SongList list;
    private String currentSong;
    private MediaPlayer player;
    private String nextSong;
    private double volume;
    private double balance;
    private double speed;
    private boolean loopAll;
    private boolean random;
    private Label currentSongLabel;
    private Label nextSongLabel;
    private ListView<String> listView;
    private ProgressBar timeSlider;
    private Label timeLabel;
    private boolean works;
    private double muteVolume;
    private boolean shuffled;

    public static void main(String[] args) {
        launch(MusicPlayer.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pan = new Pane();
        Scene scene = new Scene(pan, 475, 235);
        this.list = new SongList();

        works = this.list.init(Preferences.userRoot().get("path", ""));
        
        primaryStage.setScene(scene);

        if (works) {
            this.currentSong = this.list.getSongName(0);
            this.nextSong = this.list.getNext(this.currentSong);
        } else {
            this.currentSong = "";
            this.nextSong = "";
        }

        this.volume = Preferences.userRoot().getDouble("Vol", 0.4);
        this.muteVolume = this.volume;
        this.speed = 1;
        this.balance = 0.0;
        this.loopAll = true;
        this.random = false;
        this.shuffled = false;
        if (works) {
            this.player = newPlayer();
        }

        currentSongLabel = new Label("Playing now: " + currentSong);
        currentSongLabel.setTranslateX(205);

        nextSongLabel = new Label("Playing next: " + nextSong);
        nextSongLabel.setTranslateX(205);
        nextSongLabel.setTranslateY(20);

        ObservableList<String> data = FXCollections.observableArrayList();
        if (works) {
            data.addAll(this.list.getNames());
        }
        listView = new ListView<>(data);
        listView.getSelectionModel().select(0);
        listView.setPrefSize(200, 220);

        Button chooseFolder = new Button("Choose folder");
        chooseFolder.setTranslateY(220);
        chooseFolder.setPrefSize(200, 20);

        Button loop = new Button("Loop:all");
        loop.setTranslateX(385);
        loop.setTranslateY(70);
        loop.setPrefSize(100, 30);

        Button randomButton = new Button("Random:off");
        randomButton.setTranslateX(385);
        randomButton.setTranslateY(100);
        randomButton.setPrefSize(100, 30);

        Button shuffleButton = new Button("Shuffle");
        shuffleButton.setTranslateX(385);
        shuffleButton.setTranslateY(130);
        shuffleButton.setPrefSize(100, 30);

        Label volumeLabel = new Label("Volume:");
        volumeLabel.setTranslateX(205);
        volumeLabel.setTranslateY(160);

        Slider volumeSlider = new Slider(0, 1, this.volume);
        volumeSlider.setTranslateX(260);
        volumeSlider.setTranslateY(162);
        volumeSlider.setPrefSize(200, 10);
        volumeSlider.setMajorTickUnit(0.5);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        if (works) {
            timeLabel = new Label(String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));
        } else {
            timeLabel = new Label("00:00/00:00");
        }
        timeLabel.setTranslateX(407);
        timeLabel.setTranslateY(40);

        timeSlider = new ProgressBar(0);
        timeSlider.setTranslateX(205);
        timeSlider.setTranslateY(42);
        timeSlider.setPrefSize(200, 1);

        Label speedLabel = new Label("Speed:");
        speedLabel.setTranslateX(205);
        speedLabel.setTranslateY(200);

        Slider speedSlider = new Slider(0.25, 2, 1);
        speedSlider.setTranslateX(260);
        speedSlider.setTranslateY(202);
        speedSlider.setPrefSize(200, 10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMajorTickUnit(0.25);
        speedSlider.setMinorTickCount(0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);

        Button play = new Button();
        if (!works) {
            play.setDisable(true);
        }
        play.setTranslateX(205);
        play.setTranslateY(70);
        play.setPrefSize(90, 60);
        Rectangle square1 = new Rectangle(10, 40);
        Rectangle square2 = new Rectangle(10, 40);
        square1.setTranslateX(15);
        square1.setTranslateY(5);
        square2.setTranslateX(45);
        square2.setTranslateY(5);
        Pane pausePane = new Pane();
        pausePane.getChildren().addAll(square1, square2);
        Polygon triangle = new Polygon(0, -20, 0, 20, 30, 0);
        triangle.setTranslateX(20);
        triangle.setTranslateY(25);
        Pane playPane = new Pane();
        playPane.getChildren().add(triangle);
        play.setGraphic(playPane);

        Button playNext = new Button();
        if (!works) {
            playNext.setDisable(true);
        }
        playNext.setTranslateX(295);
        playNext.setTranslateY(70);
        playNext.setPrefSize(90, 60);
        Rectangle square3 = new Rectangle(10, 40);
        square3.setTranslateX(45);
        square3.setTranslateY(5);
        Polygon triangle2 = new Polygon(0, -20, 0, 20, 30, 0);
        triangle2.setTranslateX(15);
        triangle2.setTranslateY(25);
        Pane playNextPane = new Pane();
        playNextPane.getChildren().addAll(square3, triangle2);
        playNext.setGraphic(playNextPane);

        RadioButton left = new RadioButton("L");
        left.setTranslateX(205);
        left.setTranslateY(130);
        left.setPrefSize(25, 30);
        left.getStyleClass().remove("radio-button");
        left.getStyleClass().add("toggle-button");

        RadioButton middle = new RadioButton("M");
        middle.setTranslateX(231);
        middle.setTranslateY(130);
        middle.setPrefSize(36, 30);
        middle.getStyleClass().remove("radio-button");
        middle.getStyleClass().add("toggle-button");

        RadioButton right = new RadioButton("R");
        right.setTranslateX(266);
        right.setTranslateY(130);
        right.setPrefSize(25, 30);
        right.getStyleClass().remove("radio-button");
        right.getStyleClass().add("toggle-button");

        ToggleGroup lmr = new ToggleGroup();
        lmr.getToggles().addAll(left, middle, right);
        middle.fire();

        Button mute = new Button("Mute");
        mute.setTranslateX(295);
        mute.setTranslateY(130);
        mute.setPrefSize(90, 30);

        listView.setOnMouseClicked(ev -> {
            if (works) {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    play.setGraphic(pausePane);
                    this.player.stop();
                    this.currentSong = listView.getSelectionModel().getSelectedItem();
                    currentSongLabel.setText("Playing now: " + currentSong);
                    if (!this.random) {
                        nextSong = this.list.getNext(this.currentSong);
                    } else {
                        nextSong = list.getRandomSong(currentSong);
                    }
                    nextSongLabel.setText("Playing next: " + nextSong);
                    player.dispose();
                    this.player = newPlayer();
                    this.player.play();
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    this.nextSong = listView.getSelectionModel().getSelectedItem();
                    nextSongLabel.setText("Playing next: " + nextSong);
                }
            }

        });

        play.setOnAction(e -> {
            if (play.getGraphic().equals(pausePane)) {
                this.player.pause();
                play.setGraphic(playPane);
            } else if (play.getGraphic().equals(playPane)) {
                this.player.play();
                play.setGraphic(pausePane);
            }
        });

        loop.setOnAction(e -> {
            if (this.loopAll) {
                this.loopAll = false;
                loop.setText("Loop:one");
                playNext.setDisable(true);
                nextSongLabel.setDisable(true);
            } else {
                this.loopAll = true;
                loop.setText("Loop:all");
                if (works) {
                    playNext.setDisable(false);
                }

                nextSongLabel.setDisable(false);
            }
        });

        randomButton.setOnMouseClicked(e -> {

            if (this.random) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.random = false;
                    randomButton.setText("Random:off");
                    if (works) {
                        nextSong = list.getNext(currentSong);
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if (works) {
                        nextSong = list.getRandomSong(currentSong);
                    }
                }

            } else {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.random = true;
                    randomButton.setText("Random:on");
                    if (works) {
                        nextSong = list.getRandomSong(currentSong);
                    }
                }

            }
            nextSongLabel.setText("Playing next: " + nextSong);

        });

        shuffleButton.setOnMouseClicked(e -> {

            if (this.shuffled) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.shuffled = false;
                    shuffleButton.setText("Shuffle");
                    if (works) {
                        this.list.unshuffle();
                        data.clear();
                        data.addAll(this.list.getNames());
                        this.listView.itemsProperty().set(data);
                        listView.getSelectionModel().select(currentSong);
                        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
                        if (!random) {
                            nextSong = list.getNext(currentSong);
                        }
                        nextSongLabel.setText("Playing next: " + nextSong);
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if (works) {
                        this.list.shuffle();
                        data.clear();
                        data.addAll(this.list.getNames());
                        this.listView.itemsProperty().set(data);
                        listView.getSelectionModel().select(currentSong);
                        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
                        if (!random) {
                            nextSong = list.getNext(currentSong);
                        }
                        nextSongLabel.setText("Playing next: " + nextSong);
                    }
                }
            } else {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.shuffled = true;
                    shuffleButton.setText("Unshuffle");
                    if (works) {
                        this.list.shuffle();
                        data.clear();
                        data.addAll(this.list.getNames());
                        this.listView.itemsProperty().set(data);
                        listView.getSelectionModel().select(currentSong);
                        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
                        if (!random) {
                            nextSong = list.getNext(currentSong);
                        }
                        nextSongLabel.setText("Playing next: " + nextSong);
                    }
                }

            }

        });

        playNext.setOnAction(e -> {
            this.player.stop();
            currentSong = nextSong;
            currentSongLabel.setText("Playing now: " + currentSong);
            if (!random) {
                nextSong = list.getNext(currentSong);
            } else {
                nextSong = list.getRandomSong(currentSong);
            }
            nextSongLabel.setText("Playing next: " + nextSong);
            listView.getSelectionModel().select(currentSong);
            listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
            player.dispose();
            player = newPlayer();
            player.play();
            play.setGraphic(pausePane);
        });

        left.setOnAction(e -> {
            this.balance = -1.0;
            if (works) {
                this.player.setBalance(this.balance);
            }
        });

        right.setOnAction(e -> {
            this.balance = 1.0;
            if (works) {
                this.player.setBalance(this.balance);
            }

        });

        middle.setOnAction(e -> {
            this.balance = 0.0;
            if (works) {
                this.player.setBalance(this.balance);
            }

        });

        volumeSlider.valueProperty().addListener((volChange, oldVol, newVol) -> {

            this.volume = volumeSlider.getValue();
            if (works) {
                this.player.setVolume(volume);
            }

        });
        speedSlider.valueProperty().addListener((volChange, oldVol, newVol) -> {

            this.speed = speedSlider.getValue();
            if (works) {
                this.player.setRate(speed);
            }

        });
        timeSlider.setOnMouseClicked(ev -> {
            if (works) {
                this.player.setStartTime(this.player.getMedia().getDuration().multiply(ev.getX() / timeSlider.getWidth()));
                this.player.stop();
                this.player.play();
            }

        });
        timeSlider.setOnMouseDragged(ev -> {
            if (works) {
                this.player.setStartTime(this.player.getMedia().getDuration().multiply(ev.getX() / timeSlider.getWidth()));
                this.player.stop();
                this.player.play();
            }

        });

        chooseFolder.setOnAction(ev -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select music folder");
            File folder = null;
            folder = directoryChooser.showDialog(primaryStage);
            if (folder != null) {
                String path = folder.getPath();
                SongList newList = new SongList();
                if (newList.init(path)) {
                    Preferences.userRoot().put("path", path);
                    if (works) {
                        this.player.stop();
                    }
                    works = true;
                    this.list = newList;
                    data.clear();
                    data.addAll(this.list.getNames());
                    this.currentSong = this.list.getSongName(0);
                    currentSongLabel.setText("Playing now: " + currentSong);
                    this.nextSong = this.list.getNext(this.currentSong);
                    nextSongLabel.setText("Playing next: " + nextSong);
                    play.setGraphic(playPane);
                    play.setDisable(false);
                    playNext.setDisable(false);
                    this.player = newPlayer();
                }
            }

        });
        mute.setOnAction(e -> {
            if (volumeSlider.getValue() == 0) {
                volumeSlider.setValue(this.muteVolume);
            } else {
                this.muteVolume = this.volume;
                volumeSlider.setValue(0);
            }

        });
        primaryStage.setOnCloseRequest(e -> {
            Preferences.userRoot().putDouble("x", primaryStage.getX());
            Preferences.userRoot().putDouble("y", primaryStage.getY());
            Preferences.userRoot().putDouble("Vol", volume);
        });
        double y = Preferences.userRoot().getDouble("y", -1);
        double x = Preferences.userRoot().getDouble("x", -1);
        if (x >= 0 && y >= 0 && x < Screen.getMainScreen().getWidth() && y < Screen.getMainScreen().getHeight()) {
            primaryStage.setX(x);
            primaryStage.setY(y);
        }

        primaryStage.setResizable(false);
        pan.getChildren().addAll(play, listView, currentSongLabel, nextSongLabel, randomButton, loop, playNext, left, middle, right, volumeLabel, speedLabel, volumeSlider, speedSlider, timeSlider, timeLabel, chooseFolder, mute, shuffleButton);
        primaryStage.setTitle("MusicPlayer");
        primaryStage.show();

    }

    private MediaPlayer newPlayer() {
        MediaPlayer newPlayer = new MediaPlayer(list.getSong(currentSong));
        newPlayer.setVolume(volume);
        newPlayer.setRate(speed);
        newPlayer.setBalance(balance);
        newPlayer.setOnEndOfMedia(() -> {
            player.stop();
            if (loopAll) {
                currentSong = nextSong;
            }
            currentSongLabel.setText("Playing now: " + currentSong);
            if (!random) {
                nextSong = list.getNext(currentSong);
            } else {
                nextSong = list.getRandomSong(currentSong);
            }
            nextSongLabel.setText("Playing next: " + nextSong);
            listView.getSelectionModel().select(currentSong);
            listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
            player.dispose();
            player = newPlayer();
            player.play();
        });
        newPlayer.currentTimeProperty().addListener((change, oldValue, newValue) -> {
            timeSlider.setProgress(newValue.toSeconds() / this.player.getMedia().getDuration().toSeconds());
            timeLabel.setText(String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));

        });
        return newPlayer;
    }

}
