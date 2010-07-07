package com.globant.katari.gadgetcontainer.application;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.Validate.notNull;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.application.Command;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

/**
 * Command that perform the search page execution.
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupCommand implements Command<GadgetGroup> {
  /** 
   */
  private static Logger log = LoggerFactory.getLogger(GadgetGroupCommand.class);
  
  /** {@link GadgetGroupRepository} the DAO for page.
   */
  private final GadgetGroupRepository gadgetGroupRepository;
  
  /** {@link ContextUserService} retrieve the current user.
   */
  private final ContextUserService userService;
  
  /** {@link TokenService} the open social token service implementation.
   */
  private final TokenService tokenService;
  
  /** {@link String} the page name to search.
   */
  private String groupName;
  
  /** Constructor.
   * 
   * @param thePageRepository {@link GadgetGroupRepository}. Can not be null.
   * @param theUserService {@link ContextUserService}. Can not be null.
   * @param theTokenService {@link TokenService}. Can not be null.
   */
  public GadgetGroupCommand(final GadgetGroupRepository thePageRepository, 
      final ContextUserService theUserService, 
      final TokenService theTokenService) {
    
    notNull(thePageRepository, "page repository can not be null");
    notNull(theUserService, "user service can not be null");
    notNull(theTokenService, "token service can not be null");
    
    gadgetGroupRepository = thePageRepository;
    userService = theUserService;
    tokenService = theTokenService;
  }
  
  /** @return @link{String} the groupName. Never returns null.
   */
  public String getGroupName() {
    return groupName;
  }
  
  /** @param name {@link String} the groupName to set
   */
  public void setGroupName(final String name) {
    groupName = name;
  }
  
  /** Retrieve the user id from the spring security context then find the 
   * page with the context user and the given {@link GadgetGroupCommand#groupName}.
   * 
   * @see com.globant.katari.core.application.Command#execute()
   * @throws CanvasException if the {@link GadgetGroupCommand#groupName} is blank.
   */
  public GadgetGroup execute() {

    if(isBlank(groupName)) {
      throw new IllegalArgumentException("groupName can not be blank");
    }
    String uid = userService.getCurrentUserId();
    log.debug("searching group name = " + groupName + "for the user:" + uid);
    GadgetGroup page = gadgetGroupRepository.findPage(uid, groupName);
    if(page != null) {
      Iterator<GadgetInstance> gadgets = page.getGadgets().iterator();
      while (gadgets.hasNext()) {
        GadgetInstance gadgetInstance = (GadgetInstance) gadgets.next();
        String token = tokenService.createSecurityToken(gadgetInstance);
        log.debug("generated new securityToken:" + token);
        gadgetInstance.associateToViewer(token, uid);
      }
    }
    return page;
  }
}
