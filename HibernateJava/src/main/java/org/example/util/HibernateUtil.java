package org.example.util;

import org.example.entities.ClientEntity;
import org.example.entities.OrderEntity;
import org.example.entities.OrderStatusEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    //XML based configuration
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(ClientEntity.class);
            configuration.addAnnotatedClass(OrderStatusEntity.class);
            configuration.addAnnotatedClass(OrderEntity.class);
            sessionFactory = configuration.buildSessionFactory();
            return sessionFactory;
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
}