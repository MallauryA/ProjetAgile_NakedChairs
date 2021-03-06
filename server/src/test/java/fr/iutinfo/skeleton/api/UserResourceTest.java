package fr.iutinfo.skeleton.api;

import fr.iutinfo.skeleton.common.dto.UserDto;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static fr.iutinfo.skeleton.api.Helper.*;
import static org.junit.Assert.assertEquals;

public class UserResourceTest extends JerseyTest {
    private static final String PATH = "/user";
    private UserDao dao = BDDFactory.getDbi().open(UserDao.class);
    
    @Override
    protected Application configure() {
        return new Api();
    }

    @Before
    public void init() {
        Helper.initDb();
    }

    @Test
    public void read_should_return_a_user_as_object() {
        createUserWithName("foo");
        UserDto utilisateur = target(PATH + "/foo").request().get(UserDto.class);
        assertEquals("foo", utilisateur.getEmail());
    }

    @Test
    public void read_user_should_return_good_alias() {
        createRms();
        UserDto user = target(PATH + "/rms@fsf.org").request().get(UserDto.class);
        assertEquals("RMS", user.getrole());
    }

    @Test
    public void read_user_should_return_good_email() {
        createIan();
        UserDto user = target(PATH + "/ian@debian.org").request().get(UserDto.class);
        assertEquals("ian@debian.org", user.getEmail());
    }

    @Test
    public void read_user_should_read_user_with_same_salt() {
        String expectedSalt = "graindesel";
        createUserWithPassword("Mark", "motdepasse", expectedSalt);
        User user = dao.findByEmail("Mark");
        assertEquals(expectedSalt, user.getSalt());
    }

    @Test
    public void read_user_should_return_hashed_password() throws NoSuchAlgorithmException {
        createUserWithPassword("Loïc Dachary", "motdepasse", "grain de sable");
        User user = dao.findByEmail("Loïc Dachary");
        assertEquals("dfeb21109fe5eab1b1db7369844921c44b87b44669b0742f3f73bd166b474779", user.getPasswdHash());
    }

    @Test
    public void create_should_return_the_user_with_valid_id() {
        User user = new User(1, "thomas");
        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON);
        String json = target(PATH).request().post(userEntity).readEntity(String.class);
        assertEquals("{\"email\":\"thomas\",\"id\":7", json.substring(0, 24));
    }

    @Test
    public void list_should_return_all_users() {
        createUserWithName("foo");
        createUserWithName("bar");
        List<UserDto> users = target(PATH + "/").request().get(listUserResponseType);
        assertEquals(2, users.size());
    }

    @Test
    public void list_all_must_be_ordered() {
        createUserWithName("foo");
        createUserWithName("bar");
        List<UserDto> users = target(PATH + "/").request().get(listUserResponseType);
        assertEquals("foo", users.get(0).getEmail());
    }

    @Test
    public void after_delete_read_user_sould_return_204() {
        User u = createUserWithName("toto");
        int status = target(PATH + "/" + u.getId()).request().delete().getStatus();
        assertEquals(204, status);
    }

    @Test
    public void should_delete_user() {
        User u = createUserWithName("toto");
        target(PATH + "/" + u.getEmail()).request().delete();
        User user = dao.findByEmail(u.getEmail());
        Assert.assertEquals(null, user);
    }

    @Test
    public void list_should_search_in_name_field() {
        createUserWithName("foo");
        createUserWithName("bar");

        List<UserDto> users = target(PATH + "/").queryParam("q", "ba").request().get(listUserResponseType);
        assertEquals("bar", users.get(0).getEmail());
    }

    @Test
    public void list_should_search_in_alias_field() {
        createRms();
        createLinus();
        createRob();

        List<UserDto> users = target(PATH + "/").queryParam("q", "RMS").request().get(listUserResponseType);
        assertEquals("Richard Stallman", users.get(0).getName());
    }

    @Test
    public void list_should_search_in_email_field() {
        createRms();
        createLinus();
        createRob();

        List<UserDto> users = target(PATH + "/").queryParam("q", "fsf").request().get(listUserResponseType);
        assertEquals(2, users.size());
    }

    @After
    public void fin(){
        dao.dropUserTable();
    }
}
