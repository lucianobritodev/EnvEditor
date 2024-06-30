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
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String USUARIO = "Usuário";
    private static final String SISTEMA = "Sistema";
    private static final String INCLUSAO = "Inclusão";
    private static final String ALTERACAO = "Alteração";


    @FXML
    private ListView localEnvsListView = new ListView();

    @FXML
    private ListView globalEnvsListView = new ListView();


    @FXML
    protected void btnRecarregarEnvLocal() {
        EnvFilesService service = EnvFilesService.getInstance();
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
        String chave = env.replaceAll("=.*$", "");
        String valor = env.replaceAll("^.*=", "").replaceAll("\"+", "");

        openModalEdicao(USUARIO, chave, valor, this.localEnvsListView);
    }

    @FXML
    protected void btnExcluirEnvLocal() {
        String env = this.localEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.localEnvsListView.getItems().remove(env);
    }

    @FXML
    protected void btnRecarregarEnvGlobal() {

        EnvFilesService service = EnvFilesService.getInstance();
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
        String chave = env.replaceAll("=.*$", "");
        String valor = env.replaceAll("^.*=", "").replaceAll("\"+", "");

        openModalEdicao(SISTEMA, chave, valor, this.globalEnvsListView);
    }

    @FXML
    protected void btnExcluirEnvGlobal() {
        String env = this.globalEnvsListView.getSelectionModel().getSelectedItem().toString();
        this.globalEnvsListView.getItems().remove(env);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnRecarregarEnvLocal();
        btnRecarregarEnvGlobal();
    }

    public void btnCancelar(ActionEvent actionEvent) {
        MainApplication.stage = null;
        System.exit(0);
    }

    public void btnSalvar(ActionEvent actionEvent) {

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
                            item = envModel.getChave().toUpperCase() + "=\"" + envModel.getValor() + "\"";
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
}