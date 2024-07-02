package br.com.lucianobrito.enveditor.controllers;

import br.com.lucianobrito.enveditor.MainApplication;
import br.com.lucianobrito.enveditor.models.EnvModel;
import br.com.lucianobrito.enveditor.service.EnvFilesService;
import br.com.lucianobrito.enveditor.service.enuns.Env;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String USUARIO = "Usuário";
    private static final String SISTEMA = "Sistema";
    private static final String INCLUSAO = "Inclusão";
    private static final String ALTERACAO = "Alteração";

    private EnvFilesService service;


    @FXML
    private ListView localEnvsListView = new ListView();

    @FXML
    private ListView globalEnvsListView = new ListView();


    @FXML
    protected void btnRecarregarEnvLocal() {
        List<String> envs = service.readEnvFile(Env.LOCAL);
        ObservableList<String> observableList = FXCollections.observableList(envs);
        this.localEnvsListView.setItems(observableList);
    }

    @FXML
    public void btnIncluirEnvLocal() {
        openModalInclusao(USUARIO, localEnvsListView);
    }

    @FXML
    protected void btnEditarEnvLocal() {
        String env = this.localEnvsListView.getSelectionModel().getSelectedItem().toString();
        String chave = service.getEnvKey(env);
        String valor = service.getEnvValue(env);

        openModalEdicao(USUARIO, chave, valor, this.localEnvsListView);
    }

    @FXML
    protected void btnExcluirEnvLocal() {
        String env = this.localEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.localEnvsListView.getItems().remove(env);
    }

    @FXML
    protected void btnRecarregarEnvGlobal() {
        List<String> envs = service.readEnvFile(Env.GLOBAL);
        ObservableList<String> observableList = FXCollections.observableList(envs);
        this.globalEnvsListView.setItems(observableList);
    }

    @FXML
    public void btnIncluirEnvGlobal() {
        openModalInclusao(SISTEMA, globalEnvsListView);
    }

    @FXML
    protected void btnEditarEnvGlobal() {
        String env = this.globalEnvsListView.getSelectionModel().getSelectedItem().toString();
        String chave = service.getEnvKey(env);
        String valor = service.getEnvValue(env);

        openModalEdicao(SISTEMA, chave, valor, this.globalEnvsListView);
    }

    @FXML
    protected void btnExcluirEnvGlobal() {
        String env = this.globalEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.globalEnvsListView.getItems().remove(env);
    }

    @FXML
    protected void btnSobre() throws IOException {
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
            service = EnvFilesService.getInstance();
            reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnCancelar(ActionEvent actionEvent) {
        MainApplication.stage = null;
        System.exit(0);
    }

    public void btnSalvar(ActionEvent actionEvent) {
        List<EnvModel> listLocal = new ArrayList<>();
        this.localEnvsListView.getItems().forEach(item -> listLocal
                .add(new EnvModel(service.getEnvKey(item.toString()), service.getEnvValue(item.toString()), null))
        );
        service.salvar(Env.LOCAL, listLocal);

        List<EnvModel> listGlobal = new ArrayList<>();
        this.globalEnvsListView.getItems().forEach(item -> listGlobal
                .add(new EnvModel(service.getEnvKey(item.toString()), service.getEnvValue(item.toString()), null))
        );
        service.salvar(Env.GLOBAL, listGlobal);

        reload();
    }

    private void openModalInclusao(String titulo, ListView<String> listView) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modal-inclusao.fxml"));
            AnchorPane pane = loader.load();

            ModalController controller = loader.getController();
            controller.setLblTitleModal(String.format("%s de Variável de %s", INCLUSAO, titulo));

            stage.setScene(new Scene(pane));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(windowEvent -> {
                EnvModel envModel = (EnvModel) stage.getUserData();
                if (envModel != null && listView != null) {
                    listView.getItems().add(envModel.getChave().toUpperCase() + "=\"" + envModel.getValor() + "\"");
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openModalEdicao(String titulo, String chave, String valor, ListView<String> listView) {
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
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reload() {
        btnRecarregarEnvLocal();
        btnRecarregarEnvGlobal();
    }
}