package com.asterphoenix.kites.jory.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JoryDAO {
	
	protected EntityManager em;
	
	public JoryDAO(EntityManager em) {
		this.em = em;
	}
	
	public User validateUser(User user) {
//		create admin user
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

}
