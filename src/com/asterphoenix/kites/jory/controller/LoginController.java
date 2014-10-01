package com.asterphoenix.kites.jory.controller;

import java.io.IOException;
import java.net.URL;
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

import com.asterphoenix.kites.model.JoryDAO;
import com.asterphoenix.kites.model.User;
import com.asterphoenix.roxy.RoxyDigest;

public class LoginController implements Initializable {

	private Stage stage;
	private Scene scene;
	private JoryDAO joryDAO;
	private ResourceBundle resources;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Kites-Jory");
		EntityManager em = emf.createEntityManager();
		joryDAO = new JoryDAO(em);
		this.resources = resources;
	}
	
	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.scene = scene;
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Kites | Administration panel (JORY)");
		stage.getIcons().add(new Image(this.getClass().getResource("../res/Butterfly-web-32.png").toExternalForm()));
		stage.centerOnScreen();
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
			joryDAO.closeResources();
		} else {
			Dialogs.create().masthead(resources.getString("auth.fail"))
			.message(resources.getString("auth.fail.msg"))
			.lightweight().style(DialogStyle.UNDECORATED).showWarning();
			userNameField.clear();
			passwordField.clear();
		}
	}
	
	public void openHome() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../view/Home.fxml"));
			loader.setResources(resources);
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
