package facades;

import dto.HobbiesDTO;
import dto.PersonDTO;
import dto.PhoneDTO;
import dto.PhonesDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.ArrayList;
import java.util.List;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    Person p1;
    Person p2;
    Person p3;
    Address a1;
    Address a2;
    Address a3;
    Phone ph1;
    Phone ph2;
    Phone ph3;
    CityInfo ci1;
    CityInfo ci2;
    CityInfo ci3;
    Hobby h1;
    Hobby h2;
    Hobby h3;
    List<Phone> phones;
    PhonesDTO phonesDTO;
    List<Hobby> hobbies;
    HobbiesDTO hobbiesDTO;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            p1 = new Person("Hans", "Karlsen", "hans@hansen.dk");
            p2 = new Person("Bente", "Karlsen", "Bente@hansen.dk");
            p3 = new Person("Kurt", "Karlsen", "Kurt@hansen.dk");

            a1 = new Address("Hovedgade 27");
            a2 = new Address("Lillegade 90");
            a3 = new Address("Storegade 110");

            a1.addPerson(p1);
            a2.addPerson(p2);
            a3.addPerson(p3);

            ph1 = new Phone("12345678", "nokia 3310");
            ph2 = new Phone("87654321", "Samsung Galaxy A50");
            ph3 = new Phone("99998888", "Iphone 11 Pro Max");

            p1.addPhone(ph1);
            p2.addPhone(ph2);
            p3.addPhone(ph3);

            ci1 = new CityInfo("3000", "Helsingør");
            ci2 = new CityInfo("2240", "KBH");
            ci3 = new CityInfo("9000", "Aalborg");

            ci1.addAddress(a1);
            ci2.addAddress(a2);
            ci3.addAddress(a3);

            h1 = new Hobby("Boksning", "Junior");
            h2 = new Hobby("Svømning", "Junior");
            h3 = new Hobby("Boksning", "Senior");

            p1.addHobby(h1);
            p2.addHobby(h2);
            p3.addHobby(h3);

            phones = new ArrayList<>();
            phones.add(ph1);
            phones.add(ph2);
            phones.add(ph3);
            phonesDTO = new PhonesDTO(phones);

            hobbies = new ArrayList<>();
            hobbies.add(h1);
            hobbies.add(h2);
            hobbiesDTO = new HobbiesDTO(hobbies);

            em.getTransaction().begin();

            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();

            em.persist(ci1);
            em.persist(ci2);
            em.persist(ci3);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testPersonCount() {
        assertEquals(3, facade.personCount(), "Expects three rows in the database");
    }

    @Test
    public void testGetPerson() {
        PersonDTO p1DTO = facade.getPerson(ph1.getNumber());
        assertEquals(p1DTO.getFirstName(), p1.getFirstName(), "Expect the same firstname");
    }

    @Test
    public void testGetAllPersons() {
        assertEquals(3, facade.getAllPersons().getAll().size(), "Expect all persons");
    }

    @Test
    public void testGetAllZips() {
        assertEquals(3, facade.getAllZipcodes().getAll().size(), "Expect all zipcodes");
    }

    @Test
    public void testGetAllPersonsHobbies() {
        assertEquals(2, facade.getAllPersonsHobbies(h1.getName()).getAll().size(), "Expect two hobbies");
    }

    @Test
    public void testGetAllPersonsCity() {
        assertEquals(1, facade.getAllPersonsCity(ci2.getCity()).getAll().size(), "Expect one city");
    }

    @Test
    public void testGetHobbyCount() {
        assertEquals(2, facade.getHobbyCount(h1.getName()), "Excepts two instances of Boksning");
    }

    @Test
    public void testAddPerson() {

        PersonDTO pDTO = facade.addPerson(p1.getFirstName(), p1.getLastName(), p1.getEmail(), p1.getAddress().getStreet(), p1.getAddress().getCityInfo().getZipcode(), p1.getAddress().getCityInfo().getCity(), phonesDTO, hobbiesDTO);
        assertEquals(p1.getFirstName(), pDTO.getFirstName(), "Expect the same firstname");
        assertEquals(4, facade.personCount(), "Excepts four persons");
    }

    @Test
    public void testEditPerson() {
        PersonDTO pDTO = new PersonDTO(p2.getId(), "John", "Johnsen", "Idiot@hej.dk", "Et sted", "9000", "Aalborg", phonesDTO, hobbiesDTO);
        PersonDTO pDTO2 = facade.editPerson(pDTO);
        assertEquals(pDTO2.getFirstName(), pDTO.getFirstName(), "Excepts John");
    }

    @Test
    public void testDeletePerson() {
        PersonDTO pDTO = facade.deletePerson(p1.getId());
        assertEquals(2, facade.personCount(), "Excepts two persons");
    }
}
