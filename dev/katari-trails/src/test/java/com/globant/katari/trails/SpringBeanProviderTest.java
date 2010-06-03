package com.globant.katari.trails;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.trails.persistence.PersistenceService;

/**
 * SpringBeanProvider test class.
 * @author jimena.garbarino
 */
public class SpringBeanProviderTest extends TestCase{

  /**
   * Spring bean provider.
   */
  protected SpringBeanProvider springBeanProvider;
  protected ApplicationContext applicationContext;
  protected PersistenceService persistenceService;  

  @Override
  protected void setUp() throws Exception {
    applicationContext = EasyMock.createMock(ApplicationContext.class);
    persistenceService = EasyMock.createMock(PersistenceService.class);
    EasyMock.expect(applicationContext.getBean("persistenceService"))
        .andReturn(persistenceService);
    EasyMock.expect(applicationContext.getBean("thisBeanDoesntExist"))
    .andThrow(new NoSuchBeanDefinitionException(""));
    EasyMock.replay(persistenceService);
    EasyMock.replay(applicationContext);
    springBeanProvider = new SpringBeanProvider();
    springBeanProvider.setApplicationContext(applicationContext);
  }  
  
  /**
   * Tests SpringBeanProvider.getBean(String). 
   */
  public void testGetBean() {
    PersistenceService returnedPersistenceService =
        (PersistenceService) springBeanProvider.getBean("persistenceService");
    assertSame(persistenceService, returnedPersistenceService);
  }
  
  /**
   * Tests SpringBeanProvider.getBean(String) when the no bean exists with that name.
   */
  public void testGetNonExistentBean(){
    try {
      springBeanProvider.getBean("thisBeanDoesntExist");
      fail("The bean doesnt exist and must throw an NosuchBeanDefinition exception");
    } catch (NoSuchBeanDefinitionException e){
      // do nothing
    }
  }
  
  /**
   * Tests setBean call.
   */
  public void testSetBean(){
    try {
      springBeanProvider.setBean(null, null);
      fail("setBean method must throw a RuntimeException");
    } catch(RuntimeException e){
      assertEquals("setBean method should never be called.",e.getMessage());
    }
  }
}
