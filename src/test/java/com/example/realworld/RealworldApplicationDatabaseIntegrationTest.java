package com.example.realworld;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

public class RealworldApplicationDatabaseIntegrationTest
    extends RealworldApplicationIntegrationTest {

  private static DataSource dataSource = getDatasource();
  public static EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
  public static EntityManager entityManager = getEntityManager();
  private static Set<String> dbTables = getDBTables();

  private static DataSource getDatasource() {
    return getObject(dataSource, RealworldApplicationDatabaseIntegrationTest::dataSource);
  }

  private static EntityManagerFactory getEntityManagerFactory() {
    return getObject(
        entityManagerFactory, RealworldApplicationDatabaseIntegrationTest::sessionFactory);
  }

  private static EntityManager getEntityManager() {
    return getObject(entityManager, entityManagerFactory::createEntityManager);
  }

  private static Set<String> getDBTables() {
    return getObject(dbTables, RealworldApplicationDatabaseIntegrationTest::findDBTables);
  }

  private static Set<String> findDBTables() {
    Set<String> tables = new HashSet<>();
    transaction(
        () ->
            entityManager
                .unwrap(Session.class)
                .doWork(
                    connection -> {
                      ResultSet resultSet =
                          connection
                              .getMetaData()
                              .getTables(null, null, null, new String[] {"TABLE"});

                      while (resultSet.next()) {
                        String tableName = resultSet.getString("TABLE_NAME");
                        if (!tableName.contains("flyway")) {
                          tables.add(tableName);
                        }
                      }
                    }));
    return tables;
  }

  private static SessionFactory sessionFactory() {
    ServiceRegistry serviceRegistry = null;
    SessionFactory sessionFactory = null;
    try {
      Configuration configuration = configuration();
      StandardServiceRegistryBuilder standardServiceRegistryBuilder =
          new StandardServiceRegistryBuilder();
      standardServiceRegistryBuilder.applySettings(configuration.getProperties());
      serviceRegistry = standardServiceRegistryBuilder.build();
      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    } catch (Exception ex) {
      ex.printStackTrace();
      StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }
    return sessionFactory;
  }

  private static Configuration configuration() {
    Configuration configuration = new Configuration();
    configuration.setProperties(properties());
    return configuration;
  }

  private static Properties properties() {
    Properties properties = new Properties();
    properties.put(Environment.DRIVER, vertxConfiguration.getDatabase().getDriverClass());
    properties.put(Environment.SHOW_SQL, true);
    properties.put(Environment.FORMAT_SQL, true);
    properties.put(Environment.DATASOURCE, dataSource);
    return properties;
  }

  private static DataSource dataSource() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setUrl(vertxConfiguration.getDatabase().getUrl());
    jdbcDataSource.setUser(vertxConfiguration.getDatabase().getUser());
    jdbcDataSource.setPassword(vertxConfiguration.getDatabase().getPassword());
    return jdbcDataSource;
  }

  public void clearDatabase() {
    StringBuilder builder = new StringBuilder();
    dbTables.forEach(
        dbTable ->
            builder
                .append("SET FOREIGN_KEY_CHECKS = 0; ")
                .append("DELETE FROM ")
                .append(dbTable)
                .append("; SET FOREIGN_KEY_CHECKS = 1; "));
    executeSql(builder.toString());
  }

  protected void executeSql(String sql) {
    transaction(() -> entityManager.createNativeQuery(sql).executeUpdate());
  }

  public static void transaction(Runnable command) {
    entityManager.getTransaction().begin();
    command.run();
    entityManager.flush();
    entityManager.getTransaction().commit();
  }

  public static <T> T transaction(Supplier<T> supplier) {
    entityManager.getTransaction().begin();
    T result = supplier.get();
    entityManager.flush();
    entityManager.getTransaction().commit();
    return result;
  }
}
