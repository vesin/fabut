package eu.execom.fabut

import org.junit.{After, Before}

/**
 * Every Fabut trait that tests an aspect of application must extend this trait and implement beforeTest() and afterTest()
 * methods that will be called in @Before and @After methods when unit tests start
 **/
trait InitFabut {
  /**
   * Method used for initialization of db and Fabut
   **/
  @Before
  def before(): Unit

  /**
   * Method for after test stream close ups, rollbacks etc.
   **/
  @After
  def after(): Unit

  /**
   * This method needs to be called in @Before method of a test in order for Fabut to work.
   */
  def beforeTest(): Unit = {}

  /**
   * This method needs to be called in @After method of a test in order for Fabut to work.
   */
  def afterTest(): Unit = {}
}
