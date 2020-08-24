package musicplayer;

import com.sun.glass.ui.Screen;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

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
    private GlobalKeyListener globalKeyListener;

    public static void main(String[] args) {
        launch(MusicPlayer.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pan = new Pane();
        Scene scene = new Scene(pan, 475, 235);
        this.list = new SongList();

        works = this.list.init(Preferences.userRoot().get("MusicPlayerPath", ""));

        primaryStage.setScene(scene);

        if (works) {
            this.currentSong = this.list.getSongName(0);
            this.nextSong = this.list.getNext(this.currentSong);
        } else {
            this.currentSong = "";
            this.nextSong = "";
        }

        this.volume = Preferences.userRoot().getDouble("MusicPlayerVolume", 0.4);
        this.muteVolume = this.volume;
        this.speed = 1;
        this.balance = 0.0;
        this.loopAll = true;
        this.random = false;
        this.shuffled = false;
        if (works) {
            this.player = newPlayer();
        }

        currentSongLabel = ComponentCreator.createLabel(205, 0, "Playing now: " + currentSong);
        currentSongLabel.setTooltip(new Tooltip(currentSong));
        currentSongLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                currentSongLabel.setTooltip(new Tooltip(currentSong));
            }
        });

        nextSongLabel = ComponentCreator.createLabel(205, 20, "Playing next: " + nextSong);
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

        Button chooseFolder = ComponentCreator.createButton(0, 220, 120, 20, "Choose folder");
        Button settings = ComponentCreator.createButton(120, 220, 80, 20, "Hotkeys");

        Button loop = ComponentCreator.createButton(385, 70, 100, 30, "Loop:all");

        Button randomButton = ComponentCreator.createButton(385, 100, 100, 30, "Random:off");

        Button shuffleButton = ComponentCreator.createButton(385, 130, 100, 30, "Shuffle");

        Label volumeLabel = ComponentCreator.createLabel(205, 160, "Volume:");

        Slider volumeSlider = ComponentCreator.createSlider(0, 1, this.volume, 0.5, 260, 162, 200, 10);

        if (works) {
            timeLabel = ComponentCreator.createLabel(407, 40, String.format("%02d:%02d", (int) ((this.player.getCurrentTime().toSeconds() % 3600) / 60), (int) ((this.player.getCurrentTime().toSeconds() % 60))) + "/" + String.format("%02d:%02d", (int) ((this.player.getMedia().getDuration().toSeconds() % 3600) / 60), (int) ((this.player.getMedia().getDuration().toSeconds() % 60))));
        } else {
            timeLabel = ComponentCreator.createLabel(407, 40, "00:00/00:00");
        }

        timeBar = ComponentCreator.createProgressBar(205, 42, 200, 1);

        Label speedLabel = ComponentCreator.createLabel(205, 200, "Speed:");

        Slider speedSlider = ComponentCreator.createSlider(0.25, 2, 1, 0.25, 260, 202, 200, 10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMinorTickCount(0);

        Button play = ComponentCreator.createButton(205, 70, 90, 60, "");
        if (!works) {
            play.setDisable(true);
        }
        Rectangle square1 = ComponentCreator.createRectangle(15, 5, 10, 40);
        Rectangle square2 = ComponentCreator.createRectangle(45, 5, 10, 40);
        Pane pausePane = new Pane();
        pausePane.getChildren().addAll(square1, square2);
        Polygon triangle = ComponentCreator.createTriangle(20, 25, 0, -20, 0, 20, 30, 0);
        Pane playPane = new Pane();
        playPane.getChildren().add(triangle);
        play.setGraphic(playPane);

        Button playNext = ComponentCreator.createButton(295, 70, 90, 60, "");
        if (!works) {
            playNext.setDisable(true);
        }
        Rectangle square3 = ComponentCreator.createRectangle(45, 5, 10, 40);
        Polygon triangle2 = ComponentCreator.createTriangle(15, 25, 0, -20, 0, 20, 30, 0);
        Pane playNextPane = new Pane();
        playNextPane.getChildren().addAll(square3, triangle2);
        playNext.setGraphic(playNextPane);

        RadioButton left = ComponentCreator.createRadioButton(205, 130, 25, 30, "L");

        RadioButton middle = ComponentCreator.createRadioButton(231, 130, 36, 30, "M");

        RadioButton right = ComponentCreator.createRadioButton(266, 130, 25, 30, "R");

        ToggleGroup lmr = new ToggleGroup();
        lmr.getToggles().addAll(left, middle, right);
        middle.fire();

        Button mute = ComponentCreator.createButton(295, 130, 90, 30, "Mute");

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
                    Preferences.userRoot().put("MusicPlayerPath", path);
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
        primaryStage.setOnCloseRequest(ev -> {
            Preferences.userRoot().putDouble("MusicPlayerScreenX", primaryStage.getX());
            Preferences.userRoot().putDouble("MusicPlayerScreenY", primaryStage.getY());
            Preferences.userRoot().putDouble("MusicPlayerVolume", volume);
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (Exception ex) {
            }

        });
        double y = Preferences.userRoot().getDouble("MusicPlayerScreenY", -1);
        double x = Preferences.userRoot().getDouble("MusicPlayerScreenX", -1);
        if (x >= 0 && y >= 0 && x < Screen.getMainScreen().getWidth() && y < Screen.getMainScreen().getHeight()) {
            primaryStage.setX(x);
            primaryStage.setY(y);
        }

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
        GlobalScreen.registerNativeHook();
        globalKeyListener = new GlobalKeyListener(play, playNext, mute, randomButton, loop, shuffleButton, Preferences.userRoot().get("MusicPlayerPlay1", "56-29-25"), Preferences.userRoot().get("MusicPlayerPlay2", "-1"), Preferences.userRoot().get("MusicPlayerPlayNext1", "56-29-49"), Preferences.userRoot().get("MusicPlayerPlayNext2", "-1"), Preferences.userRoot().get("MusicPlayerMute1", "56-29-50"), Preferences.userRoot().get("MusicPlayerMute2", "-1"), Preferences.userRoot().get("MusicPlayerRandom1", "56-29-19"), Preferences.userRoot().get("MusicPlayerRandom2", "-1"), Preferences.userRoot().get("MusicPlayerLoop1", "56-29-38"), Preferences.userRoot().get("MusicPlayerLoop2", "-1"), Preferences.userRoot().get("MusicPlayerShuffle1", "56-29-31"), Preferences.userRoot().get("MusicPlayerShuffle2", "-1"));
        GlobalScreen.addNativeKeyListener(globalKeyListener);

        settings.setOnAction(e -> {
            Stage settingsStage = new Stage();
            settingsStage.setResizable(false);
            settingsStage.setWidth(300);
            settingsStage.setHeight(250);
            Pane settingsPane = new Pane();
            Scene settingsScene = new Scene(settingsPane, 300, 250);

            Label playSetting = ComponentCreator.createLabel(10, 10, "Play");
            Button play1Setting = ComponentCreator.createButton(80, 5, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlay1Key()));
            Button play2Setting = ComponentCreator.createButton(180, 5, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlay2Key()));

            Label playNextSetting = ComponentCreator.createLabel(10, 40, "Play next");
            Button playNext1Setting = ComponentCreator.createButton(80, 35, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlayNext1Key()));
            Button playNext2Setting = ComponentCreator.createButton(180, 35, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlayNext2Key()));

            Label muteSetting = ComponentCreator.createLabel(10, 70, "Mute");
            Button mute1Setting = ComponentCreator.createButton(80, 65, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getMute1Key()));
            Button mute2Setting = ComponentCreator.createButton(180, 65, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getMute2Key()));

            Label randomSetting = ComponentCreator.createLabel(10, 100, "Random");
            Button random1Setting = ComponentCreator.createButton(80, 95, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getRandom1Key()));
            Button random2Setting = ComponentCreator.createButton(180, 95, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getRandom2Key()));

            Label loopSetting = ComponentCreator.createLabel(10, 130, "Loop");
            Button loop1Setting = ComponentCreator.createButton(80, 125, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getLoop1Key()));
            Button loop2Setting = ComponentCreator.createButton(180, 125, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getLoop2Key()));

            Label shuffleSetting = ComponentCreator.createLabel(10, 160, "Shuffle");
            Button shuffle1Setting = ComponentCreator.createButton(80, 155, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getShuffle1Key()));
            Button shuffle2Setting = ComponentCreator.createButton(180, 155, 100, 30, globalKeyListener.getKeyCombinationAsText(globalKeyListener.getShuffle2Key()));

            play1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    play1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setPlay1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerPlay1", lastKey);
                            play1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setPlay1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerPlay1", lastKey);
                    play1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            play2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    play2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setPlay2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerPlay2", lastKey);
                            play2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setPlay2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerPlay2", lastKey);
                    play2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            playNext1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    playNext1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setPlayNext1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerPlayNext1", lastKey);
                            playNext1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setPlayNext1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerPlayNext1", lastKey);
                    playNext1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            playNext2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    playNext2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setPlayNext2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerPlayNext2", lastKey);
                            playNext2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setPlayNext2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerPlayNext2", lastKey);
                    playNext2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            mute1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    mute1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setMute1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerMute1", lastKey);
                            mute1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setMute1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerMute1", lastKey);
                    mute1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            mute2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    mute2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setMute2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerMute2", lastKey);
                            mute2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setMute2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerMute2", lastKey);
                    mute2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            random1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    random1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setRandom1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerRandom1", lastKey);
                            random1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setRandom1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerRandom1", lastKey);
                    random1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            random2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    random2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setRandom2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerRandom2", lastKey);
                            random2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setRandom2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerRandom2", lastKey);
                    random2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            loop1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    loop1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setLoop1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerLoop1", lastKey);
                            loop1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setLoop1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerLoop1", lastKey);
                    loop1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            loop2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    loop2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setLoop2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerLoop2", lastKey);
                            loop2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setLoop2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerLoop2", lastKey);
                    loop2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            shuffle1Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    shuffle1Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setShuffle1Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerShuffle1", lastKey);
                            shuffle1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setShuffle1Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerShuffle1", lastKey);
                    shuffle1Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            shuffle2Setting.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    shuffle2Setting.setText("");
                    settingsScene.setOnKeyPressed(key -> {
                        if (key.getCode() != KeyCode.CONTROL && key.getCode() != KeyCode.ALT && key.getCode() != KeyCode.ALT_GRAPH && key.getCode() != KeyCode.CAPS && key.getCode() != KeyCode.SHIFT) {
                            String lastKey = globalKeyListener.getLastKeyCombination();
                            globalKeyListener.setShuffle2Key(lastKey);
                            Preferences.userRoot().put("MusicPlayerShuffle2", lastKey);
                            shuffle2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                            settingsScene.setOnKeyPressed(k -> {
                            });
                        }
                    });
                } else if (ev.getButton() == MouseButton.SECONDARY) {
                    String lastKey = "-1";
                    globalKeyListener.setShuffle2Key(lastKey);
                    Preferences.userRoot().put("MusicPlayerShuffle2", lastKey);
                    shuffle2Setting.setText(globalKeyListener.getKeyCombinationAsText(lastKey));
                }

            });

            settingsScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (play1Setting.getText().equals("")) {
                        play1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlay1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (play2Setting.getText().equals("")) {
                        play2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlay2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (playNext1Setting.getText().equals("")) {
                        playNext1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlayNext1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (playNext2Setting.getText().equals("")) {
                        playNext2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getPlayNext2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (mute1Setting.getText().equals("")) {
                        mute1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getMute1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (mute2Setting.getText().equals("")) {
                        mute2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getMute2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (random1Setting.getText().equals("")) {
                        random1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getRandom1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (random2Setting.getText().equals("")) {
                        random2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getRandom2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (loop1Setting.getText().equals("")) {
                        loop1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getLoop1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (loop2Setting.getText().equals("")) {
                        loop2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getLoop2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (shuffle1Setting.getText().equals("")) {
                        shuffle1Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getShuffle1Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                    if (shuffle2Setting.getText().equals("")) {
                        shuffle2Setting.setText(globalKeyListener.getKeyCombinationAsText(globalKeyListener.getShuffle2Key()));
                        settingsScene.setOnKeyPressed(k -> {
                        });
                    }
                }
            });

            settingsPane.getChildren().addAll(playSetting, play1Setting, play2Setting, playNextSetting, playNext1Setting, playNext2Setting, muteSetting, mute1Setting, mute2Setting, randomSetting, random1Setting, random2Setting, loopSetting, loop1Setting, loop2Setting, shuffleSetting, shuffle1Setting, shuffle2Setting);
            settingsStage.setTitle("MusicPlayer hotkeys");
            settingsStage.setScene(settingsScene);
            settingsStage.show();
        });
        primaryStage.setResizable(false);
        pan.getChildren().addAll(play, listView, currentSongLabel, nextSongLabel, settings, randomButton, loop, playNext, left, middle, right, volumeLabel, speedLabel, volumeSlider, speedSlider, timeBar, timeLabel, chooseFolder, mute, shuffleButton);
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
}
