package com.example.realworld.infrastructure.web.route;

import com.example.realworld.domain.user.service.UserService;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class UsersRoute extends AbstractHttpRoute {

  private UserService userService;

  public UsersRoute(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Router configure(Vertx vertx) {
    final String usersApiPath = "/users";
    final Router usersRouter = Router.router(vertx);
    usersRouter.route().handler(BodyHandler.create());
    usersRouter.post(usersApiPath).handler(this::create);
    //    usersRouter.post(usersApiPath + "/login").handler(this::login);
    return usersRouter;
  }

  //  private void login(RoutingContext routingContext) {
  //    LoginRequest loginRequest = getBodyAndValid(routingContext, LoginRequest.class);
  //    usersService.login(
  //        loginRequest.getEmail(),
  //        loginRequest.getPassword(),
  //        responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  //  }

  private void create(RoutingContext routingContext) {
    NewUserRequest newUserRequest = getBodyAndValid(routingContext, NewUserRequest.class);
    userService
        .create(newUserRequest.toNewUser())
        .subscribe(responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  }
}
