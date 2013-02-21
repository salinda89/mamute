package br.com.caelum.brutal.controllers;

import org.joda.time.LocalDate;

import br.com.caelum.brutal.dao.QuestionDAO;
import br.com.caelum.brutal.dao.UserDAO;
import br.com.caelum.brutal.model.LoggedUser;
import br.com.caelum.brutal.model.User;
import br.com.caelum.brutal.validators.UserValidator;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class UserProfileController {
	
	private Result result;
	private UserDAO users;
	private LoggedUser currentUser;
	private final QuestionDAO questions;
	private UserValidator userValidator;
	
	public UserProfileController(Result result, UserDAO users,
			LoggedUser currentUser, QuestionDAO questions, UserValidator userValidator) {

		super();
		this.result = result;
		this.users = users;
		this.currentUser = currentUser;
		this.userValidator = userValidator;
		this.questions = questions;
	}
	
	@Get("/users/{id}/{sluggedName}")
	public void showProfile(Long id, String sluggedName){
		User selectedUser = users.findById(id);
		String correctSluggedName = selectedUser.getSluggedName();
		if (!correctSluggedName.equals(sluggedName)){
			result.redirectTo(this).showProfile(id, correctSluggedName);
			return;
		}
		
		result.include("isCurrentUser", currentUser.getCurrent().getId().equals(id));
		result.include("questionsByVotes", questions.withAuthorByVotes(selectedUser));
		result.include("selectedUser", selectedUser);
	}
	
	@Get("/users/edit/{id}")
	public void editProfile(Long id) {
		User user = users.findById(id);
		
		if (!user.getId().equals(currentUser.getCurrent().getId())){
			result.redirectTo(ListController.class).home();
			return;
		}
		
		result.include("user", user);
	}
	
	@Post("/users/edit/{id}")
	public void editProfile(Long id, String name, String email, 
			String website, String location, LocalDate birthDate, String description) {
		User user = users.findById(id);
		
		if (!user.getId().equals(currentUser.getCurrent().getId())){
			result.redirectTo(ListController.class).home();
			return;
		}
		
		if (!userValidator.validate(user, name, email, birthDate)){
			userValidator.onErrorRedirectTo(this).editProfile(id);
			return;
		}
		
		user.setPersonalInformation(email, name, website, location, birthDate.toDateTimeAtStartOfDay(), description);
		result.redirectTo(this).showProfile(id, user.getSluggedName());
	}
}
