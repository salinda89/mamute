package org.mamute.util;

import java.lang.reflect.Method;

import org.mamute.controllers.ListController;
import org.mamute.controllers.NewsController;
import org.mamute.controllers.QuestionController;
import org.mamute.controllers.UserProfileController;
import org.mamute.model.News;
import org.mamute.model.Question;
import org.mamute.model.Tag;
import org.mamute.model.User;
import org.mamute.model.interfaces.Watchable;
import org.mamute.vraptor.Env;

import br.com.caelum.vraptor.http.route.Router;
import net.vidageek.mirror.dsl.Mirror;

public class LinkToHelper {

  private final Router router;
  private final Env env;

  public LinkToHelper(Router router, Env env) {
    this.router = router;
    this.env = env;
  }

  public String mainThreadLink(Watchable watchable) {
    if (watchable.getType().isAssignableFrom(News.class)) {
      News news = (News) watchable;
      return urlFor(NewsController.class, "showNews", news, news.getSluggedTitle());
    } else {
      Question question = (Question) watchable;
      return urlFor(QuestionController.class, "showQuestion", question,
          question.getSluggedTitle());
    }
  }

  public String tagLink(Tag t) {
    return urlFor(ListController.class, "withTag", t.getName(), 1, false);
  }

  public String newsLink(News n) {
    return urlFor(NewsController.class, "showNews", n, n.getSluggedTitle());
  }

  public String questionLink(Question q) {
    return urlFor(QuestionController.class, "showQuestion", q, q.getSluggedTitle());
  }

  public String userLink(User u) {
    return urlFor(UserProfileController.class, "showProfile", u, u.getSluggedName());
  }

  public String unsubscribeLink(User user) {
    return urlFor(UserProfileController.class, "unsubscribe", user, user.getUnsubscribeHash());
  }


  private String urlFor(Class<?> clazz, String method,
      Object... args) {
    String relativePath = router.urlFor(clazz, method(clazz, method), args);
    return env.getHostAndContext() + relativePath;
  }

  private Method method(Class<?> clazz, String method) {
    return new Mirror().on(clazz).reflect().method(method).withAnyArgs();
  }
}
