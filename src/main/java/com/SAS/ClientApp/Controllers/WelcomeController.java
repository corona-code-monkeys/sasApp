package com.SAS.ClientApp.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.SAS.ClientApp.Controllers.Vista.VistaNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class WelcomeController implements Initializable {

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;

    @FXML
    private Button btnGuest;

    @FXML
    private Button btnSignup;


    public void handleRegister(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = null;
        try {
            mainPane = (Pane) loader.load(getClass().getResourceAsStream(VistaNavigator.REGISTER));
        } catch (IOException e) {
        }
        stage.close();
        Scene scene = new Scene(mainPane);
        stage.setTitle("DeYMCA");
        stage.setScene(scene);
        stage.show();
    }

    public void handleGuestLogin(ActionEvent actionEvent) {
        try {
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            //stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.MAIN));
            Pane mainPane = null;
            try {
                mainPane = (Pane) loader.load();
            } catch (IOException e) {
            }
            // Get the Controller from the FXMLLoader
            MainController mainController = loader.getController();
            VistaNavigator.setMainController(loader.getController());
            // Set data in the controller
            mainController.setUserName("Guest");
            mainController.setUserRole("GUEST");
            mainController.setPersonalArea(true);
            stage.close();
            Scene scene = new Scene(mainPane);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){

        }
    }

    public void handleLogin(ActionEvent actionEvent) {
        String response;
        String userName = txtUsername.getText();
        String password = txtPassword.getText();
        if (userName.isEmpty() || password.isEmpty()) {
            setLblError(Color.TOMATO, "Empty credentials");
        } else {
            response = sendLoginRequest();
            if(!response.equals("Failed")){
                try {
                    Node node = (Node) actionEvent.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    //stage.close();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.MAIN));
                    Pane mainPane = null;
                    try {
                        mainPane = (Pane) loader.load();
                    } catch (IOException e) {
                    }
                    // Get the Controller from the FXMLLoader
                    MainController mainController = loader.getController();
                    VistaNavigator.setMainController(loader.getController());
                    // Set data in the controller
                    mainController.setUserName(userName);
                    mainController.setUserRole(response);
                    stage.close();
                    Scene scene = new Scene(mainPane);
                    stage.setScene(scene);
                    stage.show();
                }
                catch (Exception e){

                }
            }
        }
    }

    private String sendLoginRequest(){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost request = new HttpPost(MainController.serverURL + "/users/login");

            JSONObject json = new JSONObject();
            json.put("username", txtUsername.getText());
            json.put("password", txtPassword.getText());

            //create the request
            StringEntity stringEntity = new StringEntity(json.toString());
            request.getRequestLine();
            request.setEntity(stringEntity);
            request.addHeader("Content-Type", "application/json");

            CloseableHttpResponse response = httpClient.execute(request);

            try {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                    if (result.equals("Failed")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Your credentials are incorrect.\nPlease try again.");
                        alert.show();
                    }
                }

            } finally {
                response.close();
            }
        } catch (Exception e) {
            return result;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                return result;
            }
        }
        return result;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*
        // TODO
        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up : Good to go");
        }

         */
    }

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
        System.out.println(text);
    }
}