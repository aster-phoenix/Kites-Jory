package com.asterphoenix.kites.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.asterphoenix.kites.model.Order.OrderType;

public class JoryDAO {
	
	protected EntityManager em;
	private List<Category> categoryList;
	private List<Product> productList;
	private List<Order> orderList;
	
	public JoryDAO(EntityManager em) {
		this.em = em;
	}
	
	public User updateUser(User user) {
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		return user;
	}
	
	public User validateUser(User user) {
		TypedQuery<User> query = em.createQuery("select u from User u where u.userName = :username "
				+ "and u.hashedPassword = :password", User.class);
		query.setParameter("username", user.getUserName());
		query.setParameter("password", user.getHashedPassword());
		List<User> result = query.getResultList();
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public List<Category> getCategoryList() {
		TypedQuery<Category> q1 = em.createQuery("select c from Category c", Category.class);
		categoryList = q1.getResultList();
		return categoryList;
	}

	public List<Product> getProductList() {
		TypedQuery<Product> q2 = em.createQuery("select p from Product p", Product.class);
		productList = q2.getResultList();
		return productList;
	}
	
	public Product refreshProduct(Product product) {
		em.refresh(product);
		return product;
	}
//	public List<Order> getOrderList() {
//		TypedQuery<Order> q2 = em.createQuery("select o from Order o", Order.class);
//		orderList = q2.getResultList();
//		return orderList;
//	}
	
	public List<Order> getOrderList(String date, OrderType type) {
		TypedQuery<Order> q2 = em.createQuery("select o from Order o where o.orderDate = :date and o.orderType = :type", Order.class);
		q2.setParameter("date", date);
		q2.setParameter("type", type);
		orderList = q2.getResultList();
		return orderList;
	}
	
	public void mergeOrder(Order order) {
		em.getTransaction().begin();
		em.merge(order);
		em.getTransaction().commit();
	}
	
	public Customer getCustomer(Long id) {
		return em.find(Customer.class, id);
	}
	
	public void addCategory(Category c) {
		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}
	
	public void removeCategory(long id) {
		em.getTransaction().begin();
		Category c = em.find(Category.class, id);
		em.remove(c);
		em.getTransaction().commit();
	}
	
	public void updateCategory(Category newCat) {
		em.getTransaction().begin();
		em.merge(newCat);
		em.getTransaction().commit();
	}
	
	public void addProduct(Product p) {
		em.getTransaction().begin();
		em.persist(p);
		em.getTransaction().commit();
	}
	
	public void removeProduct(long id) {
		em.getTransaction().begin();
		Product p = em.find(Product.class, id);
		em.remove(p);
		em.getTransaction().commit();
	}
	
	public void updateProduct(Product newProduct) {
		em.getTransaction().begin();
		em.merge(newProduct);
		em.getTransaction().commit();
	}

	public void closeResources() {
		if (em.isOpen()) {
			em.close();
		}
	}
	
	public boolean backup(File file) {
		try {
			TypedQuery<Thread> backupQuery = em.createQuery("objectdb backup", Thread.class);
			backupQuery.setParameter("target", file);
			Thread backupThread = backupQuery.getSingleResult();
			backupThread.join();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean restore(File restoreFile) {
		try {
			File dist = new File("D:\\APPS\\Portable\\JEE Servers\\objectdb\\db\\Kites\\KDB.odb");
			Files.copy(restoreFile.toPath(), dist.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
