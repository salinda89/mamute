package org.mamute.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

import br.com.caelum.vraptor.environment.Property;

/**
 * @author Salinda Karunarathna
 * @since 7/15/2021
 */
public class AWSSimpleMailer {

  @Property("aws.simplemail.main.from")
  @Inject
  private String from;

  public void sendEmail(final Email mail) {
    try {
      AmazonSimpleEmailService client =
          AmazonSimpleEmailServiceClientBuilder.standard()
              .withRegion(Regions.EU_WEST_2).build();
      mail.setFrom(from);

      //Adding this as a workaround for the legacy library
      mail.setMailSession(Session.getInstance(new Properties()));
      SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(mail2Content(mail));

      client.sendRawEmail(rawEmailRequest);
      System.out.println("Email sent!");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (MessagingException e) {
      e.printStackTrace();
    } catch (EmailException e) {
      e.printStackTrace();
    }
  }

  private RawMessage mail2Content(Email email)
      throws IOException, MessagingException, EmailException {
    email.buildMimeMessage();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    email.getMimeMessage().writeTo(out);
    return (new RawMessage()).withData(ByteBuffer.wrap(out.toByteArray()));
  }
}
