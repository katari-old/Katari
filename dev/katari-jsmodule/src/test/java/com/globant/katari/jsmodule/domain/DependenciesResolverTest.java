package com.globant.katari.jsmodule.domain;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.jsmodule.domain.DependenciesFinder;
import com.globant.katari.jsmodule.domain.DependenciesResolver;


/** Represents the Test Cases for the {@link DependenciesResolver}.
 * <p>
 * The Test Cases follow this dependency graph:
 * <p>
 *       f
 *     /   \               circular       circular dependency
 *    /     \              dependency     u
 *   |       |                  p         |^
 *   a   b   |      c   d  e   ^ \        | \
 *  /|\ / \ /|     / \        /   \       v  \
 * h i j   k |    l   m       \   /      /| /
 *   |      /                  \ /      / |/
 *   x_____/                    q      z   w
 */

public class DependenciesResolverTest {

  private DependenciesFinder dependenciesFinder;

  private  DependenciesResolver resolver;

  private List<String> noDeps;

  private List<String> files;

  private List<String> result;

  private List<String> expectedResult;

  private List<String> bDeps;

  private List<String> jDeps;

  private List<String> kDeps;

  private List<String> cDeps;

  private List<String> lDeps;

  private List<String> mDeps;

  private List<String> aDeps;

  private List<String> iDeps;

  private List<String> hDeps;

  private List<String> xDeps;

  private List<String> fDeps;

  private List<String> pDeps;

  private List<String> qDeps;

  private List<String> uDeps;

  private List<String> vDeps;

  private List<String> wDeps;

  private List<String> zDeps;

  @Before
  public void setUp() {
    dependenciesFinder =
        createMock(DependenciesFinder.class);
    resolver =
        new DependenciesResolver(dependenciesFinder);
    noDeps = new ArrayList<String>();
    files = new ArrayList<String>();
    bDeps = new ArrayList<String>();
    bDeps.add("j.js");
    bDeps.add("k.js");
    jDeps = noDeps;
    kDeps = noDeps;
    lDeps = noDeps;
    mDeps = noDeps;
    cDeps = new ArrayList<String>();
    cDeps.add("l.js");
    cDeps.add("m.js");
    aDeps = new ArrayList<String>();
    aDeps.add("h.js");
    aDeps.add("i.js");
    aDeps.add("j.js");
    iDeps = new ArrayList<String>();
    iDeps.add("x.js");
    hDeps = noDeps;
    xDeps = noDeps;
    result = new ArrayList<String>();
    expectedResult = new ArrayList<String>();
    fDeps = new ArrayList<String>();
    fDeps.add("a.js");
    fDeps.add("k.js");
    fDeps.add("x.js");
    pDeps = new ArrayList<String>();
    pDeps.add("q.js");
    qDeps = new ArrayList<String>();
    qDeps.add("p.js");
    uDeps = new ArrayList<String>();
    uDeps.add("v.js");
    vDeps = new ArrayList<String>();
    vDeps.add("z.js");
    vDeps.add("w.js");
    wDeps = new ArrayList<String>();
    wDeps.add("u.js");
    zDeps = noDeps;
  }

  @Test (expected = IllegalArgumentException.class)
  public void create_nullResolver() {
    new DependenciesResolver(null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void resolve_nullList() {
    (new DependenciesResolver(dependenciesFinder)).resolve(null);
  }



  @Test
  public void resolve_noDepsEmpty() {
    result = resolver.resolve(files);
    expectedResult = files;
    assertThat(result, is(expectedResult));
  }

  @Test
  public void resolve_noDepsMultiple() {

    expect(dependenciesFinder.find("d.js")).andReturn(noDeps);
    expect(dependenciesFinder.find("e.js")).andReturn(noDeps);
    replay(dependenciesFinder);

    files.add("d.js");
    files.add("e.js");

    result = resolver.resolve(files);
    expectedResult = files;
    assertThat(result, is(expectedResult));
    verify(dependenciesFinder);
  }

  @Test
  public void resolve_depsSingle() {

    expect(dependenciesFinder.find("b.js")).andReturn(bDeps);

    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);
    expect(dependenciesFinder.find("k.js")).andReturn(kDeps);

    replay(dependenciesFinder);

    files.add("b.js");

    result = resolver.resolve(files);
    expectedResult = Arrays.asList("j.js", "k.js", "b.js");
    assertThat(result, is(expectedResult));

    verify(dependenciesFinder);
  }

  @Test
  public void resolve_depsMultiple() {

    expect(dependenciesFinder.find("b.js")).andReturn(bDeps);

    expect(dependenciesFinder.find("c.js")).andReturn(cDeps);

    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);
    expect(dependenciesFinder.find("k.js")).andReturn(kDeps);

    expect(dependenciesFinder.find("l.js")).andReturn(lDeps);
    expect(dependenciesFinder.find("m.js")).andReturn(mDeps);

    replay(dependenciesFinder);

    files.add("b.js");
    files.add("c.js");

    result = resolver.resolve(files);
    expectedResult.addAll(bDeps);
    expectedResult.add("b.js");
    expectedResult.addAll(cDeps);
    expectedResult.add("c.js");
    expectedResult =
        Arrays.asList("j.js", "k.js", "b.js", "l.js", "m.js", "c.js");
    assertThat(result, is(expectedResult));

    verify(dependenciesFinder);
  }

