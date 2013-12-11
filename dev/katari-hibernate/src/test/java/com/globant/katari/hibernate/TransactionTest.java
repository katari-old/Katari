package com.globant.katari.hibernate;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** This is a test for the transaction handling.*/
public class TransactionTest {

  private PlatformTransactionManager transactionManager;
  private Transaction transaction;
  private TransactionStatus transactionStatus;

  @Before public void setUp() {
    transactionManager = createMock(PlatformTransactionManager.class);
    transactionStatus = createMock(TransactionStatus.class);
    transaction = new Transaction(transactionManager);
  }

  @Test public void testStart() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.commit(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.start();
    transaction.commit();
    verify(transactionManager, transactionStatus);
  }

  @Test public void testStartSerializable() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.commit(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.startSerializable();
    transaction.commit();
    verify(transactionManager, transactionStatus);
  }

  @Test public void test_rollback() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.rollback(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.start();
    transaction.rollback();
    verify(transactionManager, transactionStatus);
  }

  @Test public void test_cleanup() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.commit(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.start();
    transaction.commit();
    transaction.cleanup();
    verify(transactionManager, transactionStatus);
  }

  @Test public void test_cleanupWithRollback() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.rollback(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.start();
    transaction.cleanup();
    verify(transactionManager, transactionStatus);
  }

  @Test public void test_commitWithRollback() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    replay(transactionManager, transactionStatus);
    try {
      transaction.commit();
      fail("Should faild becasuse another tx is bound to this thread");
    } catch (IllegalStateException e) {
      transaction.cleanup();
    }
  }

  @Test public void testStart_twoTransactionSameThread() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.rollback(transactionStatus);
    replay(transactionManager, transactionStatus);
    transaction.start();
    try {
      transaction.start();
      fail("Should faild becasuse another tx is bound to this thread");
    } catch (IllegalStateException e) {
      transaction.cleanup();
    }
    verify(transactionManager, transactionStatus);
  }

  @Test(expected = IllegalStateException.class)
  public void start_existingTransaction() {
    TransactionSynchronizationManager.setActualTransactionActive(true);
    transaction.start();
  }

  @Test public void testRollback_withNoActiveTransaction() {
    expect(transactionManager.getTransaction(
        isA(TransactionDefinition.class))).andReturn(transactionStatus);
    transactionManager.rollback(transactionStatus);
    replay(transactionManager, transactionStatus);
    try {
      transaction.rollback();
      fail("Should faild because there are not any tx started");
    } catch (IllegalStateException e) {
      transaction.cleanup();
    }
  }
}

