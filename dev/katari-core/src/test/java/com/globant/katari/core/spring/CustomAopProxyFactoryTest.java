package com.globant.katari.core.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

import com.globant.katari.core.spring.ConstructorArgumentsBeanNameAutoProxyCreator;

public class CustomAopProxyFactoryTest extends TestCase {

  public void testProxyClassNoCglibProxy() throws Throwable {
    ConfigurableListableBeanFactory bf = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);

    AdvisedSupport support = EasyMock.createMock(AdvisedSupport.class);

    InvocationHandler ih = EasyMock.createMock(InvocationHandler.class);
    EasyMock.expect(
        ih.invoke(EasyMock.anyObject(), (Method) EasyMock.anyObject(),
            (Object[]) EasyMock.anyObject())).andReturn(true);

    AopProxy proxy = (AopProxy) Proxy
        .newProxyInstance(getClass().getClassLoader(), new Class[] {
            SpringProxy.class, AopProxy.class }, ih);

    AopProxyFactory proxyFactory = EasyMock.createMock(AopProxyFactory.class);
    AopProxyFactory factory = new ConstructorArgumentsBeanNameAutoProxyCreator.ConstructorArgsAopProxyFactory(
        bf, "someBean", proxyFactory);
    EasyMock.expect(proxyFactory.createAopProxy(support)).andReturn(proxy);

    EasyMock.replay(bf);
    EasyMock.replay(support);
    EasyMock.replay(ih);
    EasyMock.replay(proxyFactory);

    assertEquals(proxy, factory.createAopProxy(support));

