package com.example.realworld.infrastructure.verticles;

import com.example.realworld.constants.TestsConstants;
import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.infrastructure.web.model.response.ProfileResponse;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.example.realworld.constants.TestsConstants.API_PREFIX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class ProfilesAPITest extends AbstractVerticleTest {

  private final String PROFILES_RESOURCE_PATH = API_PREFIX + "/profiles";

  @Test
  public void givenAnPersistedUserShouldReturnProfileDataWithStatusCode200(
      VertxTestContext vertxTestContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setImage("image");
    user.setBio("bio");
    user.setPassword("user1_123");

    createUser(user)
        .subscribe(
            persistedUser ->
                webClient
                    .get(
                        port,
                        TestsConstants.HOST,
                        PROFILES_RESOURCE_PATH + "/" + persistedUser.getUsername())
                    .as(BodyCodec.string())
                    .send(
                        vertxTestContext.succeeding(
                            response ->
                                vertxTestContext.verify(
                                    () -> {
                                      ProfileResponse profileResponse =
                                          readValue(response.body(), ProfileResponse.class);
                                      assertThat(
                                          profileResponse.getUsername(),
                                          is(persistedUser.getUsername()));
                                      assertThat(
                                          profileResponse.getBio(), is(persistedUser.getBio()));
                                      assertThat(
                                          profileResponse.getImage(), is(persistedUser.getImage()));
                                      assertThat(profileResponse.isFollowing(), is(false));
                                    }))));
  }
}
