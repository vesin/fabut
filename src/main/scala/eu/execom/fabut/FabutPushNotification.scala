package eu.execom.fabut

import eu.execom.fabut.pair.PushNotificationItem
import junit.framework.AssertionFailedError

import scala.collection.mutable.ListBuffer

trait FabutPushNotification extends InitFabut {

  /**
   * Custom equal method that needs to be implemented in testutil class
   **/
  def customEquals(expected: Any, actual: Any): Boolean

  override def beforeTest(): Unit = {
    super.beforeTest()

    // clean push notifications
    PushNotificationSnapshot.cleanUp()
  }

  override def afterTest(): Unit = {
    super.afterTest()

    // assert push notifications
    afterAssert()
  }

  /**
   * API method for asserting mails in Fabut
   **/
  def assertPushNotification(from: Any, to: Any, collapseKey: String) {
    var asserted = false
    for (message <- PushNotificationSnapshot.messages) {
      if (customEquals(message.from, from) && customEquals(message.to, to) && message.collapseKey == collapseKey && !message.asserted) {
        message.asserted = true
        asserted = true
        return
      }
    }
    if (!asserted) {
      throw new AssertionFailedError("No push notifications found to assert!")
    }
  }

  def afterAssert(): Unit = {
    val stringBuilder = new StringBuilder()
    var asserted = true
    for (message <- PushNotificationSnapshot.messages) {
      if (!message.asserted) {
        stringBuilder.append(s"\n Push notification sent but not asserted: from id: \n\t\t${message.from} \nto id:\n\t\t ${message.to} \nwith collapse key: ${message.collapseKey}")
        asserted = false
      }
    }

    if (!asserted) {
      throw new AssertionFailedError(stringBuilder.toString())
    }
  }
}

/**
 * Simulates a snapshot for notifications
 **/
object PushNotificationSnapshot {

  var messages = new ListBuffer[PushNotificationItem]

  def cleanUp(): Unit = {
    messages.clear()
  }

  def add(item: PushNotificationItem): Unit = {
    messages += item
  }
}

