package fr.amu.iut.cc3;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ToileController implements Initializable {

    private static int rayonCercleExterieur = 200;
    private static int angleEnDegre = 60;
    private static int angleDepart = 90;
    private static int noteMaximale = 20;
    private boolean tracerAppuyer = false;

    @FXML
    private Pane toile;

    @FXML
    private GridPane noteGridPane;

    @FXML
    private TextField comp1;

    @FXML
    private TextField comp2;

    @FXML
    private TextField comp3;

    @FXML
    private TextField comp4;

    @FXML
    private TextField comp5;

    @FXML
    private TextField comp6;

    @FXML
    private VBox errorBox;

    private ChangeListener<Number> pointChange;

    private ArrayList<TextField> listeCompTF;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listeCompTF = new ArrayList<>(Arrays.asList(comp1, comp2, comp3, comp4, comp5, comp6));

        pointChange = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number aDouble, Number t1) {
                for(Circle point : getListePoint())
                    if(!point.isVisible()) return;
                resetLigne();
                if(tracerAppuyer) tracerLigne();
            }
        };

        createAllCircle();

    }

    // Creation individuelle d'un point avec ses bindings et ses eventhandlers
    public void creationPoint(TextField textField, int numTF){
        Circle circle = new Circle(5);
        IntegerProperty noteProperty = new SimpleIntegerProperty(0);

        // Verifie si la note entrée est valide si elle ne l'est pas -> box erreur
        BooleanBinding noteValide = new BooleanBinding() {
            {
                this.bind(textField.textProperty(), noteProperty);
            }
            @Override
            protected boolean computeValue() {
                try{
                    int note = Integer.parseInt(textField.getText());
                    if(note > 20 || note < 0){
                        errorBox.setVisible(true);
                        throw new NumberFormatException();
                    }else
                        errorBox.setVisible(false);
                    noteProperty.setValue(note);
                    return true;
                }catch (NumberFormatException e){
                    return false;
                }
            }
        };

        //Determine le centre X
        IntegerBinding xCenter = new IntegerBinding() {
            {
                this.bind(noteProperty);
            }
            @Override
            protected int computeValue() {
                return getXRadarChart(noteProperty.doubleValue(), numTF+1);
            }
        };

        //Determine le centre Y
        IntegerBinding yCenter = new IntegerBinding() {
            {
                this.bind(noteProperty);
            }
            @Override
            protected int computeValue() {
                return getYRadarChart(noteProperty.doubleValue(), numTF+1);
            }
        };

        circle.centerXProperty().bind(xCenter);
        circle.centerYProperty().bind(yCenter);
        circle.visibleProperty().bind(noteValide);
        circle.centerYProperty().addListener(pointChange);
        circle.centerXProperty().addListener(pointChange);
        toile.getChildren().add(circle);

    }

    int getXRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur + Math.cos(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

    int getYRadarChart(double value, int axe ){
        return (int) (rayonCercleExterieur - Math.sin(Math.toRadians(angleDepart - (axe-1)  * angleEnDegre)) * rayonCercleExterieur
                *  (value / noteMaximale));
    }

    // Remet le graphe à 0 en enlevant les points les lignes et en vidant les texfields
    public void resetGraph(){
        for(Node node : noteGridPane.getChildren()){ // Reset les texfields
            if(node instanceof TextField)
                ((TextField) node).setText("");
        }
        toile.getChildren().clear();
        errorBox.setVisible(false);
        tracerAppuyer = false;
        createAllCircle(); // Recrée les points
    }

    // Créer les points avec leur bindings et Eventhandlers
    public void createAllCircle(){
        for(int i = 0; i < listeCompTF.size(); ++i)
            creationPoint(listeCompTF.get(i), i);
    }

    // Recupère la liste des points
    public ArrayList<Circle> getListePoint(){
        ArrayList<Circle> listePoints = new ArrayList<>();
        for(Node node : toile.getChildren())
            if(node instanceof Circle) listePoints.add((Circle) node);

        return listePoints;
    }

    // Supprime tout les éléments et recrée les points pour supprimer les lignes
    public void resetLigne(){
        toile.getChildren().clear();
        createAllCircle();
    }

    // Trace les lignes entre chaque point du graphique
    public void tracerLigne(){
        ArrayList<Circle> listePoint = getListePoint();
        if(!tracerAppuyer) tracerAppuyer = true;
        for(int i = 0; i < listePoint.size(); i++){
            Line line = new Line();
            line.setStartX(listePoint.get(i).getCenterX());
            line.setStartY(listePoint.get(i).getCenterY());
            line.setEndX(listePoint.get((i+1)%listePoint.size()).getCenterX());
            line.setEndY(listePoint.get((i+1)%listePoint.size()).getCenterY());
            line.setStyle("-fx-stroke: black");
            toile.getChildren().add(line);
        }
    }

}
