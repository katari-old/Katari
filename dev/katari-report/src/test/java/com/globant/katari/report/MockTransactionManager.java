package com.globant.katari.report;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

public class MockTransactionManager implements PlatformTransactionManager{

  public void commit(TransactionStatus arg0) throws TransactionException {
    // TODO Auto-generated method stub
  }

  public TransactionStatus getTransaction(TransactionDefinition arg0)
      throws TransactionException {
    return null;
  }

  public void rollback(TransactionStatus arg0) throws TransactionException {
  }

  static class MockTransactionStatus implements TransactionStatus {

    public boolean hasSavepoint() {
      return false;
    }

    public boolean isCompleted() {
      return false;
    }

    public boolean isNewTransaction() {
      return false;
    }

    public boolean isRollbackOnly() {
      return false;
    }

    public void setRollbackOnly() {
      // TODO Auto-generated method stub
    }

    public Object createSavepoint() throws TransactionException {
      // TODO Auto-generated method stub
      return null;
    }

    public void releaseSavepoint(Object arg0) throws TransactionException {
      // TODO Auto-generated method stub
    }

    public void rollbackToSavepoint(Object arg0) throws TransactionException {
      // TODO Auto-generated method stub
    }
  }
}

