package org.lc.comm.mail.sendgrid;

public class SendGridException extends Exception {
  public SendGridException(Exception e) {
    super(e);
  }
}
