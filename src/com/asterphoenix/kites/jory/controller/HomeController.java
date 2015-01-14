package com.asterphoenix.kites.jory.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

import com.asterphoenix.kites.model.Category;
import com.asterphoenix.kites.model.Customer;
import com.asterphoenix.kites.model.JoryDAO;
import com.asterphoenix.kites.model.Order;
import com.asterphoenix.kites.model.Order.OrderStatus;
import com.asterphoenix.kites.model.Order.OrderType;
import com.asterphoenix.kites.model.OrderItem;
import com.asterphoenix.kites.model.Product;
import com.asterphoenix.kites.model.User;
import com.asterphoenix.roxy.RoxyDigest;

public class HomeController implements Initializable {

	private Stage stage;
	private Scene scene;
	private JoryDAO joryDAO;
	private ResourceBundle resources;
	private List<Category> categoryList;
	private List<Product> productList;
	private List<Order> orderList;
	private File backupFile;
	private Thread notificationThread;
	private boolean notificationFlag = true;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Kites-Jory");
		EntityManager em = emf.createEntityManager();
		joryDAO = new JoryDAO(em);
		init();
//		Platform.runLater(notificationRunnable);
		notificationThread = new Thread(notificationRunnable);
		notificationThread.start();
	}

	public void init() {
		setupOrderTypeCombo();
		datePicker.setValue(LocalDate.now());
		updateLists();
		updateCategoryListView();
		updateCombos();
		updateNotificationNumbers();
		productListView.getItems().clear();
		
	}

	public void setUp(Stage stage, Scene scene) {
		this.stage =  stage;
		this.scene = scene;

		this.stage.setResizable(true);
		this.stage.setWidth(966);
		this.stage.setHeight(600);
		this.stage.centerOnScreen();
	}

	@FXML
	public void addCategory(ActionEvent e) {
		Optional<String> categoryName = Dialogs.create().masthead(resources.getString("provide.data"))
				.message(resources.getString("provide.cat.name"))
				.lightweight().style(DialogStyle.UNDECORATED)
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
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(categoryImage.getImage(), null), "png", buf);
				c.setImageBytes(Base64.getEncoder().encodeToString(buf.toByteArray()));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			joryDAO.updateCategory(c);
			init();
		}
	}

	@FXML
	public void addProduct(ActionEvent e) {
		if (categoryListView2.getSelectionModel().isEmpty()) {
			//			
		} else {
			Optional<String> productName = Dialogs.create().masthead(resources.getString("provide.data"))
					.message(resources.getString("provide.product.name"))
					.lightweight().style(DialogStyle.UNDECORATED)
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
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(productImage.getImage(), null), "png", buf);
			p.setImageBytes(Base64.getEncoder().encodeToString(buf.toByteArray()));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
		Product p = getSelectedProduct(productListView.getSelectionModel().getSelectedItem());
		displayProduct(joryDAO.refreshProduct(p));
	}

	@FXML
	public void onOrderListViewClicked() {
		displayOrder(getSelectedOrder(orderListView.getSelectionModel().getSelectedItem()));
	}

	public void updateLists() {
		categoryList = joryDAO.getCategoryList();
		productList = joryDAO.getProductList();
		orderList = joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem()));
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
		orderStatus.getItems().clear();
		orderStatus.getItems().add(OrderStatus.Pendding.toString());
		orderStatus.getItems().add(OrderStatus.Shipped.toString());
		orderStatus.getItems().add(OrderStatus.Completed.toString());
		orderStatus.getItems().add(OrderStatus.Rejected.toString());
		orderStatus.getSelectionModel().select(0);
	}

	public void setupOrderTypeCombo() {
		orderType.getItems().clear();
		orderType.getItems().add("Delivery");
		orderType.getItems().add("Pickup");
		orderType.getSelectionModel().select(0);
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

	public Order getSelectedOrder(String selectedOrderName) {
		Optional<Order> order = orderList.stream().filter(o -> 
		o.getOrderID() == Long.valueOf(selectedOrderName.substring(selectedOrderName.indexOf('#') + 1))).findFirst();
		if (order.isPresent()) {
			return order.get();
		}
		return null;
	}

	public void displayCategory(Category category) {
		categoryID.setText(String.valueOf(category.getCategoryID()));
		categoryName.setText(category.getCategoryName());
		categoryDesc.setText(category.getCategoryDescription());
		try {
			categoryImage.setImage(new Image(new ByteArrayInputStream(
					Base64.getDecoder().decode(category.getImageBytes()))));
		} catch (Exception e) {
			categoryImage.setImage(new Image(getClass()
					.getResource("../res/ic_action_attachment.png").toExternalForm()));
		}
	}

	public void displayProduct(Product product) {
		productID.setText(String.valueOf(product.getProductID()));
		productName.setText(product.getProductName());
		productPrice.setText(String.valueOf(product.getProductPrice()));
		productQTY.setText(String.valueOf(product.getProductQTY()));
		productBrand.getSelectionModel().select(product.getProductBrand());
		productCategory.getSelectionModel().select(product.getCategory().getCategoryName());
		productDesc.setText(product.getProductDescription());
		try {
			productImage.setImage(new Image(new ByteArrayInputStream(
					Base64.getDecoder().decode(product.getImageBytes()))));
		} catch (Exception e) {
			productImage.setImage(new Image(getClass()
					.getResource("../res/ic_action_attachment.png").toExternalForm()));
		}
	}

	public void displayOrder(Order order) {
		Customer c = joryDAO.getCustomer(order.getCustomerID());
		customerLabel.setText(c.getCustomerFName() + " " + c.getCustomerLName() + ", "
				+ order.getShippingAddress() + ", " + order.getTotalPrice() + " $");
		orderStatus.getSelectionModel().select(order.getOrderStatus().toString());
		ArrayList<String> m = new ArrayList<String>();
		for (OrderItem o : order.getOrders()) {
			m.add(o.getQty() + " X " + o.getProductName());
		}
		ObservableList<String> list = FXCollections.observableArrayList(m);
		orderItemListView.setItems(list);
	}

	@FXML
	public void browseImage(MouseEvent e) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("PNG Images", "*.png"));
		File file = fc.showOpenDialog(stage);
		if (null != file) {
			try {
				byte[] buf = Files.readAllBytes(file.toPath());
				ImageView iv = (ImageView) e.getSource();
				if (iv.equals(categoryImage)) {
					categoryImage.setImage(new Image(new ByteArrayInputStream(buf)));
				} else if (iv.equals(productImage)) {
					productImage.setImage(new Image(new ByteArrayInputStream(buf)));
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@FXML
	public void switchScreen() {
		stage.setFullScreen(!stage.isFullScreen());
		if (stage.isFullScreen()) {
			fullScreenIMG.setImage(new Image(this.getClass()
					.getResource("../res/ic_action_return_from_full_screen.png").toExternalForm()));
		} else {
			fullScreenIMG.setImage(new Image(this.getClass()
					.getResource("../res/ic_action_full_screen.png").toExternalForm()));
		}
	}

	@FXML
	public void exit() {
		joryDAO.closeResources();
		notificationFlag = false;
		Platform.exit();
	}

	@FXML
	public void minimize() {
		stage.setIconified(true);
	}

	@FXML
	public void browseBackup() {
		DirectoryChooser dc = new DirectoryChooser();
		backupFile = dc.showDialog(stage);
		if (null != backupFile) {
			backupPath.setText(backupFile.getAbsolutePath());
		}
	}

	@FXML
	public void browseRestore() {
		FileChooser fc = new FileChooser();
		backupFile = fc.showOpenDialog(stage);
		if (null != backupFile) {
			restorePath.setText(backupFile.getAbsolutePath());
		}
	}

	@FXML
	public void goBackup() {
		if (backupFile != null) {
			if (joryDAO.backup(backupFile)) {
				backupPath.setText("");
				Dialogs.create().message(resources.getString("backup.success"))
				.style(DialogStyle.UNDECORATED).lightweight().showInformation();
			}
		}
	}

	@FXML
	public void goRestore() {
		if (backupFile != null) {
			if (joryDAO.restore(backupFile)) {
				restorePath.setText("");
				Dialogs.create().message(resources.getString("restore.success"))
				.style(DialogStyle.UNDECORATED).lightweight().showInformation();
			}
		}
	}

	@FXML
	public void goCredentials() {
		if (!oldUsername.getText().trim().isEmpty() &&
				!oldPassword.getText().trim().isEmpty() &&
				!newUsername.getText().trim().isEmpty() &&
				!newPassword.getText().trim().isEmpty()) {
			User user = new User();
			RoxyDigest digest = new RoxyDigest();
			user.setUserName(oldUsername.getText());
			String hashedPassword = new String(digest.digestWithSHA256(oldPassword.getText().getBytes()));
			user.setHashedPassword(hashedPassword);
			user = joryDAO.validateUser(user);
			if (null != user) {
				user.setUserName(newUsername.getText());
				hashedPassword = new String(digest.digestWithSHA256(newPassword.getText().getBytes()));
				user.setHashedPassword(hashedPassword);
				user = joryDAO.updateUser(user);
				Dialogs.create().message(resources.getString("user.updated"))
				.lightweight().style(DialogStyle.UNDECORATED).showInformation();
				goClear();
			} else {
				Dialogs.create().masthead(resources.getString("auth.fail"))
				.message(resources.getString("auth.fail.msg"))
				.lightweight().style(DialogStyle.UNDECORATED).showWarning();
				oldUsername.clear();
				oldPassword.clear();
			}
		} else {
			Dialogs.create().message(resources.getString("provide.data"))
			.lightweight().style(DialogStyle.UNDECORATED).showWarning();
		}
	}

	@FXML
	public void goClear() {
		oldUsername.setText("");
		oldPassword.setText("");
		newUsername.setText("");
		newPassword.setText("");
	}

	@FXML
	public void goPendding() {
//		List<Order> tempList = new ArrayList<Order>();
		orderList.clear();
		joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem())).stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Pendding)).forEach(o ->
		orderList.add(o));
		updateOrderListView(orderList);
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	@FXML
	public void goShiped() {
//		List<Order> tempList = new ArrayList<Order>();
		orderList.clear();
		joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem())).stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Shipped)).forEach(o ->
		orderList.add(o));
		updateOrderListView(orderList);
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	@FXML
	public void goCompleted() {
//		List<Order> tempList = new ArrayList<Order>();
		orderList.clear();
		joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem())).stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Completed)).forEach(o ->
		orderList.add(o));
		updateOrderListView(orderList);
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	@FXML
	public void goRejected() {
//		List<Order> tempList = new ArrayList<Order>();
		orderList.clear();
		joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem())).stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Rejected)).forEach(o ->
		orderList.add(o));
		updateOrderListView(orderList);
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	@FXML
	public void goDateFilter() {
		updateNotificationNumbers();
		orderListView.getItems().clear();
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	@FXML
	public void goOrderStatus() {
		Order order = getSelectedOrder(orderListView.getSelectionModel().getSelectedItem());
		if (orderStatus.getValue().equals(OrderStatus.Pendding.toString())) {
			order.setOrderStatus(OrderStatus.Pendding);
		}
		if (orderStatus.getValue().equals(OrderStatus.Completed.toString())) {
			order.setOrderStatus(OrderStatus.Completed);
		}
		if (orderStatus.getValue().equals(OrderStatus.Rejected.toString())) {
			order.setOrderStatus(OrderStatus.Rejected);
		}
		if (orderStatus.getValue().equals(OrderStatus.Shipped.toString())) {
			order.setOrderStatus(OrderStatus.Shipped);
		}
		joryDAO.mergeOrder(order);
		updateNotificationNumbers();
		orderListView.getItems().clear();
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}
	
	@FXML
	public void goOrderType() {
		
		updateNotificationNumbers();
		orderListView.getItems().clear();
		customerLabel.setText("");
		orderItemListView.getItems().clear();
	}

	public void updateOrderListView(List<Order> orders) {
		orderListView.getItems().clear();
		orders.forEach(o -> {
			orderListView.getItems().add("Order #" + o.getOrderID());
		});
	}

	public void updateNotificationNumbers() {
		orderList = joryDAO.getOrderList(datePicker.getValue().toString(), OrderType.valueOf(orderType.getSelectionModel().getSelectedItem()));
		Long count = orderList.stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Pendding)).count();
		penddingLabel.setText("" + count);
		count = orderList.stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Shipped)).count();
		shipedLabel.setText("" + count);
		count = orderList.stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Completed)).count();
		completedLabel.setText("" + count);
		count = orderList.stream().filter(o ->
		o.getOrderStatus().equals(Order.OrderStatus.Rejected)).count();
		rejectedLabel.setText("" + count);
	}
	
	Runnable notificationRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				while(notificationFlag) {
					Thread.sleep(2000);
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							updateNotificationNumbers();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@FXML private Label categoryID;
	@FXML private TextField categoryName;
	@FXML private ImageView categoryImage;
	@FXML private TextArea categoryDesc;
	@FXML private Label productID;
	@FXML private TextField productName;
	@FXML private ImageView productImage;
	@FXML private TextField productQTY;
	@FXML private TextField productPrice;
	@FXML private ComboBox<String> productBrand;
	@FXML private TextArea productDesc;
	@FXML private ComboBox<String> productCategory;
	@FXML private ListView<String> categoryListView1;
	@FXML private ListView<String> categoryListView2;
	@FXML private ListView<String> productListView;

	@FXML private TextField backupPath;
	@FXML private TextField restorePath;
	@FXML private TextField oldUsername;
	@FXML private TextField oldPassword;
	@FXML private TextField newUsername;
	@FXML private TextField newPassword;

	@FXML private Label penddingLabel;
	@FXML private Label shipedLabel;
	@FXML private Label completedLabel;
	@FXML private Label rejectedLabel;
	@FXML private Label customerLabel;
	@FXML private ListView<String> orderListView;
	@FXML private ListView<String> orderItemListView;
	@FXML private DatePicker datePicker;
	@FXML private ComboBox<String> orderStatus;
	@FXML private ComboBox<String> orderType;

	@FXML private ImageView fullScreenIMG;

}