    EasyMock.verify(bf);
    EasyMock.verify(support);
    EasyMock.verify(ih);
    EasyMock.verify(proxyFactory);

  }

  public void testProxyClassWithDefaultConstructor() throws Throwable {
    Class< ? > targetClass = getClass();
    ConfigurableListableBeanFactory bf = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);

    AdvisedSupport support = EasyMock.createMock(AdvisedSupport.class);
    EasyMock.expect(support.getTargetClass()).andReturn(targetClass);

    AopProxy proxy = EasyMock.createMock(AopProxy.class);

    AopProxyFactory proxyFactory = EasyMock.createMock(AopProxyFactory.class);
    AopProxyFactory factory = new ConstructorArgumentsBeanNameAutoProxyCreator.ConstructorArgsAopProxyFactory(
        bf, "someBean", proxyFactory);
    EasyMock.expect(proxyFactory.createAopProxy(support)).andReturn(proxy);

    EasyMock.replay(bf);
    EasyMock.replay(support);
    EasyMock.replay(proxyFactory);

    assertEquals(proxy, factory.createAopProxy(support));

    EasyMock.verify(bf);
    EasyMock.verify(support);
    EasyMock.verify(proxyFactory);
  }

  public void testProxyClassWithNoDefaultConstructor() throws Throwable {

    ConstructorArgumentValues values = EasyMock
        .createMock(ConstructorArgumentValues.class);
    EasyMock.expect(values.getArgumentCount()).andReturn(1).anyTimes();
    EasyMock.expect(values.getIndexedArgumentValue(0, Behavior.class))
        .andReturn(new ConstructorArgumentValues.ValueHolder("Test"));

    BeanDefinition mockBd = EasyMock.createMock(BeanDefinition.class);
    EasyMock.expect(mockBd.getConstructorArgumentValues()).andReturn(values)
        .times(2);

    ConfigurableListableBeanFactory bf = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);
    EasyMock.expect(bf.getBeanDefinition("someBean")).andReturn(mockBd)
        .times(2);

    AdvisedSupport support = EasyMock.createMock(AdvisedSupport.class);
    EasyMock.expect(support.getTargetClass()).andReturn(MockAopProxy.class)
        .anyTimes();

    AopProxy proxy = new MockAopProxy(
        new ExpectBehavior< Object >(new Object[] {new Object[] {"Test" },
            new Class[] {MockAopProxy.class } }));

    AopProxyFactory proxyFactory = EasyMock.createMock(AopProxyFactory.class);
    AopProxyFactory factory = new ConstructorArgumentsBeanNameAutoProxyCreator.ConstructorArgsAopProxyFactory(
        bf, "someBean", proxyFactory);
    EasyMock.expect(proxyFactory.createAopProxy(support)).andReturn(proxy);

    EasyMock.replay(bf);
    EasyMock.replay(support);
    EasyMock.replay(proxyFactory);
    EasyMock.replay(mockBd);
    EasyMock.replay(values);

    assertEquals(proxy, factory.createAopProxy(support));

    EasyMock.verify(bf);
    EasyMock.verify(support);
    EasyMock.verify(proxyFactory);
    EasyMock.verify(mockBd);
    EasyMock.verify(values);
  }

  public void testProxyClassWithTwoNonDefaultConstructor() throws Throwable {

    ConstructorArgumentValues values = EasyMock
        .createMock(ConstructorArgumentValues.class);
    EasyMock.expect(values.getArgumentCount()).andReturn(2).anyTimes();
    EasyMock.expect(values.getIndexedArgumentValue(0, Behavior.class))
        .andReturn(new ConstructorArgumentValues.ValueHolder("Test"));
//    EasyMock.expect(values.getIndexedArgumentValue(1, String.class)).andReturn(
        //new ConstructorArgumentValues.ValueHolder("Test"));

    BeanDefinition mockBd = EasyMock.createMock(BeanDefinition.class);
    EasyMock.expect(mockBd.getConstructorArgumentValues()).andReturn(values)
        .times(2);

    ConfigurableListableBeanFactory bf = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);
    EasyMock.expect(bf.getBeanDefinition("someBean")).andReturn(mockBd)
        .times(2);

    AdvisedSupport support = EasyMock.createMock(AdvisedSupport.class);
    EasyMock.expect(support.getTargetClass()).andReturn(MockAopProxy.class)
        .anyTimes();

    AopProxy proxy = new MockAopProxy(
        new ExpectBehavior< Object >(new Object[] {new Object[] {"Test" },
            new Class[] {MockAopProxy.class } }));

    AopProxyFactory proxyFactory = EasyMock.createMock(AopProxyFactory.class);
    AopProxyFactory factory = new ConstructorArgumentsBeanNameAutoProxyCreator.ConstructorArgsAopProxyFactory(
        bf, "someBean", proxyFactory);
    EasyMock.expect(proxyFactory.createAopProxy(support)).andReturn(proxy);

    EasyMock.replay(bf);
    EasyMock.replay(support);
    EasyMock.replay(proxyFactory);
    EasyMock.replay(mockBd);
    EasyMock.replay(values);

    assertEquals(proxy, factory.createAopProxy(support));

    EasyMock.verify(bf);
    EasyMock.verify(support);
    EasyMock.verify(proxyFactory);
    EasyMock.verify(mockBd);
    EasyMock.verify(values);
  }

  public void testProxyClassWithNoSetConstructorArgumentsMethod()
      throws Throwable {

    ConfigurableListableBeanFactory bf = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);

    AdvisedSupport support = EasyMock.createMock(AdvisedSupport.class);
    EasyMock.expect(support.getTargetClass()).andReturn(MockAopProxy.class)
        .anyTimes();

    AopProxy proxy = EasyMock.createMock(AopProxy.class);

    AopProxyFactory proxyFactory = EasyMock.createMock(AopProxyFactory.class);
    AopProxyFactory factory = new ConstructorArgumentsBeanNameAutoProxyCreator.ConstructorArgsAopProxyFactory(
        bf, "someBean", proxyFactory);
    EasyMock.expect(proxyFactory.createAopProxy(support)).andReturn(proxy);

    EasyMock.replay(bf);
    EasyMock.replay(support);
    EasyMock.replay(proxyFactory);
    EasyMock.replay(proxy);

    assertEquals(proxy, factory.createAopProxy(support));

    EasyMock.verify(bf);
    EasyMock.verify(support);
    EasyMock.verify(proxyFactory);
    EasyMock.verify(proxy);
  }

  /**
   * Mock implementation of an AopProxy that contains a method with the same
   * signature as Cglib2AopProxy's setConstructorArguments.
   *
   * @author pablo.saavedra
   */
  private static class MockAopProxy implements AopProxy {

    private Behavior< ? > behavior;

    public MockAopProxy(Behavior< ? > behavior) {
      this.behavior = behavior;
    }

    @SuppressWarnings("unused")
    public MockAopProxy(Behavior< ? > behavior, String something) {
      this.behavior = behavior;
    }

    public Object getProxy() {
      return null;
    }

    public Object getProxy(ClassLoader classLoader) {
      return null;
    }

    @SuppressWarnings("unused")
    public void setConstructorArguments(Object[] arguments,
        Class< ? >[] argumentTypes) throws Throwable {
      if (this.behavior != null) {
        this.behavior.execute(arguments, argumentTypes);
      }
    }
  }

  private static abstract class Behavior<T> {
    public abstract T execute(Object... params) throws Throwable;
  }

  private static class ExpectBehavior<T> extends Behavior< Void > {

    private Object[] expects;

    public ExpectBehavior(Object[] expectations) {
      this.expects = expectations;
    }

    @Override
    public Void execute(Object... params) throws Throwable {
      if (params == null && expects == null) {
        return null;
      }
      if (params.length != expects.length) {
        throw new IllegalArgumentException(
            "Parameters don't match expectations");
      }
      for (int i = 0; i < params.length; i++) {
        if (!params[i].equals(expects[i])) {
          throw new IllegalArgumentException(
              "Parameters don't match expectations");
        }
      }
      return null;
    }
  }
}
