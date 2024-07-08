package br.com.lucianobrito.enveditor.controllers;

import br.com.lucianobrito.enveditor.service.EnvFilesService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SobreController implements Initializable {

    private final Logger LOGGER = Logger.getLogger(SobreController.class.getName());

    private String version;

    @FXML
    private TextArea txtArea;

    @FXML
    protected void btnFechar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            version = EnvFilesService.getInstance().getVersionApp();
            txtArea.setText(getDescription());
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public String getVersion() {
        return version;
    }

    private String getDescription() {
        return new StringBuilder().append("Aplicativo opensource para edição de variáveis de\n")
                .append("ambiente em distribuições Linux.\n\n")
                .append("Versão: " + version + "\n")
                .append("Autor: Luciano Brito\n")
                .append("Licença: GPL-2.0\n")
                .append("Repositório: \nhttps://github.com/lucianobritodev/EnvEditor")
                .toString();
    }
}
