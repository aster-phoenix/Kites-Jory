package com.asterphoenix.kites.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JoryDAO {
	
	protected EntityManager em;
	private List<Category> categoryList;
	private List<Product> productList;
	
	public JoryDAO(EntityManager em) {
		this.em = em;
	}
	
	public User validateUser(User user) {
//		em.getTransaction().begin();
//		em.persist(user);
//		em.getTransaction().commit();
		
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

}
