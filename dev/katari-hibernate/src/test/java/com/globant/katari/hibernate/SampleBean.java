package com.globant.katari.hibernate;

import java.util.List;

/** Just a sample bean for testing. */
public class SampleBean {

  /** Some beans!. */
  private List<SampleBean> beans;

  /** Just a name. */
  private String name;

  /** dark vader.*/
  public SampleBean(final String aName) {
    name = aName;
  }

  /** new instance.
   * @param someBeans some beans to inject.
   */
  public SampleBean(final List<SampleBean> someBeans) {
    beans = someBeans;
    name = "Hello, I have a list ()";
  }

  /** Retrieves the list of beans.
   * @return the list of beans.
   */
  public List<SampleBean> getBeans() {
    return beans;
  }

  /** retrieves the name.
   * @return the name
   */
  public String getName() {
    return name;
  }

}