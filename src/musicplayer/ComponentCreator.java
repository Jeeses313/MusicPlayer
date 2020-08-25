package musicplayer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class ComponentCreator {

    public static Label createLabel(int x, int y, String text) {
        Label newLabel = new Label(text);
        newLabel.setTranslateX(x);
        newLabel.setTranslateY(y);
        return newLabel;
    }

    public static Button createButton(int x, int y, int width, int height, String text) {
        Button newButton = new Button(text);
        newButton.setTranslateX(x);
        newButton.setTranslateY(y);
        newButton.setPrefSize(width, height);
        newButton.styleProperty().setValue("-fx-focus-color: grey; -fx-faint-focus-color: transparent;");
        return newButton;
    }

    public static RadioButton createRadioButton(int x, int y, int width, int height, String text) {
        RadioButton newButton = new RadioButton(text);
        newButton.setTranslateX(x);
        newButton.setTranslateY(y);
        newButton.getStyleClass().remove("radio-button");
        newButton.getStyleClass().add("toggle-button");
        newButton.setPrefSize(width, height);
        newButton.styleProperty().setValue("-fx-focus-color: grey; -fx-faint-focus-color: transparent;");
        return newButton;
    }

    public static Rectangle createRectangle(int x, int y, int width, int height) {
        Rectangle newRectangle = new Rectangle(width, height);
        newRectangle.setTranslateX(x);
        newRectangle.setTranslateY(y);
        return newRectangle;
    }

    public static Polygon createTriangle(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3) {
        Polygon newTriangle = new Polygon(x1, y1, x2, y2, x3, y3);
        newTriangle.setTranslateX(x);
        newTriangle.setTranslateY(y);
        return newTriangle;
    }

    public static Slider createSlider(double min, double max, double value, double major, int x, int y, int width, int height) {
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

    public static ProgressBar createProgressBar(int x, int y, int width, int height) {
        ProgressBar newProgressBar = new ProgressBar(0);
        newProgressBar.setTranslateX(x);
        newProgressBar.setTranslateY(y);
        newProgressBar.setPrefSize(width, height);
        newProgressBar.setStyle("-fx-accent: #A9A9A9;");
        return newProgressBar;
    }
}
