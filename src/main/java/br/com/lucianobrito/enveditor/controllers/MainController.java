package br.com.lucianobrito.enveditor.controllers;

import br.com.lucianobrito.enveditor.MainApplication;
import br.com.lucianobrito.enveditor.models.EnvModel;
import br.com.lucianobrito.enveditor.models.enuns.Env;
import br.com.lucianobrito.enveditor.service.EnvFilesService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.BooleanStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class MainController implements Initializable {

    private boolean isLocalEnvChanged;
    private boolean isGlobalEnvChanged;
    private ObservableBooleanValue isMessage;

    private static final String USUARIO = "Usuário";
    private static final String SISTEMA = "Sistema";
    private static final String INCLUSAO = "Inclusão";
    private static final String ALTERACAO = "Alteração";

    private EnvFilesService service;

    @FXML
    private Button btnLocalEnvEditar;

    @FXML
    private Button btnLocalEnvExcluir;

    @FXML
    private Button btnGlobalEnvEditar;

    @FXML
    private Button btnGlobalEnvExcluir;

    @FXML
    private Label lblMessage;


    @FXML
    private ListView localEnvsListView = new ListView();

    @FXML
    private ListView globalEnvsListView = new ListView();


    @FXML
    protected void recarregarEnvLocal() {
        List<String> envs = service.readEnvFile(Env.LOCAL);
        ObservableList<String> observableList = FXCollections.observableList(envs);
        this.localEnvsListView.setItems(observableList);
    }

    @FXML
    public void incluirEnvLocal(ActionEvent event) {
        openModalInclusao(event, Env.LOCAL, USUARIO, localEnvsListView);
    }

    @FXML
    protected void editarEnvLocal() {
        String env = this.localEnvsListView.getSelectionModel().getSelectedItem().toString();
        String chave = service.getEnvKey(env);
        String valor = service.getEnvValue(env);

        openModalEdicao(Env.LOCAL, USUARIO, chave, valor, this.localEnvsListView);
    }

    @FXML
    protected void excluirEnvLocal() {
        String env = this.localEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.localEnvsListView.getItems().remove(env);
        isLocalEnvChanged = true;
    }

    @FXML
    protected void recarregarEnvGlobal() {
        List<String> envs = service.readEnvFile(Env.GLOBAL);
        ObservableList<String> observableList = FXCollections.observableList(envs);
        this.globalEnvsListView.setItems(observableList);
    }

    @FXML
    public void incluirEnvGlobal(ActionEvent event) {
        openModalInclusao(event, Env.GLOBAL, SISTEMA, globalEnvsListView);
    }

    @FXML
    protected void editarEnvGlobal() {
        String env = this.globalEnvsListView.getSelectionModel().getSelectedItem().toString();
        String chave = service.getEnvKey(env);
        String valor = service.getEnvValue(env);

        openModalEdicao(Env.GLOBAL, SISTEMA, chave, valor, this.globalEnvsListView);
    }

    @FXML
    protected void excluirEnvGlobal() {
        String env = this.globalEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.globalEnvsListView.getItems().remove(env);
        isGlobalEnvChanged = true;
    }

    @FXML
    protected void sobre() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sobre.fxml"));
        AnchorPane pane = loader.load();

        stage.setScene(new Scene(pane));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            isMessage = new SimpleBooleanProperty(false);
            isLocalEnvChanged = false;
            isGlobalEnvChanged = false;
            service = EnvFilesService.getInstance();
            recarregarEnvLocal();
            recarregarEnvGlobal();

            btnLocalEnvEditar.setDisable(true);
            btnLocalEnvExcluir.setDisable(true);
            btnGlobalEnvEditar.setDisable(true);
            btnGlobalEnvExcluir.setDisable(true);

            localEnvsListView.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((ObservableValue observableValue, Object o, Object t1) -> {
                        btnLocalEnvEditar.setDisable(false);
                        btnLocalEnvExcluir.setDisable(false);
                    });

            globalEnvsListView.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((ObservableValue observableValue, Object o, Object t1) -> {
                        btnGlobalEnvEditar.setDisable(false);
                        btnGlobalEnvExcluir.setDisable(false);
                    });

            lblMessage.textProperty().addListener(observable -> {
                StringProperty obs = (StringProperty) observable;
                if (obs != null && "".equals(obs.getValue())) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        this.lblMessage.setText("");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void cancelar() {
        sair();
    }

    @FXML
    public void salvar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        boolean isSaveLocal = false;
        boolean isSaveGlobal = false;

        if (isLocalEnvChanged) {
            List<EnvModel> listLocal = new ArrayList<>();
            this.localEnvsListView.getItems().forEach(item -> listLocal.add(
                    new EnvModel(service.getEnvKey(item.toString()), service.getEnvValue(item.toString()), null)));

            service.salvar(Env.LOCAL, listLocal);
            isSaveLocal = true;
            recarregarEnvLocal();
        }

        if (isGlobalEnvChanged) {
            List<EnvModel> listGlobal = new ArrayList<>();
            this.globalEnvsListView.getItems().forEach(item -> listGlobal.add(
                    new EnvModel(service.getEnvKey(item.toString()), service.getEnvValue(item.toString()), null)));

            service.salvar(Env.GLOBAL, listGlobal);
            isSaveGlobal = true;
            recarregarEnvGlobal();
        }

        if (isSaveLocal || isSaveGlobal) {
            this.lblMessage.setStyle("-fx-text-fill: green;");
            this.lblMessage.setText("Dados salvos com sucesso!");
        } else {
            this.lblMessage.setStyle("-fx-text-fill: orange;");
            this.lblMessage.setText("Nenhuma alteração foi registrada!");
        }
    }

    private void openModalInclusao(ActionEvent event, Env env, String titulo, ListView<String> listView) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modal-inclusao.fxml"));
            AnchorPane pane = loader.load();

            ModalController controller = loader.getController();
            controller.setLblTitleModal(String.format("%s de Variável de %s", INCLUSAO, titulo));

            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(windowEvent -> {
                EnvModel envModel = (EnvModel) stage.getUserData();
                if (envModel != null && listView != null) {
                    listView.getItems().add(envModel.getChave().toUpperCase() + "=\"" + envModel.getValor() + "\"");
                    if (Env.LOCAL.equals(env)) {
                        isLocalEnvChanged = true;
                    } else {
                        isGlobalEnvChanged = true;
                    }
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void openModalEdicao(Env env, String titulo, String chave, String valor, ListView<String> listView) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modal-inclusao.fxml"));
            AnchorPane pane = loader.load();

            ModalController controller = loader.getController();
            controller.setLblTitleModal(String.format("%s de Variável de %s", ALTERACAO, titulo));
            controller.setTxtChaveEnvModal(chave);
            controller.setAreaValorEnvModal(valor);

            stage.setScene(new Scene(pane));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(windowEvent -> {
                EnvModel envModel = (EnvModel) stage.getUserData();

                if (envModel != null && listView != null) {
                    ObservableList<String> newList = FXCollections.observableArrayList();
                    listView.getItems().forEach(item -> {
                        if (envModel.getChaveAnterior().equals(item.replaceAll("=.*$", ""))) {
                            item = envModel.getChave().toUpperCase() + "=\"" + envModel.getValor().replaceAll("\"+", "") + "\"";
                        }
                        newList.add(item);
                    });

                    listView.setItems(newList);

                    if (Env.LOCAL.equals(env)) {
                        isLocalEnvChanged = true;
                    } else {
                        isGlobalEnvChanged = true;
                    }
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @FXML
    public void sair() {
        MainApplication.stage = null;
        System.exit(0);
    }
}