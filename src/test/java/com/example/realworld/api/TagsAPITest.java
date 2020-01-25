package com.example.realworld.api;

import com.example.realworld.RealworldDataIntegrationTest;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.infrastructure.web.model.response.TagsResponse;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.example.realworld.constants.TestsConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class TagsAPITest extends RealworldDataIntegrationTest {

  private final String TAGS_PATH = API_PREFIX + "/tags";

  @Test
  public void
      givenTenPersistedTags_whenExecuteGetTagsEndpoint_shouldReturnATagListWithStatusCode200(
          VertxTestContext vertxTestContext) {

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    Tag tag3 = new Tag();
    tag3.setName("tag3");

    Tag tag4 = new Tag();
    tag4.setName("tag4");

    Tag tag5 = new Tag();
    tag5.setName("tag5");

    Tag tag6 = new Tag();
    tag6.setName("tag6");

    Tag tag7 = new Tag();
    tag7.setName("tag7");

    Tag tag8 = new Tag();
    tag8.setName("tag8");

    Tag tag9 = new Tag();
    tag9.setName("tag9");

    Tag tag10 = new Tag();
    tag10.setName("tag10");

    saveTags(tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8, tag9, tag10);

    webClient
        .get(port, HOST, TAGS_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX)
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          TagsResponse tagsResponse =
                              readValue(response.body(), TagsResponse.class, false);

                          assertThat(tagsResponse.getTags().size(), is(10));
                          assertThat(
                              tagsResponse.getTags(),
                              containsInAnyOrder(
                                  tag1.getName(),
                                  tag2.getName(),
                                  tag3.getName(),
                                  tag4.getName(),
                                  tag5.getName(),
                                  tag6.getName(),
                                  tag7.getName(),
                                  tag8.getName(),
                                  tag9.getName(),
                                  tag10.getName()));

                          vertxTestContext.completeNow();
                        })));
  }
}
