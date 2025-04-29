package org.example.sbsk_v2_slavemodule_card_camera;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    public Label titleLabel;
    public ImageView logoImageView1;
    public ImageView logoImageView2;
    public ImageView logoImageView3;
    public ImageView logoImageView4;
    public Label welcomeLabel;
    public Label descriptionLabel;
    public Label footerLabel;
    public Button toHandleUserAttendanceButton;




    private Stage userAttendanceStage;
    @FXML
    public void handleUserAttendance(ActionEvent actionEvent) {

        System.out.println("User Attendance Stage");

        if (userAttendanceStage != null && userAttendanceStage.isShowing()){
            userAttendanceStage.toFront();
            userAttendanceStage.requestFocus();
           return;

        }

        try {
            FXMLLoader attendanceLoader = new FXMLLoader(getClass().getResource("userAttendanceController-view.fxml"));
            Parent root = attendanceLoader.load();

            userAttendanceStage = new Stage();
            userAttendanceStage.setTitle("Attendance Window");
            userAttendanceStage.setScene(new Scene(root));
            userAttendanceStage.initModality(Modality.APPLICATION_MODAL);
            userAttendanceStage.setAlwaysOnTop(true);
            userAttendanceStage.setOnHidden(event -> userAttendanceStage = null);
            userAttendanceStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}