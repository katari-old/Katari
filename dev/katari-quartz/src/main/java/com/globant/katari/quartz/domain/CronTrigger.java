/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.domain;

import java.text.ParseException;

import org.apache.commons.lang.Validate;

import org.quartz.JobDetail;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/** A lazy initializer cron trigger for quartz.
 *
 * This is a quartz trigger that lazily initializes the task to run. This is
 * used to guarantee that the task does not force premature initialization of
 * the application context.
 *
 * It uses the cron syntax to specify when the task should run.
 */
public class CronTrigger extends CronTriggerBean implements BeanFactoryAware,
    InitializingBean, BeanClassLoaderAware, BeanNameAware {

  /** The serial version uid, used for serialization.
   */
  private static final long serialVersionUID = 1L;

  /** The bean factory that contains this cron trigger.
   *
   * This is used to create the lazy scheduled command. It must be set by
   * spring to a non null value.
   */
  private BeanFactory beanFactory;

  /** Classloader passed by spring through the implementation of
   *  BeanClassLoaderAware.
   *
   *  This is used by ProxyFactoryBean. It is set by spring to a non null
   *  value.
   */
  private ClassLoader classLoader;

  /** The bean name under which this object is registered in spring.
   *
   * This is used by MethodInvokingJobDetailFactoryBean. Set by spring to a non
   * null value.
   */
  private String beanName;

  /** The cron expression that defines when the task is run, as defined by
   * quartz's CronTrigger.
   *
   * This is never null.
   */
  private final String cronExpression;

  /** The spring bean name of the task to run.
   *
   * The bean must implement the ScheduledCommand interface. It is never null.
   */
  private final String taskBeanName;

  /** Constructor.
   *
   * @param theCronExpression The cron expression that defines when the task is
   * run. See CronTrigger class in quartz for the syntax. It cannot be null.
   *
   * @param theTaskBeanName The name of the spring bean for the task. The bean
   * must implement the ScheduledCommand interface. It cannot be null.
   */
  public CronTrigger(final String theCronExpression, final String
      theTaskBeanName) {
    Validate.notNull(theCronExpression, "The cron expression cannot be null.");
    Validate.notNull(theTaskBeanName, "The task bean name cannot be null.");
    cronExpression = theCronExpression;
    taskBeanName = theTaskBeanName;
  }

  /** Operation from interface BeanFactoryAware.
   *
   * {@inheritDoc}
   */
  public void setBeanFactory(final BeanFactory theBeanFactory) {
    beanFactory = theBeanFactory;
  }

  /** Operation from interface BeanClassLoaderAware.
   *
   * {@inheritDoc}
   */
  public void setBeanClassLoader(final ClassLoader theClassLoader) {
    classLoader = theClassLoader;
  }

  /** Operation from BeanNameAware.
   *
   * {@inheritDoc}
   */
  public void setBeanName(final String name) {
    beanName = name;
    super.setBeanName(name);
  }

  /** Operation from InitializingBean.
   *
   * All initilization work is done here.
   *
   * {@inheritDoc}
   *
   * @throws ParseException if the cron expression is invalid. Throwing
   * ParseException is just inherited from CronTriggerBean. It should be able
   * to throw more exceptions, because current implementation throws
   * RuntimeException on other problems.
   */
  public void afterPropertiesSet() throws ParseException {

    LazyInitTargetSource lazyInitScheduledCommand = new LazyInitTargetSource();
    lazyInitScheduledCommand.setTargetBeanName(taskBeanName);

    lazyInitScheduledCommand.setBeanFactory(beanFactory);

    ProxyFactoryBean scheduledCommandProxy = new ProxyFactoryBean();
    scheduledCommandProxy.setBeanClassLoader(classLoader);
    scheduledCommandProxy.setBeanFactory(beanFactory);

    scheduledCommandProxy.setTargetSource(lazyInitScheduledCommand);

    // This is just for convenience, we reuse springs wrapper for a job detail
    // through the MethodInvokingJobDetailFactoryBean.
    MethodInvokingJobDetailFactoryBean jobFactory;
    jobFactory = new MethodInvokingJobDetailFactoryBean();
    jobFactory.setBeanClassLoader(classLoader);
    jobFactory.setBeanFactory(beanFactory);
    jobFactory.setBeanName(beanName);
    jobFactory.setConcurrent(false);
    jobFactory.setTargetObject(scheduledCommandProxy.getObject());
    jobFactory.setTargetMethod("execute");
    try {
      jobFactory.afterPropertiesSet();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error in afterPropertiesSet of jobFactory",
          e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Error in afterPropertiesSet of jobFactory",
          e);
    }

    setCronExpression(cronExpression);

    setJobDetail((JobDetail) jobFactory.getObject());

    super.afterPropertiesSet();
  }
}

