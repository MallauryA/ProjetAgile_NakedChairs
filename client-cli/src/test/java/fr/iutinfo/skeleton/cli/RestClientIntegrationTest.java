package fr.iutinfo.skeleton.cli;

import fr.iutinfo.skeleton.api.Api;
import fr.iutinfo.skeleton.api.BDDFactory;
import fr.iutinfo.skeleton.api.User;
import fr.iutinfo.skeleton.api.UserDao;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;

public class RestClientIntegrationTest extends JerseyTest {

    private UserDao userDao = BDDFactory.getDbi().open(UserDao.class);

    @Override
    protected Application configure() {
        return new Api();
    }

    @Test
    public void should_read_remote_user() {
        initDatabase();
        createUser("Thomas");

        RestClient restClient = new RestClient(getBaseUri().toString());
        User user = restClient.readUser("Thomas");
        assertEquals("Thomas", user.getName());
    }

    private void createUser(String name) {
        User thomas = new User();
        thomas.setName(name);
        userDao.insert(thomas);
    }

    private void initDatabase() {
        userDao.dropUserTable();
        userDao.createUserTable();
    }
}
