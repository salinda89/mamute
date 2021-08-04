package org.mamute.notification;

import static org.joda.time.format.DateTimeFormat.forPattern;

import java.util.Locale;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.mamute.mail.AWSSimpleMailer;
import org.mamute.mail.action.EmailAction;
import org.mamute.model.User;
import org.mamute.util.LinkToHelper;
import org.mamute.vraptor.Env;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.simplemail.template.BundleFormatter;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;

public class NotificationMailer extends AWSSimpleMailer {

  private static final Logger LOG = Logger.getLogger(NotificationMailer.class);
  private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();

  @Inject
  private TemplateMailer templates;
  @Inject
  private Environment env;
  @Inject
  private Env brutalEnv;
  @Inject
  private Router router;
  @Inject
  private BundleFormatter bundle;

  public void send(NotificationMail notificationMail) throws EmailException, MessagingException {
    User to = notificationMail.getTo();
    Email email = buildEmail(notificationMail);
    email.setCharset("utf-8");
    try {
      sendEmail(email);
    } catch (Throwable e) {
      LOG.error("Could not send notifications mail to: " + to.getEmail(), e);
    }
  }

  public Email buildEmail(NotificationMail notificationMail) throws EmailException {
    DateTimeFormatter dateFormat = forPattern("MMM, dd").withLocale(new Locale("pt", "br"));
    EmailAction action = notificationMail.getAction();
    User to = notificationMail.getTo();
    Email email = templates
        .template(notificationMail.getEmailTemplate(), bundle.getMessage("site.name"),
            action.getMainThread().getTitle())
        .with("emailAction", action)
        .with("dateFormat", dateFormat)
        .with("sanitizer", POLICY)
        .with("bundle", bundle)
        .with("watcher", to)
        .with("linkerHelper", new LinkToHelper(router, brutalEnv))
        .with("logoUrl", env.get("mail_logo_url"))
        .to(to.getName(), to.getEmail());

    return email;
  }
}
