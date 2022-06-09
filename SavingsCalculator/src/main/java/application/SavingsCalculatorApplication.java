package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class SavingsCalculatorApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //the components of the application can be managed using a borderpane
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20, 40, 20, 40));
        //in the middle of the borderpane add a linechart
        NumberAxis xAxis = new NumberAxis(0, 30, 4);
        NumberAxis yAxis = new NumberAxis(0, 125000, 2500);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Savings calculator");
        lineChart.setMinSize(150, 150);

        layout.setCenter(lineChart);

        //on the top of the borderpane add a vbox component
        //which contains two borderpanes 
        VBox vBox = new VBox();
        BorderPane slider1Pane = new BorderPane();
        BorderPane slider2Pane = new BorderPane();
        vBox.getChildren().addAll(slider1Pane, slider2Pane);

        //the first Borderpane contains monthly savings text, slider, and text
        Label monthly = new Label("Monthly savings");
        Slider slider1 = new Slider(25, 250, 25);
        Label slider1Label = new Label("" + slider1.getValue());

        //adding functionality to the slider
        slider1.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                slider1Label.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        }
        );

        slider1Pane.setLeft(monthly);

        slider1Pane.setCenter(slider1);

        slider1Pane.setRight(slider1Label);

        //the second borderpane contains yearly interest text, slider, text
        Label yearly = new Label("Yearly interest rate");
        Slider slider2 = new Slider(0, 10, 0);
        Label slider2Label = new Label("" + slider2.getValue());

        //adding functionality to the slider
        slider2.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                slider2Label.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        }
        );

        slider2Pane.setLeft(yearly);

        slider2Pane.setCenter(slider2);

        slider2Pane.setRight(slider2Label);

        layout.setTop(vBox);

        //implementing the functionality
        //displaying the savings 
        //calculating the savings and storing in a hashmap
        HashMap<Integer, Double> savings = new HashMap<>();

        for (int i = 0;
                i <= 30; i++) {
            savings.put(i, i * slider1.getValue() * 12);
        }

        //monthly savings 
        //adding the hashmap to the linechart
        XYChart.Series data = new XYChart.Series();

        data.setName(
                "Savings");
        savings.entrySet()
                .stream().forEach(pair -> {
                    data.getData().add(new XYChart.Data(pair.getKey(), pair.getValue()));
                }
                );

        lineChart.getData()
                .add(data);

        //montly savings plus interest rate 
        HashMap<Integer, Double> interest = new HashMap<>();
        double sum = 0;
        for (int i = 0; i <= 30; i++) {

            double sumYear = sum + ( slider1.getValue() * 12);
            double interestYear = sumYear * (slider2.getValue() / 100);
            sum = sumYear + interestYear;
            savings.put(i, sum);
        }

        XYChart.Series data2 = new XYChart.Series();

        data.setName(
                "Savings plus interest");
        interest.entrySet()
                .stream().forEach(pair -> {
                    data2.getData().add(new XYChart.Data(pair.getKey(), pair.getValue()));
                }
                );

        lineChart.getData()
                .add(data2);

        //slider functionality 
        slider1.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                updateChart(slider1.getValue(), data);
                updateChartInterest(slider1.getValue(), slider2.getValue(), data2);
            }
        });

        slider2.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                updateChart(slider1.getValue(), data);
                updateChartInterest(slider1.getValue(), slider2.getValue(), data2);
            }
        });
        
        System.out.println("slider1 getchildrenunmodifiable: " + slider1.getChildrenUnmodifiable());

        Scene scn = new Scene(layout, 800, 800);

        stage.setScene(scn);

        stage.show();
    }

    public static void main(String[] args) {
        launch(SavingsCalculatorApplication.class
        );
        System.out.println(
                "Hello world!");
    }

    private void updateChart(double value, XYChart.Series data) {
        data.getData().clear();
        HashMap<Integer, Double> savings = new HashMap<>();
        for (int i = 0;
                i <= 30; i++) {
            savings.put(i, i * value * 12);
        }

        data.setName(
                "Savings");
        savings.entrySet()
                .stream().forEach(pair -> {
                    data.getData().add(new XYChart.Data(pair.getKey(), pair.getValue()));
                }
                );

    }

    private void updateChartInterest(double slider1, double slider2, XYChart.Series data) {
        data.getData().clear();
        HashMap<Integer, Double> interest = new HashMap<>();
        double sum = 0;
        for (int i = 0;
                i <= 30; i++) {
            if( i == 0) {
               interest.put(i, 0.0); 
               continue;
            }
            double sumYear = sum + ( slider1 * 12);
            double interestYear = sumYear * (slider2 / 100);
            sum = sumYear + interestYear;
            interest.put(i, sum); 
        }

        data.setName(
                "Savings plus interest");
        interest.entrySet()
                .stream().forEach(pair -> {
                    data.getData().add(new XYChart.Data(pair.getKey(), pair.getValue()));
                }
                );

    }

}
