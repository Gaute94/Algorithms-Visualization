package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Stack;

public class Main extends Application {


    private static final int
            BAR_COUNT = 15,
            MAX_BAR_HEIGHT = 50;

    private static final String
            COLOR_ACTIVE = "-fx-bar-fill: #f64",
            COLOR_INITIAL = "-fx-bar-fill: #888",
            COLOR_FINALIZED = "-fx-bar-fill: #03fc13",
            COLOR_MIN = "-fx-bar-fill: #3cf";

    private static int
            DELAY_MILLIS = 200;

    private int[] bubble = {7, 4, 2, 14, 15, 10, 8, 3, 6, 13, 12, 5, 11, 1, 9};
    private int[] bubbleReset = {7, 4, 2, 14, 15, 10, 8, 3, 6, 13, 12, 5, 11, 1, 9};
    private int x = 0;
    private int y = 1;
    private int z = 0;

    private Stack<Integer> stack = new Stack<>();

    private int selectionMin = 0;
    private boolean Jfinished = false;

    private ObservableList<XYChart.Data<String, Number>> bars;
    private BarChart<String, Number> chart;
    private FlowPane inputs;

    private Timeline timeline;


    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Algorithm Bar Chart");

        final CategoryAxis xAxis= new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Index");
        yAxis.setLabel("Value");
        final BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        stack.push(0);
        stack.push(bubble.length);
        makeChart(pane);
        makeButtons(pane, bubble);

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();



    }

    private void addPainting(Node newNode, String color){
        System.out.println("In addPainting()");

        if(newNode != null){
            System.out.println("addPainting(), newNode != null");
            newNode.setStyle(color);
        }
    }

    private void makeChart(BorderPane pane) {
        chart = new BarChart<>(new CategoryAxis(), new NumberAxis(0, MAX_BAR_HEIGHT, 0));
        chart.setLegendVisible(false);
        chart.getYAxis().setTickLabelsVisible(false);
        chart.getYAxis().setOpacity(0);
        chart.getXAxis().setTickLabelsVisible(false);
        chart.getXAxis().setOpacity(0);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setAnimated(false);

        bars = FXCollections.observableArrayList();
        chart.getData().add(new XYChart.Series<>(bars));

        setChartData();
        pane.setCenter(chart);
    }

    private void makeButtons(BorderPane pane, int[] a) {
        inputs = new FlowPane();
        inputs.setHgap(5);
        inputs.setVgap(5);
        createButton("Start", () -> startSorting(0));
        createButton("Selection", () -> startSorting(1));
        createButton("Quick", () -> startSorting(2));
//        createButton("x2", () -> setSpeed(100));
//        createButton("x0.5", () -> setSpeed(400));
//        createButton("x1", () -> setSpeed(200));
        createButton("Stop", () -> timeline.stop());
        createButton("reset", () -> reset());
        pane.setBottom(inputs);

    }
    private void reset(){
        timeline.stop();
        for(int i = 0; i < bubble.length; i++){
            bubble[i] = bubbleReset[i];
        }
        bubble = bubbleReset;
        x = 0;
        y = 1;
        setChartData();
        for(int i = 0; i < bars.size(); i++){
            addPainting(bars.get(i).getNode(), COLOR_INITIAL);
        }
    }

    private void setChartData(){
        bars.clear();
        for (int i = 0; i < bubble.length; i++) {

            XYChart.Data<String, Number> dataObject = new XYChart.Data<>(String.valueOf(i), bubble[i]);
            bars.add(dataObject); // node will be present after this
            addPainting(dataObject.getNode(), COLOR_INITIAL); // do this after bars.add
        }
    }

    private void setSpeed(int millis){
        DELAY_MILLIS = millis;
        timeline.stop();
        timeline.play();

    }

    private void startSorting(int a){
        timeline = new Timeline(
                new KeyFrame(Duration.millis(DELAY_MILLIS), e -> {
                    if(a == 0) {
                        bubbleSort();
                    }else if(a == 2){
                        iterativeQsort();
                        //quicksort(0, bubble.length-1);
                    }
                    else{
                        selectionSort();
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void createButton(String label, Runnable method) {
        final Button test = new Button(label);
        test.setOnAction(event -> method.run());
        inputs.getChildren().add(test);
    }

    public void bubbleSort(){

        List<XYChart.Data<String, Number>> list = bars;

        System.out.println("In bubbleSort()");

        int[] a = bubble;
        int n = a.length;

        int temp;
        while(x < list.size()){
            System.out.println("In bubbleSort loop, list.size: " + list.size());

            System.out.println("BubbleSort: i = " + x);
            while(y < list.size()- x){
                System.out.println("BubbleSort: j = " + y);
                if(getValue(list, y -1) > getValue(list, y)){
                    temp = getValue(list, y -1);
                    list.get(y - 1).setYValue(list.get(y).getYValue());

                    list.get(y).setYValue(temp);

                }
                addPainting(bars.get(y -1).getNode(), COLOR_INITIAL);
                addPainting(bars.get(y).getNode(), COLOR_ACTIVE);
                y++;

                break;
            }
            if(y >= list.size()- x){
                addPainting(bars.get(y -1).getNode(), COLOR_FINALIZED);

                y = 1;
                x++;
            }
            break;
        }
    }

    private void selectionSort(){

        List<XYChart.Data<String, Number>> list = bars;

        System.out.println("SelectionSort: list.size = " + list.size());

        while(x < list.size()-1){

            System.out.println("SelectionSort: x: " + x);
            while(y < list.size()){
                if(y -1 != selectionMin && y -1 != x) {
                    addPainting(bars.get(y - 1).getNode(), COLOR_INITIAL);
                }
                addPainting(bars.get(y).getNode(), COLOR_ACTIVE);
                System.out.println("SelectionSort: y: " + y);
                if(getValue(list, y) < getValue(list, selectionMin)){
                    addPainting(bars.get(selectionMin).getNode(), COLOR_INITIAL);
                    selectionMin = y;
                    addPainting(bars.get(selectionMin).getNode(), COLOR_MIN);
                }

                y++;
                break;
            }

            if(y >= list.size()){
                System.out.println("SelectionSort: y >= list.size. BubbleJ: " + y);
                int temp = getValue(list, selectionMin);

                list.get(selectionMin).setYValue(list.get(x).getYValue());

                list.get(x).setYValue(temp);
                addPainting(bars.get(x).getNode(), COLOR_FINALIZED);
                addPainting(bars.get(selectionMin).getNode(), COLOR_INITIAL);
                x++;
                y = x + 1;

                selectionMin = x;
            }
            if(x >= list.size()-1){
                addPainting(bars.get(x).getNode(), COLOR_FINALIZED);
                timeline.stop();
            }
            break;
        }

    }

    private int partition(int low, int high){

        //pivot = a[high] = getValue(list, high);

        //i = x, j = y;

        List<XYChart.Data<String, Number>> list = bars;

        int pivot = getValue(list, high);
        x = (low-1); //index of smaller element
        z = low;
        System.out.println("pivot: " + pivot);
        while(z < high){
            System.out.println("high: " + high);
            System.out.println("z: " + z);
            //If current element is smaller than the pivot
            addPainting(bars.get(z).getNode(), COLOR_ACTIVE);
            //a[j] = getValue(list, j);
            if(getValue(list, z) < pivot){
                System.out.println("x: " + x);
                x++;

                //swap a[i] and [aj]
                //a[i] = getValue(list, i), a[j] = getValue(list, j)
                int temp = getValue(list, x);

                //list.get(i).setYValue(list.get(j).getYValue());
                list.get(x).setYValue(list.get(z).getYValue());

                //list.get(j).setYValue(temp);
                list.get(z).setYValue(temp);
            }
            z++;
            break;
        }

        //swap a[i+1] and a[high] (or pivot)
        int temp = getValue(list, x+1);
        list.get(x+1).setYValue(list.get(high).getYValue());

        list.get(high).setYValue(temp);


        return x+1;
    }

    private void iterativeQsort() {


        while (!stack.isEmpty()) {
            int end = stack.pop()-1;
            int start = stack.pop();
            System.out.println("end: " + end);
            System.out.println("start: " + start);

            if (end - start < 2) {
                continue;
            }
            int p = partition(start, end);
            stack.push(p + 1);
            stack.push(end);
            stack.push(start);
            stack.push(p);
            break;
        }
    }


    private void quicksort(int low, int high){
        System.out.println("lowBefore: " + low + ", highBefore: " + high);
        if(low < high){
            int pi = partition(low, high);
            System.out.println("lowIn: " + low + ", highIn: " + high);

            quicksort(low, pi-1);
            quicksort(pi+1, high);

            System.out.println("lowAfter: " + low + ", highAfter: " + high);

        }else{
            timeline.stop();
        }
    }

    private int getValue(List<XYChart.Data<String, Number>> list, int index){
        return list.get(index).getYValue().intValue();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
