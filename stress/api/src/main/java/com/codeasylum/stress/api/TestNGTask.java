/*
 * Copyright (c) 2007, 2008, 2009, 2010, 2011, 10212, 2013 David Berkman
 * 
 * This file is part of the CodeAsylum Code Project.
 * 
 * The CodeAsylum Code Project is free software, you can redistribute
 * it and/or modify it under the terms of GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * The CodeAsylum Code Project is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the the GNU Affero General Public
 * License, along with the CodeAsylum Code Project. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under the GNU Affero GPL version 3 section 7
 * ------------------------------------------------------------------
 * If you modify this Program, or any covered work, by linking or
 * combining it with other code, such other code is not for that reason
 * alone subject to any of the requirements of the GNU Affero GPL
 * version 3.
 */
package com.codeasylum.stress.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class TestNGTask extends AbstractTask {

  private Attribute<Class> testClassAttribute = new Attribute<Class>(Class.class, false);

  public TestNGTask () {

  }

  public TestNGTask (TestNGTask testNGTask) {

    super(testNGTask);

    testClassAttribute = new Attribute<Class>(Class.class, testNGTask.getTestClassAttribute());
  }

  public Attribute<Class> getTestClassAttribute () {

    return testClassAttribute;
  }

  public void setTestClassAttribute (Attribute<Class> testClassAttribute) {

    this.testClassAttribute = testClassAttribute;
  }

  @Override
  public void execute (long timeDifferential, int hostIndex, String hostId, Ouroboros ouroboros, ExchangeTransport exchangeTransport) throws Exception {

    if (isEnabled() && ouroboros.isEnabled()) {

      Class testClass;

      if ((testClass = testClassAttribute.get(this)) == null) {
        throw new TaskExecutionException("No test class was set");
      }
      else {

        CountDownLatch finishedLatch = new CountDownLatch(1);
        TestListenerAdapter testListenerAdapter = new JormungandrTestListenerAdapter(finishedLatch);
        TestNG testng = new TestNG();

        testng.setTestClasses(new Class[] {testClass});
        testng.addListener(testListenerAdapter);
        testng.run();

        while (!finishedLatch.await(1, TimeUnit.SECONDS)) {
          if (!(isEnabled() && ouroboros.isEnabled())) {
            finishedLatch.countDown();
          }
        }
      }
    }
  }

  @Override
  public Task deepCopy () {

    return new TestNGTask(this);
  }

  private static class JormungandrTestListenerAdapter extends TestListenerAdapter {

    private CountDownLatch finishedLatch;

    private JormungandrTestListenerAdapter (CountDownLatch finishedLatch) {

      this.finishedLatch = finishedLatch;
    }

    @Override
    public void onFinish (ITestContext testContext) {

      super.onFinish(testContext);
      finishedLatch.countDown();
    }
  }
}
