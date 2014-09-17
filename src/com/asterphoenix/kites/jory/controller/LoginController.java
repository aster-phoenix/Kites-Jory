package com.asterphoenix.kites.jory.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

import com.asterphoenix.kites.jory.model.JoryDAO;
import com.asterphoenix.kites.jory.model.User;
import com.asterphoenix.roxy.RoxyDigest;

public class LoginController implements Initializable {

	private Stage stage;
	private Scene scene;
	private JoryDAO joryDAO;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Kites-Jory");
		EntityManager em = emf.createEntityManager();
		joryDAO = new JoryDAO(em);
	}
	
	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.scene = scene;
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Kites | Administration panel (JORY)");
		stage.getIcons().add(new Image(this.getClass().getResource("../res/Butterfly-web-32.png").toExternalForm()));
		stage.show();
	}
	
	@FXML
	public void login(ActionEvent e){
		User user = new User();
		RoxyDigest digest = new RoxyDigest();
		user.setUserName(userNameField.getText());
		String hashedPassword = new String(digest.digestWithSHA256(passwordField.getText().getBytes()));
		user.setHashedPassword(hashedPassword);
		user = joryDAO.validateUser(user);
		if (null != user) {
			openHome();
		} else {
			Dialogs.create().title("Warrning").masthead("Authentication faild")
			.message("User name OR password are not correct please try again!")
			.lightweight().style(DialogStyle.UNDECORATED).showWarning();
			userNameField.clear();
			passwordField.clear();
		}
	}
	
	public void openHome() {
		try {
			ResourceBundle resource = PropertyResourceBundle
					.getBundle("com.asterphoenix.kites.jory.res/Kites", Locale.getDefault());
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../view/Home.fxml"));
			loader.setResources(resource);
			StackPane root = loader.load();
			scene.setRoot(root);
			HomeController controller = loader.getController();
			controller.setUp(stage, scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML private TextField userNameField;
	@FXML private PasswordField passwordField;

}
