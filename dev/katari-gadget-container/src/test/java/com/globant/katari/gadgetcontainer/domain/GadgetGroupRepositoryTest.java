package com.globant.katari.gadgetcontainer.domain;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.globant.katari.gadgetcontainer.SpringTestUtils;

/**
 * Test for the repository {@link GadgetGroupRepository}
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupRepositoryTest {
  
  private static final String REPOSITORY = "social.gadgetGroupRepository";
  private GadgetGroupRepository gadgetGroupRepository;
  private XmlWebApplicationContext appContext;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    appContext = SpringTestUtils.getContext();
    gadgetGroupRepository = (GadgetGroupRepository) appContext.getBean(REPOSITORY);
  }

  /** This test, persist a new page, and the search it back in the db.
   *  check that the page has the same attributes.
   */
  @Test
  public void testFindPage() {
    String userId = randomUUID().toString();
    String pageName = randomUUID().toString();
    String url = "http://" + randomUUID().toString();
    storePage(userId, pageName, url, "1#3");
    
    GadgetGroup thePage = gadgetGroupRepository.findPage(userId, pageName);
    
    assertNotNull(thePage);
    assertFalse(thePage.getGadgets().isEmpty());
    assertTrue(pageName.equals(thePage.getName()));
    assertTrue(thePage.getGadgets().iterator().next().getUrl().equals(url));
  }
  
  /** This test, persist a new page, and the search it back in the db.
   *  check that the page has the same attributes.
   */
  @Test
  public void testFindPageNonExist() {
    GadgetGroup thePage = gadgetGroupRepository.findPage("nonExist", "nonExist");
    assertNull(thePage);
  }
  
  /** This test creates a new page, then change the attribute of one of his
   *  gadgets instances, then check the changes performed over the 
   *  gadget instance.
   */
  @Test
  public void testUpdateGadgetInstance() {
    String userId = randomUUID().toString();
    String pageName = randomUUID().toString();
    String url = "http://" + randomUUID().toString();
    
    storePage(userId, pageName, url, "1#2");
    
    String gadgetNewPosition = "3#3";
    
    GadgetGroup page = gadgetGroupRepository.findPage(userId, pageName);
    
    GadgetInstance i = page.getGadgets().iterator().next();
    i.move(gadgetNewPosition);
    
    gadgetGroupRepository.savePage(page);
    
    page = gadgetGroupRepository.findPage(userId, pageName);
    
    assertTrue(page.getGadgets().iterator().next().getGadgetPosition().equals(
        gadgetNewPosition));
  }
  
  /** Store a new page in the database.
   * 
   * @param userId
   * @param pageName
   * @param gadgetUrl
   * @param gadgetPosition
   */
  private void storePage(final String userId, final String pageName,
      final String gadgetUrl, final String gadgetPosition) {
    Set<GadgetInstance> instanceSet = new HashSet<GadgetInstance>();
    instanceSet.add(new GadgetInstance(userId, gadgetUrl,  gadgetPosition));
    GadgetGroup page = new GadgetGroup(userId, pageName, instanceSet);
    gadgetGroupRepository.savePage(page);
  }
  
}
