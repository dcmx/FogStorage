package com.fonkwill.fogstorage.ui;

import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginController extends AbstractController {
    @FXML
    private TextField tf_user;

    @FXML
    private TextField tf_password;

    @FXML
    private Button btn_submit;


    public void onButtonSubmit(ActionEvent actionEvent) {
        if (tf_password!= null && tf_user!= null) {
            fogStorageContext.setPassword(tf_password.getText());
            fogStorageContext.setUsername(tf_user.getText());

            if (!fogStorageContext.getPassword().isEmpty() &&
                 !fogStorageContext.getUsername().isEmpty()) {

                navigateToMain();

            }
        }

    }

    private void navigateToMain() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainController controller = loader.getController();
        controller.setFileService(fileService);
        controller.setFogStorageContext(fogStorageContext);
        controller.setStage(primaryStage);
        controller.setFogNodeRepository(fogNodeRepository);
        controller.init();;

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("FogStorage");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
