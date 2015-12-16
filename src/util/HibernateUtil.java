/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author razikov
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    /**
     * Статический конструктор, инициализирующий фабрику сессий
     */
    static {
        Configuration configuration = new Configuration().configure("/hibernate.cfg.xml");
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        HibernateUtil.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * Получить фабрику сессий
     * @return фабрика сессий
     */
    public static SessionFactory getSessionFactory() {
        return HibernateUtil.sessionFactory;
    }
}
