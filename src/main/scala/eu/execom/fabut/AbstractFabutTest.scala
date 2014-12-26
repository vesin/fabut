package eu.execom.fabut

import org.junit.{After, Before}

trait AbstractFabutTest extends Fabut with IFabutTest {

  @Before
  override def beforeTest(): Unit = {
    Fabut.beforeTest(this)
  }

  @After
  override def afterTest(): Unit = {
    Fabut.afterTest()
  }
}
