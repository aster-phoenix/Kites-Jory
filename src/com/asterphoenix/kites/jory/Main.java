package com.asterphoenix.kites.jory;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.asterphoenix.kites.jory.controller.LoginController;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			ResourceBundle resource = PropertyResourceBundle
					.getBundle("com.asterphoenix.kites.jory.res/Kites", Locale.getDefault());
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("view/login.fxml"));
			loader.setResources(resource);
			StackPane root = loader.load();
			Scene scene = new Scene(root);
			LoginController controller = loader.getController();

			controller.setUp(primaryStage, scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
