package sample.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import sample.DateElement;
import sample.Main;
import sample.ProgressDialog;
import sample.interfaces.CustomCheckBoxFactory;
import sample.interfaces.CustomEvents;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static javafx.scene.input.KeyCode.*;

public class InitialController extends DefaultController implements CustomEvents {
    @FXML
    Label carregarArquivo;

    @FXML
    Label novoElemento;

    @FXML
    TextField nameTxt;

    @FXML
    TextField dateTxt;

    @FXML
    TextField statusTxt;

    @FXML
    Label lblSelecionados;

    @FXML
    Button btnDeletarSelecionados;

    @FXML
    Button btnSalvar;

    @FXML
    TableView<DateElement> tableContainer;

    @FXML
    TableColumn<DateElement, Boolean> colSelected;

    @FXML
    TableColumn<DateElement, String> colName;

    @FXML
    TableColumn<DateElement, String> colDate;

    @FXML
    TableColumn<DateElement, String> colStatus;

    private Map<Integer, Boolean> selected = new HashMap<>();

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<DateElement> elements;
    private DateElement selectedElement = null;

    private static List<KeyCode> ignoreKeyCodes;

    private void initTable() {
        CustomCheckBoxFactory checkBoxFactory = new CustomCheckBoxFactory(this);

        colSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        colSelected.setCellFactory(checkBoxFactory::checkBoxFactory);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<DateElement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if ("OK".equals(item)) {
                    setTextFill(Color.GREEN);
                    setText(item);
                } else if ("NOT".equals(item)) {
                    setTextFill(Color.BLACK);
                    setText(item);
                } else {
                    final int index = getIndex();
                    ObservableList<DateElement> observableList = getTableView().getItems();
                    if (index >= 0 && index < observableList.size()) {
                        DateElement dateElement = getTableView().getItems().get(index);
                        if (dateElement.getDate() != null) {
                            String dateStr = dateElement.getDate().trim();

                            if (dateStr.contains("-")) {
                                dateStr = dateStr.split("-")[0];
                            }
                            try {
                                Date date = simpleDateFormat.parse(dateStr);
                                Calendar calDate = Calendar.getInstance();
                                calDate.setTime(date);

                                Calendar calnow = Calendar.getInstance();
                                long diffMillis = calnow.getTimeInMillis() - calDate.getTimeInMillis();

                                LocalDate ini = LocalDate.of(calnow.get(Calendar.YEAR), calnow.get(Calendar.MONTH), calnow.get(Calendar.DAY_OF_MONTH));
                                LocalDate end = LocalDate.of(calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH), calDate.get(Calendar.DAY_OF_MONTH));


                                int days = Period.between(ini, end).getDays();
                                if (days < 0) {
                                    setText("ATRASADO");
                                    setTextFill(Color.RED);
                                } else if (days > 0 && days <= 7) {
                                    setText("URGENTE");
                                    setTextFill(Color.ORANGE);
                                } else {
                                    setText("PENDENTE");
                                    setTextFill(Color.BLUE);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }


            }
        });


        tableContainer.getItems().forEach(dateElement -> {
            dateElement.setSelected(true);
            return;
        });
        tableContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DateElement>() {
            @Override
            public void changed(ObservableValue<? extends DateElement> observable, DateElement
                    oldValue, DateElement newValue) {
                if (newValue != null) {
                    selectedElement = newValue;
                    nameTxt.setText(newValue.getName());
                    dateTxt.setText(newValue.getDate());
                    statusTxt.setText(newValue.getStatus());

                    if (newValue.getSelected() == null) {
                        observable.getValue().setSelected(true);
                    } else {
                        if (!newValue.getSelected()) {
                            observable.getValue().setSelected(true);
                        } else {
                            observable.getValue().setSelected(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        ignoreKeyCodes = new ArrayList<>();
        lblSelecionados.setText("");
        Collections.addAll(ignoreKeyCodes, new KeyCode[]{F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12});
        initTable();
        System.out.println("Iniciado");
        primaryStage.setTitle("OPA");
        elements = new ArrayList<>();

        btnDeletarSelecionados.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deletarSelecionados();
            }
        });
        carregarArquivo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                carregarArquivo();
            }
        });
        dateTxt.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (ignoreKeyCodes.contains(keyEvent.getCode())) {
                    keyEvent.consume();
                }
            }
        });
        dateField(dateTxt);

        btnSalvar.setOnAction(event -> {
            Session session = Main.getLocalDB();

            session.getTransaction().begin();
            if (selectedElement == null) {
                session.persist(new DateElement(nameTxt.getText(), dateTxt.getText(), statusTxt.getText()));
            } else {
                selectedElement.setDate(dateTxt.getText());
                selectedElement.setName(nameTxt.getText());
                selectedElement.setStatus(statusTxt.getText());
                session.update(selectedElement);
            }
            session.getTransaction().commit();
            session.close();
            updateList();
        });

        novoElemento.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cleanFields();
            }
        });


        updateList();


    }

    private void cleanFields() {
        selectedElement = null;
        dateTxt.setText(null);
        nameTxt.setText(null);
        statusTxt.setText(null);
    }


    public void onSelection(Integer index, ActionEvent event) {

    }

    public static void dateField(final TextField textField) {
        maxField(textField, 10);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() < 11) {
                    String value = textField.getText();
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                    value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                    textField.setText(value);
                    positionCaret(textField);
                }
            }
        });
    }


    public static void ignoreKeys(final TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (ignoreKeyCodes.contains(keyEvent.getCode())) {
                    keyEvent.consume();
                }
            }
        });
    }

    /**
     * Devido ao incremento dos caracteres das mascaras eh necessario que o cursor sempre se posicione no final da string.
     *
     * @param textField TextField
     */
    private static void positionCaret(final TextField textField) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Posiciona o cursor sempre a direita.
                textField.positionCaret(textField.getText().length());
            }
        });
    }

    private static void maxField(final TextField textField, final Integer length) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue.length() > length)
                    textField.setText(oldValue);
            }
        });
    }


    public void carregarArquivo() {
        FileChooser fileChooser = new FileChooser();
        if (Main.getPropertie("lastfile") != null) {
            File dir = new File(Main.getPropertie("lastfile")).getParentFile();
            if (dir.exists()) {
                fileChooser.setInitialDirectory(dir);
            }

        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json files (*.json)", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            loadFile(selectedFile);
        }
    }

    private void loadFile(File selectedFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode dateElements = mapper.readValue(selectedFile, ArrayNode.class);

            Session localDb = Main.getLocalDB();
            localDb.getTransaction().begin();
            for (JsonNode de : dateElements) {
                try {
                    localDb.persist(mapper.convertValue(de, DateElement.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            localDb.getTransaction().commit();
            Main.setProperties("lastfile", selectedFile.getAbsolutePath());
            updateList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateList() {
        final ProgressDialog dialog = new ProgressDialog();
        cleanFields();
        dialog.show();
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {

                Session localDb = Main.getLocalDB();
                List<DateElement> elementList = localDb.createQuery("select td from DateElement td", DateElement.class).list();
                Platform.runLater(new Runnable() {
                    public void run() {

                        tableContainer.setItems(FXCollections.observableList(elementList));
                        tableContainer.autosize();
                        tableContainer.refresh();
                        dialog.hide();
                    }
                });
                localDb.close();
                return null;
            }
        };
        runAsync(task);

    }


    private HBox wrapInHBOX(Node element) {
        HBox hBox = new HBox();
        hBox.getChildren().add(element);
        return hBox;
    }

    @Override
    public URL getXML() {
        return getXML("/initial.fxml");
    }

    @Override
    public double getHeight() {
        return 600;
    }

    @Override
    public double getWidth() {
        return 1200;
    }


    @Override
    public void onCheckBoxChange(Integer index, ActionEvent event) {
        DateElement element = tableContainer.getItems().get(index);
        CheckBox checkBox = (CheckBox) event.getSource();
        System.out.println("Element: " + element.getName() + " : " + checkBox.isSelected());
        selected.put(element.getId(), checkBox.isSelected());

        int total = 0;
        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
            if (entry.getValue()) {
                total++;
            }
        }
        if (total > 0) {
            btnDeletarSelecionados.setVisible(true);
            lblSelecionados.setText(total + " itens selecionados");
        }

    }

    public void deletarSelecionados() {
        Session session = Main.getLocalDB();
        session.getTransaction().begin();
        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {
            session.delete(session.find(DateElement.class, entry.getKey()));
        }
        session.getTransaction().commit();
        selected = new HashMap<>();
        lblSelecionados.setText("");
        btnDeletarSelecionados.setVisible(false);
        updateList();

    }
}
