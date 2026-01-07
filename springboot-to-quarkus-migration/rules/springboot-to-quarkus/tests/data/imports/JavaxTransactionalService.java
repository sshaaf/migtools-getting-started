package com.example.service;

// These javax transaction imports should trigger migration rules
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class JavaxTransactionalService {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public User createUser(User user) {
        entityManager.persist(user);
        return user;
    }
    
    @Transactional
    public User updateUser(User user) {
        return entityManager.merge(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}







