/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.domain;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.quartz.JobDetail;
import org.springframework.beans.factory.BeanFactory;

public class CronTriggerTest {

  @Test
  public void test() throws Exception {
    BeanFactory beanFactory = createMock(BeanFactory.class);
    expect(beanFactory.getType("job")).andReturn(ScheduledCommand.class);
    replay(beanFactory);
    
    CronTrigger trigger = new CronTrigger("0 0 0 * * ?", "job");
    trigger.setBeanFactory(beanFactory);
    trigger.setBeanClassLoader(getClass().getClassLoader());
    trigger.setBeanName("job");

    trigger.afterPropertiesSet();
    
    JobDetail jobDetail = trigger.getJobDetail();
    assertThat(jobDetail.getGroup(), is("DEFAULT"));
    assertThat(jobDetail.getName(), is("job"));
  }
}

