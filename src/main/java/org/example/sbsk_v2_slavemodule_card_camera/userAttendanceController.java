package org.example.sbsk_v2_slavemodule_card_camera;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class userAttendanceController {
    public Label titleLabel;
    public ImageView fieldImg;
    public Label footerLabel;
    public ImageView logoImageView1;
    public ImageView logoImageView2;
    public ImageView logoImageView3;
    public ImageView logoImageView4;



    private Stage cardVerificationStage;

    public void handleCardVerificationProcess(ActionEvent actionEvent) {

        System.out.println("Card Verification Process Start");

        if (cardVerificationStage != null && cardVerificationStage.isShowing()){
            cardVerificationStage.toFront();
            cardVerificationStage.requestFocus();
            return;

        }

        try {
            FXMLLoader cardVerificationLoader = new FXMLLoader(getClass().getResource("cardVerificationStageForAttendanceMarking-view.fxml"));
            Parent root = cardVerificationLoader.load();

            cardVerificationStage = new Stage();
            cardVerificationStage.setTitle("Card Verification Window (TO MARK ATTENDANCE PROCESS)");
            cardVerificationStage.setScene(new Scene(root));
            cardVerificationStage.initModality(Modality.APPLICATION_MODAL);
            cardVerificationStage.setAlwaysOnTop(true);
            cardVerificationStage.setOnHidden(event -> cardVerificationStage = null);


            // Get reference to current stage (UserAttendance stage)
            Stage currentStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

            cardVerificationStage.show();

            // Close the current stage
            currentStage.close();

        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
