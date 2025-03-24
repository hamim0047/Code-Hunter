module com.example.tilegamefxglproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;

    opens com.example.tilegamefxglproject to javafx.fxml;
    exports com.example.tilegamefxglproject;
}