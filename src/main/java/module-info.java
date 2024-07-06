module br.com.lucianobrito.enveditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.desktop;
    
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.xml;
    requires java.logging;

    opens br.com.lucianobrito.enveditor to javafx.fxml;
    exports br.com.lucianobrito.enveditor;
    exports br.com.lucianobrito.enveditor.controllers;
    opens br.com.lucianobrito.enveditor.controllers to javafx.fxml;
}