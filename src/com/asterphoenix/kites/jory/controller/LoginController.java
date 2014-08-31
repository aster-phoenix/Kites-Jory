package com.asterphoenix.kites.jory.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.Scene = scene;
		stage.setScene(scene);
		stage.show();
	}
	
	Stage stage;
	Scene Scene;

}
