package com.example.service;

// These jakarta transaction imports should NOT trigger migration rules
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JakartaTransactionalService {
    
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







