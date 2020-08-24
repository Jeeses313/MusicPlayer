package musicplayer;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

    private Button playButton;
    private Button nextButton;
    private Button muteButton;
    private Button randomButton;
    private Button loopButton;
    private Button shuffleButton;
    private boolean altPressed;
    private boolean ctrlPressed;
    private String lastKeyCombination;
    private String play1Key;
    private String play2Key;
    private String playNext1Key;
    private String playNext2Key;
    private String mute1Key;
    private String mute2Key;
    private String random1Key;
    private String random2Key;
    private String loop1Key;
    private String loop2Key;
    private String shuffle1Key;
    private String shuffle2Key;

    public GlobalKeyListener(Button playButton, Button nextButton, Button muteButton, Button randomButton, Button loopButton, Button shuffleButton, String play1Key, String play2Key, String playNext1Key, String playNext2Key, String mute1Key, String mute2Key, String random1Key, String random2Key, String loop1Key, String loop2Key, String shuffle1Key, String shuffle2Key) {
        this.playButton = playButton;
        this.nextButton = nextButton;
        this.muteButton = muteButton;
        this.randomButton = randomButton;
        this.loopButton = loopButton;
        this.shuffleButton = shuffleButton;
        this.play1Key = play1Key;
        this.play2Key = play2Key;
        this.playNext1Key = playNext1Key;
        this.playNext2Key = playNext2Key;
        this.mute1Key = mute1Key;
        this.mute2Key = mute2Key;
        this.random1Key = random1Key;
        this.random2Key = random2Key;
        this.loop1Key = loop1Key;
        this.loop2Key = loop2Key;
        this.shuffle1Key = shuffle1Key;
        this.shuffle2Key = shuffle2Key;
        this.altPressed = false;
        this.ctrlPressed = false;
        lastKeyCombination = "0";
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nke) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nke) {
        lastKeyCombination = "";
        if (altPressed) {
            lastKeyCombination += NativeKeyEvent.VC_ALT + "-";
        }
        if (ctrlPressed) {
            lastKeyCombination += NativeKeyEvent.VC_CONTROL + "-";
        }
        lastKeyCombination += nke.getKeyCode();
        Platform.runLater(() -> {
            if (nke.getKeyCode() == NativeKeyEvent.VC_ALT) {
                altPressed = true;
                return;
            }
            if (nke.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
                ctrlPressed = true;
                return;
            }
            if (lastKeyCombination.equals(play1Key) || lastKeyCombination.equals(play2Key)) {
                playButton.fire();
                return;
            }
            if (lastKeyCombination.equals(mute1Key) || lastKeyCombination.equals(mute2Key)) {
                muteButton.fire();
                return;
            }
            if (lastKeyCombination.equals(playNext1Key) || lastKeyCombination.equals(playNext2Key)) {
                nextButton.fire();
                return;
            }
            if (lastKeyCombination.equals(shuffle1Key) || lastKeyCombination.equals(shuffle2Key)) {
                shuffleButton.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 2, 3, 4, MouseButton.PRIMARY, 5, true, true, true, true, true, true, true, true, true, true, null));
                return;
            }
            if (lastKeyCombination.equals(loop1Key) || lastKeyCombination.equals(loop2Key)) {
                loopButton.fire();
                return;
            }
            if (lastKeyCombination.equals(random1Key) || lastKeyCombination.equals(random2Key)) {
                randomButton.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 2, 3, 4, MouseButton.PRIMARY, 5, true, true, true, true, true, true, true, true, true, true, null));
            }
        });
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nke) {
        Platform.runLater(() -> {
            if (nke.getKeyCode() == NativeKeyEvent.VC_ALT) {
                altPressed = false;
                return;
            }
            if (nke.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
                ctrlPressed = false;
            }
        });
    }

    public String getLastKeyCombination() {
        return lastKeyCombination;
    }

    public String getKeyCombinationAsText(String keyCombination) {
        try {
            String keyCombinationText = "";
            String[] keys = keyCombination.split("-");
            int i = 0;
            while (i < keys.length - 1) {
                keyCombinationText += NativeKeyEvent.getKeyText(Integer.parseInt(keys[i])) + "+";
                i++;
            }
            keyCombinationText += NativeKeyEvent.getKeyText(Integer.parseInt(keys[i]));
            return keyCombinationText;
        } catch (Exception e) {
            return "";
        }
    }

    public String getPlay1Key() {
        return play1Key;
    }

    public void setPlay1Key(String play1Key) {
        this.play1Key = play1Key;
    }

    public String getPlay2Key() {
        return play2Key;
    }

    public void setPlay2Key(String play2Key) {
        this.play2Key = play2Key;
    }

    public String getPlayNext1Key() {
        return playNext1Key;
    }

    public void setPlayNext1Key(String playNext1Key) {
        this.playNext1Key = playNext1Key;
    }

    public String getPlayNext2Key() {
        return playNext2Key;
    }

    public void setPlayNext2Key(String playNext2Key) {
        this.playNext2Key = playNext2Key;
    }

    public String getMute1Key() {
        return mute1Key;
    }

    public void setMute1Key(String mute1Key) {
        this.mute1Key = mute1Key;
    }

    public String getMute2Key() {
        return mute2Key;
    }

    public void setMute2Key(String mute2Key) {
        this.mute2Key = mute2Key;
    }

    public String getRandom1Key() {
        return random1Key;
    }

    public void setRandom1Key(String random1Key) {
        this.random1Key = random1Key;
    }

    public String getRandom2Key() {
        return random2Key;
    }

    public void setRandom2Key(String random2Key) {
        this.random2Key = random2Key;
    }

    public String getLoop1Key() {
        return loop1Key;
    }

    public void setLoop1Key(String loop1Key) {
        this.loop1Key = loop1Key;
    }

    public String getLoop2Key() {
        return loop2Key;
    }

    public void setLoop2Key(String loop2Key) {
        this.loop2Key = loop2Key;
    }

    public String getShuffle1Key() {
        return shuffle1Key;
    }

    public void setShuffle1Key(String shuffle1Key) {
        this.shuffle1Key = shuffle1Key;
    }

    public String getShuffle2Key() {
        return shuffle2Key;
    }

    public void setShuffle2Key(String shuffle2Key) {
        this.shuffle2Key = shuffle2Key;
    }

}
