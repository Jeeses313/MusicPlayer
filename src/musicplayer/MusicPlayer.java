package musicplayer;

import com.sun.glass.ui.Screen;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
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
    private ProgressBar timeBar;
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

        currentSongLabel = createLabel(205, 0, "Playing now: " + currentSong);
        currentSongLabel.setTooltip(new Tooltip(currentSong));
        currentSongLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                currentSongLabel.setTooltip(new Tooltip(currentSong));
            }
        });

        nextSongLabel = createLabel(205, 20, "Playing next: " + nextSong);
        nextSongLabel.setTooltip(new Tooltip(nextSong));
        nextSongLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                nextSongLabel.setTooltip(new Tooltip(nextSong));
            }
        });

        ObservableList<String> data = FXCollections.observableArrayList();
        if (works) {
            data.addAll(this.list.getNames());
        }
        listView = new ListView<>(data);
        listView.getSelectionModel().select(0);
        listView.setPrefSize(200, 220);
        listView.styleProperty().setValue("-fx-selection-bar: D3D3D3; -fx-focus-color: grey; -fx-faint-focus-color: transparent;");

        Button chooseFolder = createButton(0, 220, 200, 20, "Choose folder");

        Button loop = createButton(385, 70, 100, 30, "Loop:all");

        Button randomButton = createButton(385, 100, 100, 30, "Random:off");

        Button shuffleButton = createButton(385, 130, 100, 30, "Shuffle");

        Label volumeLabel = createLabel(205, 160, "Volume:");

        Slider volumeSlider = createSlider(0, 1, this.volume, 0.5, 260, 162, 200, 10);

        if (works) {
            timeLabel = createLabel(407, 40, String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));
        } else {
            timeLabel = createLabel(407, 40, "00:00/00:00");
        }

        timeBar = createProgressBar(205, 42, 200, 1);

        Label speedLabel = createLabel(205, 200, "Speed:");

        Slider speedSlider = createSlider(0.25, 2, 1, 0.25, 260, 202, 200, 10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMinorTickCount(0);

        Button play = createButton(205, 70, 90, 60, "");
        if (!works) {
            play.setDisable(true);
        }
        Rectangle square1 = createRectangle(15, 5, 10, 40);
        Rectangle square2 = createRectangle(45, 5, 10, 40);
        Pane pausePane = new Pane();
        pausePane.getChildren().addAll(square1, square2);
        Polygon triangle = createTriangle(20, 25, 0, -20, 0, 20, 30, 0);
        Pane playPane = new Pane();
        playPane.getChildren().add(triangle);
        play.setGraphic(playPane);

        Button playNext = createButton(295, 70, 90, 60, "");
        if (!works) {
            playNext.setDisable(true);
        }
        Rectangle square3 = createRectangle(45, 5, 10, 40);
        Polygon triangle2 = createTriangle(15, 25, 0, -20, 0, 20, 30, 0);
        Pane playNextPane = new Pane();
        playNextPane.getChildren().addAll(square3, triangle2);
        playNext.setGraphic(playNextPane);

        RadioButton left = createRadioButton(205, 130, 25, 30, "L");

        RadioButton middle = createRadioButton(231, 130, 36, 30, "M");

        RadioButton right = createRadioButton(266, 130, 25, 30, "R");

        ToggleGroup lmr = new ToggleGroup();
        lmr.getToggles().addAll(left, middle, right);
        middle.fire();

        Button mute = createButton(295, 130, 90, 30, "Mute");

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
        timeBar.setOnMouseClicked(ev -> {
            if (works) {
                this.player.seek(this.player.getMedia().getDuration().multiply(ev.getX() / timeBar.getWidth()));
                if (player.getStatus() != MediaPlayer.Status.PAUSED && player.getStatus() != MediaPlayer.Status.PLAYING) {
                    timeBar.setProgress(this.player.getCurrentTime().toSeconds() / this.player.getMedia().getDuration().toSeconds());
                    timeLabel.setText(String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));
                }
            }
        });
        timeBar.setOnMouseDragged(ev -> {
            if (works) {
                this.player.seek(this.player.getMedia().getDuration().multiply(ev.getX() / timeBar.getWidth()));
                if (player.getStatus() != MediaPlayer.Status.PAUSED && player.getStatus() != MediaPlayer.Status.PLAYING) {
                    timeBar.setProgress(this.player.getCurrentTime().toSeconds() / this.player.getMedia().getDuration().toSeconds());
                    timeLabel.setText(String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));
                }
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
        pan.getChildren().addAll(play, listView, currentSongLabel, nextSongLabel, randomButton, loop, playNext, left, middle, right, volumeLabel, speedLabel, volumeSlider, speedSlider, timeBar, timeLabel, chooseFolder, mute, shuffleButton);
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
            timeBar.setProgress(newValue.toSeconds() / this.player.getMedia().getDuration().toSeconds());
            timeLabel.setText(String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));

        });
        return newPlayer;
    }

    private Label createLabel(int x, int y, String text) {
        Label newLabel = new Label(text);
        newLabel.setTranslateX(x);
        newLabel.setTranslateY(y);
        return newLabel;
    }

    private Button createButton(int x, int y, int width, int height, String text) {
        Button newButton = new Button(text);
        newButton.setTranslateX(x);
        newButton.setTranslateY(y);
        newButton.setPrefSize(width, height);
        newButton.styleProperty().setValue("-fx-focus-color: grey; -fx-faint-focus-color: transparent;");
        return newButton;
    }

    private RadioButton createRadioButton(int x, int y, int width, int height, String text) {
        RadioButton newButton = new RadioButton(text);
        newButton.setTranslateX(x);
        newButton.setTranslateY(y);
        newButton.getStyleClass().remove("radio-button");
        newButton.getStyleClass().add("toggle-button");
        newButton.setPrefSize(width, height);
        newButton.styleProperty().setValue("-fx-focus-color: grey; -fx-faint-focus-color: transparent;");
        return newButton;
    }

    private Rectangle createRectangle(int x, int y, int width, int height) {
        Rectangle newRectangle = new Rectangle(width, height);
        newRectangle.setTranslateX(x);
        newRectangle.setTranslateY(y);
        return newRectangle;
    }

    private Polygon createTriangle(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3) {
        Polygon newTriangle = new Polygon(x1, y1, x2, y2, x3, y3);
        newTriangle.setTranslateX(x);
        newTriangle.setTranslateY(y);
        return newTriangle;
    }

    private Slider createSlider(double min, double max, double value, double major, int x, int y, int width, int height) {
        Slider newSlider = new Slider(min, max, value);
        newSlider.setTranslateX(x);
        newSlider.setTranslateY(y);
        newSlider.setPrefSize(width, height);
        newSlider.setMajorTickUnit(major);
        newSlider.setShowTickLabels(true);
        newSlider.setShowTickMarks(true);
        newSlider.styleProperty().setValue("-fx-focus-color: grey; -fx-faint-focus-color: transparent;");
        return newSlider;
    }

    private ProgressBar createProgressBar(int x, int y, int width, int height) {
        ProgressBar newProgressBar = new ProgressBar(0);
        newProgressBar.setTranslateX(x);
        newProgressBar.setTranslateY(y);
        newProgressBar.setPrefSize(width, height);
        newProgressBar.setStyle("-fx-accent: #A9A9A9;");
        return newProgressBar;
    }
}
