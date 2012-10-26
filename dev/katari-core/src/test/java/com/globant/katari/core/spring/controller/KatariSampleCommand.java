package com.globant.katari.core.spring.controller;

import java.io.OutputStream;
import java.util.Date;

import com.globant.katari.core.application.Command;

public class KatariSampleCommand implements Command<Void> {

  private String name;
  private int age;
  private Date birthday;
  private OutputStream outputStream;

  public Void execute() {
    return null;
  }

  public int getAge() {
    return age;
  }

  public void setAge(final int theAge) {
    age = theAge;
  }

  public String getName() {
    return name;
  }

  public void setName(final String theName) {
    name = theName;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(final Date theBirthday) {
    birthday = theBirthday;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(final OutputStream theOutputStream) {
    outputStream = theOutputStream;
  }
}