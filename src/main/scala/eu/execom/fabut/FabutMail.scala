package eu.execom.fabut

import eu.execom.fabut.pair.AssertMail
import junit.framework.AssertionFailedError
import org.subethamail.wiser.Wiser
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Mail asserting extension of Fabut
 */
trait FabutMail extends InitFabut {

  var assertMails = new ListBuffer[AssertMail]
  var wiserMails = new ListBuffer[AssertMail]

  /**
   *  Needs to be implemented in test
   */
  def wiser: Wiser

  override def beforeTest(): Unit = {
    super.beforeTest()

    //cleaning wiser
    assertMails.clear()
    wiser.getMessages.clear()
    wiserMails.clear()
  }

  override def afterTest(): Unit = {
    super.afterTest()

    assertWiserMessages()
  }


  def assertMail(to: String, subject: String) {
    assertMails = assertMails :+ new AssertMail(to, subject, false)
  }

  /**
   * Asserts mails sent by the test.
   */
  def assertWiserMessages() = {

    wiserMails.clear()
    wiserMails = wiserMails ++ wiser.getMessages.map( x => new AssertMail(x.getMimeMessage.getAllRecipients()(0).toString, x.getMimeMessage.getSubject, false))

    for (wiserMail <- wiserMails) {
      for (assertMail <- assertMails) {
        if (!wiserMail.asserted && !assertMail.asserted && wiserMail.to.equals(assertMail.to) && wiserMail.subject.equals(assertMail.subject)) {
          wiserMail.asserted = true
          assertMail.asserted = true
        }
      }
    }

    val sb = new StringBuffer
    for (wiserMail <- wiserMails) {
      if (!wiserMail.asserted) {
        sb.append(s"Mail recieved but not asserted in test: sent to: ${wiserMail.to} with subject ${wiserMail.subject} \n")
      }
    }

    for (assertMail <- assertMails) {
      if (!assertMail.asserted) {
        sb.append(s"Mail never recieved in test: to ${assertMail.to} with subject ${assertMail.subject} \n")
      }
    }

    if (sb.length() > 0) {
      sb.insert(0, "\n")
      throw new AssertionFailedError(sb.toString)
    }
  }
}



