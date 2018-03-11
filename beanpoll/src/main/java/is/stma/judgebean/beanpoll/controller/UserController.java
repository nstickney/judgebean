package is.stma.judgebean.beanpoll.controller;

import is.stma.judgebean.beanpoll.model.User;
import is.stma.judgebean.beanpoll.rules.UserRules;
import is.stma.judgebean.beanpoll.service.UserService;

import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@Model
public class UserController extends AbstractEntityController<User, UserRules,
        UserService> {

    @Inject
    private UserService service;

    @Inject
    private SessionBean bean;

    private User newUser;

    @Override
    @Produces
    @Named("newUser")
    User getNew() {
        if (newUser == null) {
            newUser = new User();
        }
        return newUser;
    }

    @Override
    void setNew(User entity) {
        newUser = entity;
    }

    @Override
    UserService getService() {
        return service;
    }

    @Override
    public void update(User entity) {
        doUpdate(entity);
    }

    @Override
    public void delete(User entity) {
        doDelete(entity);
    }

    public void changePassword(User entity, String currentPassword, String newPassword) {
        if (null != entity && null != currentPassword && null != newPassword && entity.checkPassword(currentPassword)) {
            entity.setSecret(newPassword);
        }
    }
}