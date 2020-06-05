package org.requirementsascode.moonwlker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.requirementsascode.moonwlker.Moonwlker;
import org.requirementsascode.moonwlker.testobject.animal.Animal;
import org.requirementsascode.moonwlker.testobject.animal.Dog;
import org.requirementsascode.moonwlker.testobject.person.Employee;
import org.requirementsascode.moonwlker.testobject.person.Person;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

public class SubclassInSamePackageTest extends MoonwlkerTest{
  /*
   * Happy path tests 
   */
  
  @Test 
  public void readsAndWrites_oneObject() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Person.class).inSamePackage()
            .build();
    
    String jsonString = "{\"type\":\"Employee\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"employeeNumber\":\"EMP-2020\"}";
    Person person = objectMapper.readValue(jsonString, Person.class);
    assertEquals("Jane", person.getFirstName());
    assertEquals("Doe", person.getLastName());
    assertEquals("EMP-2020", ((Employee)person).getEmployeeNumber());       
    
    assertEquals(jsonString, writeToJson(objectMapper, person));
  }
  
  @Test 
  public void readsAndWrites_oneObject_withTypeProperty() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Person.class).inSamePackage()
          .typeProperty("kind")
            .build();
    
    String jsonString = "{\"kind\":\"Employee\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"employeeNumber\":\"EMP-2020\"}";
    Person person = objectMapper.readValue(jsonString, Person.class);
    assertEquals("Jane", person.getFirstName());
    assertEquals("Doe", person.getLastName());
    assertEquals("EMP-2020", ((Employee)person).getEmployeeNumber());
        
    assertEquals(jsonString, writeToJson(objectMapper, person));
  }
  
  @Test 
  public void readsAndWrites_twoObjects() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Animal.class, Person.class).inSamePackage()
            .build();
    
    String jsonString = "{\"type\":\"Dog\",\"price\":412,\"name\":\"Calla\",\"command\":\"Sit\"}";
    Dog dog = (Dog) objectMapper.readValue(jsonString, Animal.class);
    assertEquals("Calla", dog.name);
    assertEquals("Sit", dog.command);
    assertEquals(jsonString, writeToJson(objectMapper, dog));
    
    jsonString = "{\"type\":\"Employee\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"employeeNumber\":\"EMP-2020\"}";
    Employee employee = (Employee) objectMapper.readValue(jsonString, Person.class);
    assertEquals("Jane", employee.getFirstName());
    assertEquals("Doe", employee.getLastName());
    assertEquals("EMP-2020", employee.getEmployeeNumber());
    assertEquals(jsonString, writeToJson(objectMapper, employee));
  }
  
  /*
   * Alternative path tests
   */
  
  @Test 
  public void readsAndWrites_twoObjects_longVersion() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Animal.class).inSamePackage()
          .subclassesOf(Person.class).inSamePackage()
            .build();
    
    String jsonString = "{\"type\":\"Dog\",\"price\":412,\"name\":\"Calla\",\"command\":\"Sit\"}";
    Dog dog = (Dog) objectMapper.readValue(jsonString, Animal.class);
    assertEquals("Calla", dog.name);
    assertEquals("Sit", dog.command);
    assertEquals(jsonString, writeToJson(objectMapper, dog));
    
    jsonString = "{\"type\":\"Employee\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"employeeNumber\":\"EMP-2020\"}";
    Employee employee = (Employee) objectMapper.readValue(jsonString, Person.class);
    assertEquals("Jane", employee.getFirstName());
    assertEquals("Doe", employee.getLastName());
    assertEquals("EMP-2020", employee.getEmployeeNumber());
  }
  
  /*
   * Error path tests
   */
  
  @Test(expected = InvalidTypeIdException.class)
  public void doesntRead_objectThatIsntSubclass() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Animal.class).inSamePackage()
            .build();
    
    String jsonString = "{\"type\":\"OrphanAnimal\",\"name\":\"Toad\"\"}";
    objectMapper.readValue(jsonString, Animal.class);
  }
  
  @Test(expected = InvalidTypeIdException.class)
  public void doesntRead_objectInWrongPackage() throws Exception {
    ObjectMapper objectMapper = 
        Moonwlker.objectMapperBuilder()
          .subclassesOf(Animal.class, Person.class).inSamePackage()
            .build();
    
    String jsonString = "{\"type\":\"StrayCat\",\"price\":1,\"name\":\"Bella\",\"nickname\":\"Bee\"}";
    objectMapper.readValue(jsonString, Animal.class);
  }
}