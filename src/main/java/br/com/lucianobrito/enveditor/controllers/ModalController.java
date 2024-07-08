package br.com.lucianobrito.enveditor.controllers;

import br.com.lucianobrito.enveditor.models.EnvModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModalController {

    private String chaveAnterior;

    @FXML
    private Label lblTitleModal;

    @FXML
    private TextField txtChaveEnvModal;

    @FXML
    private TextArea areaValorEnvModal;

    @FXML
    protected void btnModalConfirmar(ActionEvent event) {
        closeModal(event, new EnvModel(this.txtChaveEnvModal.getText(), this.areaValorEnvModal.getText(), this.chaveAnterior));
    }

    @FXML
    protected void btnModalCancelar(ActionEvent event) {
        closeModal(event, null);
    }

    public void setLblTitleModal(String title) {
        this.lblTitleModal.setText(title);
    }

    public void setTxtChaveEnvModal(String txtChaveEnvModal) {
        this.chaveAnterior = txtChaveEnvModal;
        this.txtChaveEnvModal.setText(txtChaveEnvModal);
    }

    public void setAreaValorEnvModal(String areaValorEnvModal) {
        this.areaValorEnvModal.setText(areaValorEnvModal.replaceAll("\"", ""));
    }

    private void closeModal(ActionEvent event, EnvModel envModel) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if (envModel != null) {
            stage.setUserData(envModel);
        }

        stage.close();
    }

}
