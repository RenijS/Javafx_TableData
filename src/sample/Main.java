package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    ObservableList<MusicAlbum> tableData;
    TableView <MusicAlbum> tableView;
    TableColumn<MusicAlbum, String> idCol;
    TableColumn<MusicAlbum, String> nameCol;
    TableColumn<MusicAlbum, String> genreCol;
    TableColumn<MusicAlbum, Boolean> compilationCol;
    TableColumn<MusicAlbum, Integer> tracksCol;

    MusicCatalogDS musicCatalogDS =new MusicCatalogDS();


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Table");
        build(primaryStage);
        primaryStage.show();

        Thread.currentThread().setUncaughtExceptionHandler((thread, exception) ->
        {
            System.out.println("ERROR: " + exception);
        });
    }

    private void build(Stage stage) {

        try {
            tableView = makeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HBox filterHB = makeFilter();
        HBox sorterHB = makeSorter();
        HBox editorHB = makeEditor();
        HBox saveHB = saveHB();

        VBox root = new VBox();
        root.getChildren().addAll(saveHB,tableView, filterHB, sorterHB, editorHB);

        Scene scene = new Scene(root, 400, 500);

        stage.setScene(scene);
    }

    private HBox saveHB() {
        Button saveBT = new Button("Save Data");
        HBox saveHB = new HBox(saveBT);
        saveHB.setStyle("-fx-alignment:top-right");
        return saveHB;
    }

    private HBox makeEditor() {
        Button addBT = new Button("Add Button");
        Button deleteBT = new Button("Delete Selected Album");

        HBox editorHB = new HBox();
        editorHB.getChildren().addAll(addBT, deleteBT);
        editorHB.setStyle("-fx-alignment: center");

        addBT.setOnAction(event -> addAlbum());
        deleteBT.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Really Delete?");
            alert.setContentText("Are you sure you want to delete this album?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
            {
                MusicAlbum album = tableView.getSelectionModel().getSelectedItem();
                musicCatalogDS.remove(album.getId());
                tableData.remove(album);
            }
        });

        return editorHB;
    }

    private void addAlbum() {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Album");

        Label nameLB = new Label("Name:");
        TextField nameTF = new TextField();
        nameTF.setMinWidth(200);

        HBox h1 = new HBox(nameLB, nameTF);

        Label genreLB = new Label("Genre:");
        TextField genreTF = new TextField();
        nameTF.setMinWidth(200);

        HBox h2 = new HBox(genreLB, genreTF);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(false);
        Label compilationLB = new Label("Compilation?");

        HBox h3 = new HBox(checkBox, compilationLB);

        Label trackLB = new Label("Track Count:");
        TextField trackTF = new TextField();
        trackTF.setMinWidth(200);

        HBox h4 = new HBox(trackLB, trackTF);

        Button okBT = new Button("OK");
        Button cancelBT = new Button("Cancel");

        cancelBT.setOnAction(event -> addStage.close());

        okBT.setOnAction(event ->
                {
                    try {
                        musicCatalogDS.add(new MusicAlbum(musicCatalogDS.generateId(),nameTF.getText(), genreTF.getText(), checkBox.isSelected(), Integer.parseInt(trackTF.getText().trim())));
                        tableData.add(new MusicAlbum(musicCatalogDS.generateId(),nameTF.getText(), genreTF.getText(), checkBox.isSelected(), Integer.parseInt(trackTF.getText().trim())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        HBox h5 = new HBox(okBT, cancelBT);

        VBox root = new VBox(h1, h2, h3, h4, h5);

        Scene scene = new Scene(root, 300, 150);

        addStage.setScene(scene);
        addStage.show();
    }

    private HBox makeSorter() {
        Button sortBT = new Button("Sort by Genre and Track Count");
        Button restoreBT = new Button("Restore Order");

        sortBT.setOnAction((e) ->
        {
            genreCol.setSortType(TableColumn.SortType.ASCENDING);
            tracksCol.setSortType(TableColumn.SortType.DESCENDING);
            tableView.getSortOrder().clear();
            tableView.getSortOrder().add(genreCol);
            tableView.getSortOrder().add(tracksCol);
        });
        restoreBT.setOnAction((e) -> tableView.getSortOrder().clear());

        HBox sorterHB = new HBox();
        sorterHB.getChildren().addAll(sortBT, restoreBT);
        sorterHB.setStyle("-fx-alignment: center");

        return sorterHB;
    }

    private HBox makeFilter() {
        Label filterLB = new Label("Filter:");
        TextField filterTF = new TextField();
        filterTF.setMaxWidth(300);

        // wrap the table data in a filtered list
        FilteredList<MusicAlbum> filteredList = new FilteredList<>(tableData, p -> true);

        // wrap the filtered list in a sorted list
        SortedList<MusicAlbum> sortedList = new SortedList<>(filteredList);

        // bind the comparator of the sorted list to that of the table
        // ( so when the later changes , the former changes as well )
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        // create the filter text field and add a change listener
        // The change listener defines predicate to determine if a table row should be displayed or not
        filterTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(MusicAlbum ->{
                if (newValue == null || newValue.isEmpty())
                {
                    return true; // true -> display the row
                }
                //else
                String filterString = newValue.toUpperCase();
                // do not display
                return MusicAlbum.getName().toUpperCase().contains(filterString)
                        || MusicAlbum.getGenre().toUpperCase().contains(filterString);
            });
        });

        HBox filterHB = new HBox(filterLB, filterTF);
        filterHB.setStyle("-fx-alignment: center");
        return filterHB;
    }

    private TableView<MusicAlbum> makeTable() throws Exception {
        tableData = FXCollections.observableArrayList();

        // Define the table columns
        idCol = new TableColumn<>("Id");
        nameCol = new TableColumn<>("Name");
        genreCol = new TableColumn<>("Genre");
        compilationCol = new TableColumn<>("isCompilation");
        tracksCol = new TableColumn<>("# Tracks");

        // For each column , we must set the cell value factory inorder to display the column’s data
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        compilationCol.setCellValueFactory(new PropertyValueFactory<>("compilation"));
        tracksCol.setCellValueFactory(new PropertyValueFactory<>("trackCount"));

        // add columns to view
        TableView<MusicAlbum> tableView2 = new TableView<>();
        tableView2.getColumns().add(idCol);
        tableView2.getColumns().add(nameCol);
        tableView2.getColumns().add(genreCol);
        tableView2.getColumns().add(compilationCol);
        tableView2.getColumns().add(tracksCol);

        //Temporary data
        MusicAlbum m1 = new MusicAlbum(musicCatalogDS.generateId(),"Unplugged(Alice in Chains)", "Alternative Rock", true, 13);
        musicCatalogDS.add(m1);
        tableData.add(m1);

        MusicAlbum m2 = new MusicAlbum(musicCatalogDS.generateId(), "Californication", "Funk Rock", false, 15);
        musicCatalogDS.add(m2);
        tableData.add(m2);

        MusicAlbum m3 = new MusicAlbum(musicCatalogDS.generateId(), "In Between Dreams", "Acoustic Rock", false, 14);
        musicCatalogDS.add(m3);
        tableData.add(m3);

        MusicAlbum m4 = new MusicAlbum(musicCatalogDS.generateId(), "The Dark Side of the Moon", "Progressive Rock", false, 10);
        musicCatalogDS.add(m4);
        tableData.add(m4);

        // Set data items of the view
        tableView2.setItems(tableData);
        return tableView2;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
