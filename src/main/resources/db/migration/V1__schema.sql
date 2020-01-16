CREATE TABLE USERS(
  ID VARCHAR(255) PRIMARY KEY,
  USERNAME VARCHAR(255),
  BIO VARCHAR(255),
  EMAIL VARCHAR(255),
  IMAGE VARCHAR(255),
  PASSWORD VARCHAR(255),
  TOKEN VARCHAR(500)
);

CREATE TABLE USERS_FOLLOWED(
  USER_ID VARCHAR(255) NOT NULL,
  FOLLOWED_ID VARCHAR(255) NOT NULL,
  PRIMARY KEY (USER_ID, FOLLOWED_ID),
  CONSTRAINT FK_USER_ID FOREIGN KEY (USER_ID) REFERENCES USERS(ID),
  CONSTRAINT FK_FOLLOWED_ID FOREIGN KEY (FOLLOWED_ID) REFERENCES USERS(ID)
);

CREATE TABLE ARTICLES(
  ID VARCHAR(255) PRIMARY KEY,
  TITLE VARCHAR(255) NOT NULL,
  DESCRIPTION VARCHAR(255),
  BODY VARCHAR(255),
  SLUG VARCHAR(255) NOT NULL,
  AUTHOR_ID VARCHAR(255) NOT NULL,
  CREATED_AT TIMESTAMP,
  UPDATED_AT TIMESTAMP,
  CONSTRAINT FK_AUTHOR_ID FOREIGN KEY (AUTHOR_ID) REFERENCES USERS(ID)
);

CREATE TABLE TAGS(
  ID VARCHAR(255) PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL
);

CREATE TABLE ARTICLES_TAGS(
  ARTICLE_ID VARCHAR(255) NOT NULL,
  TAG_ID VARCHAR(255) NOT NULL,
  PRIMARY KEY (ARTICLE_ID, TAG_ID),
  CONSTRAINT FK_ARTICLES_TAGS_ARTICLE_ID FOREIGN KEY (ARTICLE_ID) REFERENCES ARTICLES(ID),
  CONSTRAINT FK_ARTICLES_TAGS_TAGS_ID FOREIGN KEY (TAG_ID) REFERENCES TAGS(ID)
);

CREATE TABLE ARTICLES_USERS(
  ARTICLE_ID VARCHAR(255) NOT NULL,
  USER_ID VARCHAR(255) NOT NULL,
  PRIMARY KEY (ARTICLE_ID, USER_ID),
  CONSTRAINT FK_ARTICLES_USERS_ARTICLE_ID FOREIGN KEY (ARTICLE_ID) REFERENCES ARTICLES(ID),
  CONSTRAINT FK_ARTICLES_USERS_USER_ID FOREIGN KEY (USER_ID) REFERENCES USERS(ID)
);
