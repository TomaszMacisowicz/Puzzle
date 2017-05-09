package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Puzzle extends Application {

    private Timeline timeline;

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
//zdjęcie do puzzli
        Image image = new Image(getClass().getResourceAsStream("pic.jpg"));
        int numOfColumns = (int) (image.getWidth() / Piece.SIZE);
        int numOfRows = (int) (image.getHeight() / Piece.SIZE);
//    tapeta
        final Desk desk = new Desk(numOfColumns, numOfRows);
//    elementy puzzli
        final List<Piece> pieces = new ArrayList<Piece>();
        for (int col = 0; col < numOfColumns; col++) {
            for (int row = 0; row < numOfRows; row++) {
                int x = col * Piece.SIZE;
                int y = row * Piece.SIZE;
                final Piece piece = new Piece(image, x, y, row > 0, col > 0, row < numOfRows - 1, col < numOfColumns - 1, desk.getWidth(), desk.getHeight());
                pieces.add(piece);
            }
        }
        desk.getChildren().addAll(pieces);

// przycisk
        Button shuffleButton = new Button("Shuffle");
        shuffleButton.setStyle("-fx-font-size: 2em;");
        shuffleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (timeline != null) {
                    timeline.stop();
                }
                timeline = new Timeline();
                for (final Piece piece : pieces) {
                    piece.setActive();
                    double shuffleX = Math.random()
                            * (desk.getWidth() - Piece.SIZE + 48f)
                            - 24f - piece.getCorrectX();
                    double shuffleY = Math.random()
                            * (desk.getHeight() - Piece.SIZE + 30f)
                            - 15f - piece.getCorrectY();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(1),
                                    new KeyValue(piece.translateXProperty(), shuffleX),
                                    new KeyValue(piece.translateYProperty(), shuffleY)));
                }
                timeline.playFromStart();
            }
        });
        Button solveButton = new Button("Solve");
        solveButton.setStyle("-fx-font-size: 2em;");
        solveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (timeline != null) {
                    timeline.stop();
                }
                timeline = new Timeline();
                for (final Piece piece : pieces) {
                    piece.setInactive();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(1),
                                    new KeyValue(piece.translateXProperty(), 0),
                                    new KeyValue(piece.translateYProperty(), 0)));
                }
                timeline.playFromStart();
            }
        });
        HBox buttonBox = new HBox(8);
        buttonBox.getChildren().addAll(shuffleButton, solveButton);
        // vbox dla tapety i przcisku
        VBox vb = new VBox(10);
        vb.getChildren().addAll(desk, buttonBox);
        root.getChildren().addAll(vb);
    }

}

public static class Desk extends Pane {

    Desk(int numOfColumns, int numOfRows) {
        setStyle("-fx-background-color: #cccccc; "
                + "-fx-border-color: #464646; "
                + "-fx-effect: innershadow( two-pass-box , rgba(0,0,0,0.8) , 15, 0.0 , 0 , 4 );");
        double DESK_WIDTH = Piece.SIZE * numOfColumns;
        double DESK_HEIGHT = Piece.SIZE * numOfRows;
        setPrefSize(DESK_WIDTH, DESK_HEIGHT);
        setMaxSize(DESK_WIDTH, DESK_HEIGHT);
        autosize();

        Path grid = new Path();
        grid.setStroke(Color.rgb(70, 70, 70));
        getChildren().add(grid);
        for (int col = 0; col < numOfColumns - 1; col++) {
            grid.getElements().addAll(
                    new MoveTo(Piece.SIZE + Piece.SIZE * col, 5),
                    new LineTo(Piece.SIZE + Piece.SIZE * col, Piece.SIZE * numOfRows - 5)
            );
        }

        for (int row = 0; row < numOfRows - 1; row++) {
            grid.getElements().addAll(
                    new MoveTo(5, Piece.SIZE + Piece.SIZE * row),
                    new LineTo(Piece.SIZE * numOfColumns - 5, Piece.SIZE + Piece.SIZE * row)
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
