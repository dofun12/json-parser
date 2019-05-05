package sample;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateConnection {
    public static SessionFactory getLocalSession(String path) {
        SessionFactory sf = null;
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:"+path);
        configuration.setProperty("hibernate.connection.username", "");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.dialect", "org.lemanoman.sqlite.dialect.SQLiteDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        configuration.addAnnotatedClass(DateElement.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sf = configuration.buildSessionFactory(builder.build());

        return sf;
    }
}
