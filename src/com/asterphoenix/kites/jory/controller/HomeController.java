package com.asterphoenix.kites.jory.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

import com.asterphoenix.kites.model.Category;
import com.asterphoenix.kites.model.JoryDAO;
import com.asterphoenix.kites.model.Product;

public class HomeController implements Initializable {

	private Stage stage;
	private Scene scene;
	private JoryDAO joryDAO;
	private List<Category> categoryList;
	private List<Product> productList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Kites-Jory");
		EntityManager em = emf.createEntityManager();
		joryDAO = new JoryDAO(em);
		init();
	}

	public void init() {
		updateLists();
		updateCategoryListView();
		updateCombos();
		productListView.getItems().clear();
	}
	
	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.scene = scene;
		
		this.stage.setResizable(true);
		this.stage.setWidth(966);
		this.stage.setHeight(600);
	}
	
	@FXML
	public void addCategory(ActionEvent e) {
		Optional<String> categoryName = Dialogs.create().masthead("Please provide data")
				.message("Enter category name").lightweight().style(DialogStyle.UNDECORATED)
				.showTextInput();
		if (categoryName.isPresent()) {
			Category c = new Category();
			c.setCategoryName(categoryName.get());
			joryDAO.addCategory(c);			
			init();
		}
	}

	@FXML
	public void removeCategory(ActionEvent e) {
		if (categoryListView1.getSelectionModel().isEmpty()) {
//			
		} else {
			joryDAO.removeCategory(Long.valueOf(categoryID.getText()));
			init();
		}
	}
	
	@FXML
	public void updateCategory(ActionEvent e) {
		if (categoryListView1.getSelectionModel().isEmpty()) {
//			
		} else {
			Category c = getSelectedCategory(categoryListView1.getSelectionModel().getSelectedItem());
			c.setCategoryName(categoryName.getText());
			c.setCategoryDescription(categoryDesc.getText());
			joryDAO.updateCategory(c);
			init();
		}
	}

	@FXML
	public void addProduct(ActionEvent e) {
		if (categoryListView2.getSelectionModel().isEmpty()) {
//			
		} else {
			Optional<String> productName = Dialogs.create().masthead("Please provide data")
					.message("Enter product name").lightweight().style(DialogStyle.UNDECORATED)
					.showTextInput();
			if (productName.isPresent()) {
				Category cat = getSelectedCategory(categoryListView2.getSelectionModel().getSelectedItem());
				Product p = new Product();
				p.setProductName(productName.get());
				p.setCategory(cat);
				joryDAO.addProduct(p);
				init();					
			}
		}
	}
	
	@FXML
	public void removeProduct(ActionEvent e) {
		if (productListView.getSelectionModel().isEmpty()) {
//			
		} else {
			joryDAO.removeProduct(Long.valueOf(productID.getText()));
			init();
		}
	}
	
	@FXML
	public void updateProduct(ActionEvent e) {
		Product p = getSelectedProduct(productListView.getSelectionModel().getSelectedItem());
		p.setProductName(productName.getText());
		p.setProductPrice(Float.valueOf(productPrice.getText()));
		p.setProductQTY(Float.valueOf(productQTY.getText()));
		p.setProductBrand(productBrand.getSelectionModel().getSelectedItem());
		p.setProductDescription(productDesc.getText());
		p.setCategory(getSelectedCategory(productCategory.getSelectionModel().getSelectedItem()));
		joryDAO.updateProduct(p);
		init();
	}
	
	@FXML
	public void onCategoryListView1Clicked() {
		displayCategory(getSelectedCategory(categoryListView1.getSelectionModel().getSelectedItem()));
	}
	
	@FXML
	public void onCategoryListView2Clicked() {
		updateProducListView(categoryListView2.getSelectionModel().getSelectedItem());
	}
	
	@FXML
	public void onProductListViewClicked() {
		displayProduct(getSelectedProduct(productListView.getSelectionModel().getSelectedItem()));
	}
	
	public void updateLists() {
		categoryList = joryDAO.getCategoryList();
		productList = joryDAO.getProductList();
	}
	
	public void updateCategoryListView() {
		categoryListView1.getItems().clear();
		categoryListView2.getItems().clear();
		categoryList.forEach(c -> {
			categoryListView1.getItems().add(c.getCategoryName());
		});
		categoryListView2.setItems(categoryListView1.getItems());
	}
	
	public void updateProducListView(String catName) {
		productListView.getItems().clear();
		productList.stream().filter(p ->
				p.getCategory().getCategoryName().equals(catName)).forEach( p -> {
					productListView.getItems().add(p.getProductName());
				});
	}
	
	public void updateCombos() {
		productCategory.setItems(categoryListView2.getItems());
		productList.forEach(p -> {
			if (productBrand.getItems().contains(p.getProductBrand())) {
				
			} else {
				productBrand.getItems().add(p.getProductBrand());				
			}
		});
	}
	
	public Category getSelectedCategory(String selectedCategoryName) {
		Optional<Category> cat = categoryList.stream().filter(c ->
			c.getCategoryName().equals(selectedCategoryName)).findFirst();
		if (cat.isPresent()) {
			return cat.get();
		}
		return null;
	}
	
	public Product getSelectedProduct(String selectedProductName) {
		Optional<Product> product = productList.stream().filter(p ->
			p.getProductName().equals(selectedProductName)).findFirst();
		if (product.isPresent()) {
			return product.get();
		}
		return null;
	}
	
	public void displayCategory(Category category) {
		categoryID.setText(String.valueOf(category.getCategoryID()));
		categoryName.setText(category.getCategoryName());
		categoryDesc.setText(category.getCategoryDescription());
	}
	
	public void displayProduct(Product product) {
		productID.setText(String.valueOf(product.getProductID()));
		productName.setText(product.getProductName());
		productPrice.setText(String.valueOf(product.getProductPrice()));
		productQTY.setText(String.valueOf(product.getProductQTY()));
		productBrand.getSelectionModel().select(product.getProductBrand());
		productCategory.getSelectionModel().select(product.getCategory().getCategoryName());
		productDesc.setText(product.getProductDescription());
	}
	
	@FXML
	public void switchScreen() {
		stage.setFullScreen(!stage.isFullScreen());
	}
	
	@FXML private Label categoryID;
	@FXML private TextField categoryName;
	@FXML private TextArea categoryDesc;
	@FXML private Label productID;
	@FXML private TextField productName;
	@FXML private TextField productQTY;
	@FXML private TextField productPrice;
	@FXML private ComboBox<String> productBrand;
	@FXML private TextArea productDesc;
	@FXML private ComboBox<String> productCategory;
	@FXML private ListView<String> categoryListView1;
	@FXML private ListView<String> categoryListView2;
	@FXML private ListView<String> productListView;

}
