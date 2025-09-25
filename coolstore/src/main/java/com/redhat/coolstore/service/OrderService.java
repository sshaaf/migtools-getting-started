package com.redhat.coolstore.service;

import com.enterprise.audit.logging.config.AuditConfiguration;
import com.enterprise.audit.logging.exception.AuditLoggingException;
import com.enterprise.audit.logging.service.FileSystemAuditLogger;
import com.redhat.coolstore.model.Order;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class OrderService {

  @Inject
  private EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public List<Order> getOrders() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Order> criteria = cb.createQuery(Order.class);
    Root<Order> member = criteria.from(Order.class);
    criteria.select(member);
    return em.createQuery(criteria).getResultList();
  }

  public Order getOrderById(long id) {
    return em.find(Order.class, id);
  }

  private FileSystemAuditLogger auditLogger;

  @PostConstruct
  public void init() throws AuditLoggingException {
    // Initialize audit logger
    AuditConfiguration config = new AuditConfiguration();
    config.setLogDirectory("./device-inventory-audit-logs");
    config.setAutoCreateDirectory(true);
    auditLogger = new FileSystemAuditLogger(config);

  }

  @PreDestroy
  public void cleanup() throws AuditLoggingException {
    if (auditLogger != null) {
      auditLogger.close();
    }
  }

}