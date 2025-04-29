module org.example.sbsk_v2_slavemodule_card_camera {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.smartcardio;
    requires java.desktop;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;

    opens org.example.sbsk_v2_slavemodule_card_camera to javafx.fxml;
    exports org.example.sbsk_v2_slavemodule_card_camera;
}