/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.domain;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.quartz.JobDetail;
import org.springframework.beans.factory.BeanFactory;

public class CronTriggerTest {

  @SuppressWarnings("unchecked")
  @Test
  public void getJobDetail() throws Exception {
    BeanFactory beanFactory = createMock(BeanFactory.class);
    Class<?> beanType = ScheduledCommand.class;
    expect((Class) beanFactory.getType("job")).andReturn(beanType);
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

