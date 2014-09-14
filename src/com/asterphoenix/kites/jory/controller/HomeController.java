package com.asterphoenix.kites.jory.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController implements Initializable {

	private Stage stage;
	private Scene scene;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.scene = scene;
		this.stage.setResizable(true);
		this.stage.setWidth(966);
		this.stage.setHeight(600);
		this.stage.setFullScreen(true);
	}

}
