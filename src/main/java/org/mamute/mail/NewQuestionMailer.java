package org.mamute.mail;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.mail.Email;
import org.mamute.controllers.QuestionController;
import org.mamute.model.Question;
import org.mamute.model.User;
import org.mamute.vraptor.Linker;

import br.com.caelum.vraptor.environment.Property;
import br.com.caelum.vraptor.simplemail.template.BundleFormatter;
import br.com.caelum.vraptor.simplemail.template.TemplateMail;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;

public class NewQuestionMailer extends AWSSimpleMailer {

  private TemplateMailer templates;
  private BundleFormatter bundle;
  private String emailLogo;
  private Linker linker;

  @Deprecated
  NewQuestionMailer() {
  }

  @Inject
  public NewQuestionMailer(TemplateMailer templates,
      BundleFormatter bundle, Linker linker,
      @Property("mail_logo_url") String emailLogo) {
    this.templates = templates;
    this.bundle = bundle;
    this.linker = linker;
    this.emailLogo = emailLogo;
  }

  public void send(List<User> subscribed, Question question) {
    linker.linkTo(QuestionController.class).showQuestion(question, question.getSluggedTitle());
    String questionLink = linker.get();
    TemplateMail template = templates.template("new_question_notification")
        .with("question", question)
        .with("bundle", bundle)
        .with("questionLink", questionLink)
        .with("logoUrl", emailLogo);

    //TODO send asynchronously
    for (User user : subscribed) {
      boolean notSameAuthor = !user.equals(question.getAuthor());
      if (notSameAuthor) {
        Email email = template.to(user.getName(), user.getEmail());

        sendEmail(email);
      }
    }
  }


}