  @Test
  public void resolve_depsSingleDeep() {

    expect(dependenciesFinder.find("a.js")).andReturn(aDeps);

    expect(dependenciesFinder.find("i.js")).andReturn(iDeps);

    expect(dependenciesFinder.find("h.js")).andReturn(hDeps);
    expect(dependenciesFinder.find("x.js")).andReturn(xDeps);
    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);

    replay(dependenciesFinder);

    files.add("a.js");

    result = resolver.resolve(files);
    expectedResult = Arrays.asList("h.js", "x.js", "i.js", "j.js", "a.js");
    assertThat(result, is(expectedResult));

    verify(dependenciesFinder);
  }

  @Test
  public void resolve_depsMultipleDeep() {

    expect(dependenciesFinder.find("a.js")).andReturn(aDeps);

    expect(dependenciesFinder.find("i.js")).andReturn(iDeps);

    expect(dependenciesFinder.find("h.js")).andReturn(hDeps);
    expect(dependenciesFinder.find("x.js")).andReturn(xDeps);
    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);

    expect(dependenciesFinder.find("b.js")).andReturn(bDeps);

    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);
    expect(dependenciesFinder.find("k.js")).andReturn(kDeps);

    replay(dependenciesFinder);

    files.add("a.js");
    files.add("b.js");

    result = resolver.resolve(files);
    expectedResult =
        Arrays.asList("h.js", "x.js", "i.js", "j.js", "a.js", "k.js", "b.js");
    assertThat(result, is(expectedResult));

    verify(dependenciesFinder);
  }

  @Test
  public void resolve_depsComplexDeep() {

    // dependencies that will be called by file a.js
    expect(dependenciesFinder.find("a.js")).andReturn(aDeps);
    expect(dependenciesFinder.find("h.js")).andReturn(hDeps);
    expect(dependenciesFinder.find("x.js")).andReturn(xDeps);
    expect(dependenciesFinder.find("i.js")).andReturn(iDeps);
    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);

    // dependencies that will be called by file b.js
    expect(dependenciesFinder.find("b.js")).andReturn(bDeps);
    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);
    expect(dependenciesFinder.find("k.js")).andReturn(kDeps);

    // dependencies that will be called by file f.js
    expect(dependenciesFinder.find("f.js")).andReturn(fDeps);
    expect(dependenciesFinder.find("a.js")).andReturn(aDeps);
    expect(dependenciesFinder.find("h.js")).andReturn(hDeps);
    expect(dependenciesFinder.find("i.js")).andReturn(iDeps);
    expect(dependenciesFinder.find("x.js")).andReturn(xDeps);
    expect(dependenciesFinder.find("j.js")).andReturn(jDeps);
    expect(dependenciesFinder.find("k.js")).andReturn(kDeps);

    expect(dependenciesFinder.find("x.js")).andReturn(xDeps);

    replay(dependenciesFinder);

    files.add("a.js");
    files.add("b.js");
    files.add("f.js");

    result = resolver.resolve(files);
    expectedResult =
        Arrays.asList("h.js", "x.js", "i.js", "j.js", "a.js", "k.js", "b.js",
            "f.js");
    assertThat(result, is(expectedResult));

    verify(dependenciesFinder);
  }

  @Test (expected = RuntimeException.class)
  public void resolve_singleCircularDependency() {
    expect(dependenciesFinder.find("p.js")).andReturn(pDeps);
    expect(dependenciesFinder.find("q.js")).andReturn(qDeps);
    replay(dependenciesFinder);

    files.add("p.js");

    result = resolver.resolve(files);
  }

  @Test (expected = RuntimeException.class)
  public void resolve_deepCircularDependency() {
    expect(dependenciesFinder.find("u.js")).andReturn(uDeps);
    expect(dependenciesFinder.find("v.js")).andReturn(vDeps);
    expect(dependenciesFinder.find("w.js")).andReturn(wDeps);
    expect(dependenciesFinder.find("z.js")).andReturn(zDeps);
    replay(dependenciesFinder);

    files.add("u.js");

    result = resolver.resolve(files);
  }
}
