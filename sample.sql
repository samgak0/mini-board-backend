-- #### A. **Users Table (users)**

CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Table for storing user information';
COMMENT ON COLUMN users.id IS 'Unique identifier for each user';
COMMENT ON COLUMN users.username IS 'User login name for authentication';
COMMENT ON COLUMN users.email IS 'Email address for communication and recovery';
COMMENT ON COLUMN users.password IS 'Encrypted password for user authentication';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user information was last updated';

-- #### B. **Posts Table (posts)**

CREATE TABLE posts (
    id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    title VARCHAR2(255) NOT NULL,
    content CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE posts IS 'Table for storing post information';
COMMENT ON COLUMN posts.id IS 'Unique identifier for each post';
COMMENT ON COLUMN posts.user_id IS 'ID of the user who created the post';
COMMENT ON COLUMN posts.title IS 'Title of the post for display';
COMMENT ON COLUMN posts.content IS 'Content of the post in text format';
COMMENT ON COLUMN posts.created_at IS 'Timestamp when the post was created';
COMMENT ON COLUMN posts.updated_at IS 'Timestamp when the post information was last updated';

-- #### C. **Comments Table (comments)**

CREATE TABLE comments (
    id NUMBER PRIMARY KEY,
    post_id NUMBER REFERENCES posts(id),
    user_id NUMBER REFERENCES users(id),
    content VARCHAR2(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id NUMBER REFERENCES comments(id)
);

COMMENT ON TABLE comments IS 'Table for storing comment information';
COMMENT ON COLUMN comments.id IS 'Unique identifier for each comment';
COMMENT ON COLUMN comments.post_id IS 'ID of the post to which the comment belongs';
COMMENT ON COLUMN comments.user_id IS 'ID of the user who wrote the comment';
COMMENT ON COLUMN comments.content IS 'Text content of the comment';
COMMENT ON COLUMN comments.created_at IS 'Timestamp when the comment was created';
COMMENT ON COLUMN comments.parent_comment_id IS 'ID of the parent comment for nested replies';

-- #### D. **Post Likes Table (likes)**

CREATE TABLE post_likes (
    id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    post_id NUMBER REFERENCES posts(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE likes IS 'Table for storing post like information';
COMMENT ON COLUMN likes.id IS 'Unique identifier for each like action';
COMMENT ON COLUMN likes.user_id IS 'ID of the user who liked the content';
COMMENT ON COLUMN likes.post_id IS 'ID of the post that received a like';
COMMENT ON COLUMN likes.created_at IS 'Timestamp when the like action occurred';

-- #### E. **Comment Likes Table (likes)**

CREATE TABLE comment_likes (
    id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    comment_id NUMBER REFERENCES comments(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE likes IS 'Table for storing comment like information';
COMMENT ON COLUMN likes.id IS 'Unique identifier for each like action';
COMMENT ON COLUMN likes.user_id IS 'ID of the user who liked the content';
COMMENT ON COLUMN likes.comment_id IS 'ID of the comment that received a like';
COMMENT ON COLUMN likes.created_at IS 'Timestamp when the like action occurred';

-- ### F. **Post File Table (post_files)**


CREATE TABLE post_files (
    id NUMBER PRIMARY KEY,
    post_id NUMBER REFERENCES posts(id),
    file_name VARCHAR2(255) NOT NULL,
    original_name VARCHAR2(255) NOT NULL,
    file_path VARCHAR2(255) NOT NULL,
    file_size NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE post_files IS 'Table for storing files associated with posts';
COMMENT ON COLUMN post_files.id IS 'Unique identifier for each post file';
COMMENT ON COLUMN post_files.post_id IS 'ID of the post that the file is associated with';
COMMENT ON COLUMN post_files.file_name IS 'Name of the file as stored in the system';
COMMENT ON COLUMN post_files.original_name IS 'Original name of the file as uploaded by the user';
COMMENT ON COLUMN post_files.file_path IS 'File path indicating where the file is stored';
COMMENT ON COLUMN post_files.file_size IS 'Size of the file in bytes';
COMMENT ON COLUMN post_files.created_at IS 'Timestamp when the file was uploaded';

-- ### G. **Comment File Table (comment_files)**

CREATE TABLE comment_files (
    id NUMBER PRIMARY KEY,
    comment_id NUMBER REFERENCES comments(id),
    file_name VARCHAR2(255) NOT NULL,
    original_name VARCHAR2(255) NOT NULL,
    file_path VARCHAR2(255) NOT NULL,
    file_size NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE comment_files IS 'Table for storing files associated with comments';
COMMENT ON COLUMN comment_files.id IS 'Unique identifier for each comment file';
COMMENT ON COLUMN comment_files.comment_id IS 'ID of the comment that the file is associated with';
COMMENT ON COLUMN comment_files.file_name IS 'Name of the file as stored in the system';
COMMENT ON COLUMN comment_files.original_name IS 'Original name of the file as uploaded by the user';
COMMENT ON COLUMN comment_files.file_path IS 'File path indicating where the file is stored';
COMMENT ON COLUMN comment_files.file_size IS 'Size of the file in bytes';
COMMENT ON COLUMN comment_files.created_at IS 'Timestamp when the file was uploaded';

-- # USERS_202409191625

INSERT INTO SAMGAK.USERS (ID,USERNAME,EMAIL,PASSWORD,CREATED_AT,UPDATED_AT) VALUES
	 (1,'john_doe','john@example.com','$2a$10$E1r9T6N2B9W9g3wtuoWc9Oi1J5U1H.S5Yp2Tqnn0Sttx7fdC5kH9G',TIMESTAMP'2024-09-19 13:42:50.828716',TIMESTAMP'2024-09-19 13:42:50.828716'),
	 (2,'jane_smith','jane@example.com','$2a$10$uUO5/7.H5CvSyRZ3bn9SuOwF1UZKvCZdYkOcbJbD.PAk1TV2cKyyG',TIMESTAMP'2024-09-19 13:43:41.465442',TIMESTAMP'2024-09-19 13:43:41.465442'),
	 (3,'alice_wonderland','alice@example.com','$2a$10$1JvEKqK9BkjbXsP69BO1QOQmCtI/5EDrYX5fd7B0vEmW0K14ZoQzK',TIMESTAMP'2024-09-19 13:43:41.470288',TIMESTAMP'2024-09-19 13:43:41.470288'),
	 (4,'bob_builder','bob@example.com','$2a$10$66MB5S8Q65rLIR8YV8n5S.jWg78hjaK6oWc6lfl8W3rwUmLqkW/6a',TIMESTAMP'2024-09-19 13:43:41.473489',TIMESTAMP'2024-09-19 13:43:41.473489'),
	 (5,'charlie_brown','charlie@example.com','$2a$10$yAFZtA9N6TyDhiIj9f69pOOCSBOhVDPQcYTPtnB9dHzU0P.Wbf3Sy',TIMESTAMP'2024-09-19 13:43:41.476436',TIMESTAMP'2024-09-19 13:43:41.476436'),
	 (6,'david_doe','david@example.com','$2a$10$Bw7nKfT4.fGSh5DeW2ee0O4Lx3s.p8S.bZl2Zt6O6F.GGNYVPOIhK',TIMESTAMP'2024-09-19 13:43:41.479925',TIMESTAMP'2024-09-19 13:43:41.479925'),
	 (7,'eve_online','eve@example.com','$2a$10$HFlb8EYo1QugxX0lS6g9y.JnUbQdY9xCU3e5BBzB1a2K9kWhcc3fe',TIMESTAMP'2024-09-19 13:43:41.485204',TIMESTAMP'2024-09-19 13:43:41.485204'),
	 (8,'frank_castle','frank@example.com','$2a$10$1Q0bQ5KeQsPaWUvkgkjzvOsbPiGAUZON7e5qOoJ3mfz5CfPOkA75y',TIMESTAMP'2024-09-19 13:43:41.489654',TIMESTAMP'2024-09-19 13:43:41.489654'),
	 (9,'george_costanza','george@example.com','$2a$10$ReZxuYraM6/yoK56.3SPxuY8ZoE2Ov/hPGrABMNi.pIP2j57LfTJu',TIMESTAMP'2024-09-19 13:43:41.493084',TIMESTAMP'2024-09-19 13:43:41.493084'),
	 (10,'hannah_montana','hannah@example.com','$2a$10$3x.oHQI6V0auE8C8Fz6CwO0V0hR2aiDB4ScMcbwMtu1gFBDTejZJW',TIMESTAMP'2024-09-19 13:43:41.4972',TIMESTAMP'2024-09-19 13:43:41.4972');


-- # POSTS_202409191625

INSERT INTO SAMGAK.POSTS (ID,USER_ID,TITLE,CONTENT,CREATED_AT,UPDATED_AT) VALUES
	 (20,10,'스무 번째 게시글',TO_CLOB('이것은 스무 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:10.602866',TIMESTAMP'2024-09-19 14:26:10.602866'),
	 (1,1,'첫 번째 게시글',TO_CLOB('이것은 첫 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.786967',TIMESTAMP'2024-09-19 14:26:14.786967'),
	 (2,1,'두 번째 게시글',TO_CLOB('이것은 두 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.793744',TIMESTAMP'2024-09-19 14:26:14.793744'),
	 (3,2,'세 번째 게시글',TO_CLOB('이것은 세 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.797389',TIMESTAMP'2024-09-19 14:26:14.797389'),
	 (4,2,'네 번째 게시글',TO_CLOB('이것은 네 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.800069',TIMESTAMP'2024-09-19 14:26:14.800069'),
	 (5,3,'다섯 번째 게시글',TO_CLOB('이것은 다섯 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.803495',TIMESTAMP'2024-09-19 14:26:14.803495'),
	 (6,3,'여섯 번째 게시글',TO_CLOB('이것은 여섯 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.807487',TIMESTAMP'2024-09-19 14:26:14.807487'),
	 (7,4,'일곱 번째 게시글',TO_CLOB('이것은 일곱 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.811077',TIMESTAMP'2024-09-19 14:26:14.811077'),
	 (8,4,'여덟 번째 게시글',TO_CLOB('이것은 여덟 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.814726',TIMESTAMP'2024-09-19 14:26:14.814726'),
	 (9,5,'아홉 번째 게시글',TO_CLOB('이것은 아홉 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.818413',TIMESTAMP'2024-09-19 14:26:14.818413');
INSERT INTO SAMGAK.POSTS (ID,USER_ID,TITLE,CONTENT,CREATED_AT,UPDATED_AT) VALUES
	 (10,5,'열 번째 게시글',TO_CLOB('이것은 열 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.8225',TIMESTAMP'2024-09-19 14:26:14.8225'),
	 (11,6,'열한 번째 게시글',TO_CLOB('이것은 열한 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.826763',TIMESTAMP'2024-09-19 14:26:14.826763'),
	 (12,6,'열두 번째 게시글',TO_CLOB('이것은 열두 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.831043',TIMESTAMP'2024-09-19 14:26:14.831043'),
	 (13,7,'열세 번째 게시글',TO_CLOB('이것은 열세 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.83554',TIMESTAMP'2024-09-19 14:26:14.83554'),
	 (14,7,'열네 번째 게시글',TO_CLOB('이것은 열네 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.839191',TIMESTAMP'2024-09-19 14:26:14.839191'),
	 (15,8,'열다섯 번째 게시글',TO_CLOB('이것은 열다섯 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.842652',TIMESTAMP'2024-09-19 14:26:14.842652'),
	 (16,8,'열여섯 번째 게시글',TO_CLOB('이것은 열여섯 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.846282',TIMESTAMP'2024-09-19 14:26:14.846282'),
	 (17,9,'열일곱 번째 게시글',TO_CLOB('이것은 열일곱 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.85007',TIMESTAMP'2024-09-19 14:26:14.85007'),
	 (18,9,'열여덟 번째 게시글',TO_CLOB('이것은 열여덟 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.853874',TIMESTAMP'2024-09-19 14:26:14.853874'),
	 (19,10,'열아홉 번째 게시글',TO_CLOB('이것은 열아홉 번째 게시글의 내용입니다.'),TIMESTAMP'2024-09-19 14:26:14.857206',TIMESTAMP'2024-09-19 14:26:14.857206');


-- # POST_LIKES_202409191625

INSERT INTO SAMGAK.POST_LIKES (ID,USER_ID,POST_ID,CREATED_AT) VALUES
	 (1,1,1,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (2,2,1,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (3,3,1,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (4,1,2,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (5,2,2,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (6,3,2,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (7,1,3,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (8,2,3,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (9,3,3,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (10,1,4,TIMESTAMP'2024-09-19 15:37:36.63531');
INSERT INTO SAMGAK.POST_LIKES (ID,USER_ID,POST_ID,CREATED_AT) VALUES
	 (11,2,4,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (12,3,4,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (13,1,5,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (14,2,5,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (15,3,5,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (16,1,6,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (17,2,6,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (18,3,6,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (19,1,7,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (20,2,7,TIMESTAMP'2024-09-19 15:37:36.63531');
INSERT INTO SAMGAK.POST_LIKES (ID,USER_ID,POST_ID,CREATED_AT) VALUES
	 (21,3,7,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (22,1,8,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (23,2,8,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (24,3,8,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (25,1,9,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (26,2,9,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (27,3,9,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (28,1,10,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (29,2,10,TIMESTAMP'2024-09-19 15:37:36.63531'),
	 (30,3,10,TIMESTAMP'2024-09-19 15:37:36.63531');

-- # POST_FILES_202409191625

INSERT INTO SAMGAK.POST_FILES (ID,POST_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (1,1,'file1.jpg','첫 번째 파일.jpg','/uploads/file1.jpg',1024,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (2,1,'file2.png','두 번째 파일.png','/uploads/file2.png',2048,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (3,2,'file3.pdf','세 번째 파일.pdf','/uploads/file3.pdf',3072,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (4,2,'file4.docx','네 번째 파일.docx','/uploads/file4.docx',4096,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (5,3,'file5.mp4','다섯 번째 파일.mp4','/uploads/file5.mp4',5120,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (6,3,'file6.gif','여섯 번째 파일.gif','/uploads/file6.gif',6000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (7,4,'file7.zip','일곱 번째 파일.zip','/uploads/file7.zip',7000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (8,4,'file8.txt','여덟 번째 파일.txt','/uploads/file8.txt',800,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (9,5,'file9.csv','아홉 번째 파일.csv','/uploads/file9.csv',1500,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (10,5,'file10.pptx','열 번째 파일.pptx','/uploads/file10.pptx',2500,TIMESTAMP'2024-09-19 15:39:13.251646');
INSERT INTO SAMGAK.POST_FILES (ID,POST_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (11,6,'file11.mp3','열한 번째 파일.mp3','/uploads/file11.mp3',3500,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (12,6,'file12.wmv','열두 번째 파일.wmv','/uploads/file12.wmv',4500,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (13,7,'file13.webp','열세 번째 파일.webp','/uploads/file13.webp',2000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (14,7,'file14.bmp','열네 번째 파일.bmp','/uploads/file14.bmp',3000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (15,8,'file15.tiff','열다섯 번째 파일.tiff','/uploads/file15.tiff',4000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (16,8,'file16.psd','열여섯 번째 파일.psd','/uploads/file16.psd',5000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (17,9,'file17.ai','열일곱 번째 파일.ai','/uploads/file17.ai',6000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (18,9,'file18.indd','열여덟 번째 파일.indd','/uploads/file18.indd',7000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (19,10,'file19.eps','열아홉 번째 파일.eps','/uploads/file19.eps',8000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (20,10,'file20.svg','스무 번째 파일.svg','/uploads/file20.svg',9000,TIMESTAMP'2024-09-19 15:39:13.251646');
INSERT INTO SAMGAK.POST_FILES (ID,POST_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (21,1,'file21.txt','스물한 번째 파일.txt','/uploads/file21.txt',1000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (22,2,'file22.doc','스물두 번째 파일.doc','/uploads/file22.doc',2000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (23,3,'file23.ppt','스물세 번째 파일.ppt','/uploads/file23.ppt',3000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (24,4,'file24.pdf','스물네 번째 파일.pdf','/uploads/file24.pdf',4000,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (25,5,'file25.html','스물다섯 번째 파일.html','/uploads/file25.html',500,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (26,6,'file26.js','스물여섯 번째 파일.js','/uploads/file26.js',1500,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (27,7,'file27.css','스물일곱 번째 파일.css','/uploads/file27.css',1200,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (28,8,'file28.sql','스물여덟 번째 파일.sql','/uploads/file28.sql',1800,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (29,9,'file29.py','스물아홉 번째 파일.py','/uploads/file29.py',2100,TIMESTAMP'2024-09-19 15:39:13.251646'),
	 (30,10,'file30.rb','서른 번째 파일.rb','/uploads/file30.rb',1900,TIMESTAMP'2024-09-19 15:39:13.251646');


-- # COMMENTS_202409191625

INSERT INTO SAMGAK.COMMENTS (ID,POST_ID,USER_ID,CONTENT,CREATED_AT,PARENT_COMMENT_ID) VALUES
	 (1,1,1,'첫 번째 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (2,1,2,'첫 번째 댓글에 대한 답글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',1),
	 (3,1,3,'두 번째 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (4,2,1,'첫 번째 포스트에 대한 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (5,2,2,'좋은 포스트입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (6,2,3,'유익한 내용입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (7,3,1,'세 번째 포스트에 대한 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (8,3,2,'멋진 내용이네요!',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (9,3,3,'아주 잘 봤습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (10,4,1,'이 포스트는 정말 흥미롭습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL);
INSERT INTO SAMGAK.COMMENTS (ID,POST_ID,USER_ID,CONTENT,CREATED_AT,PARENT_COMMENT_ID) VALUES
	 (11,4,2,'좋은 정보를 얻었습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (12,4,3,'유용한 포스트네요.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (13,5,1,'다섯 번째 포스트에 대한 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (14,5,2,'재미있게 읽었습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (15,5,3,'감사합니다!',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (16,6,1,'이 주제에 대해 더 알고 싶어요.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (17,6,2,'좋은 의견입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (18,6,3,'의미 있는 내용입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (19,7,1,'세 번째 포스트에 대한 댓글입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (20,7,2,'정말 유익한 내용이네요.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL);
INSERT INTO SAMGAK.COMMENTS (ID,POST_ID,USER_ID,CONTENT,CREATED_AT,PARENT_COMMENT_ID) VALUES
	 (21,7,3,'댓글도 읽어볼 가치가 있습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (22,8,1,'아주 흥미로운 주제입니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (23,8,2,'추천합니다!',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (24,8,3,'댓글을 보고 더 궁금해졌습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (25,9,1,'좋은 포스트에요.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (26,9,2,'많은 도움이 되었습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (27,9,3,'이 내용은 중요합니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (28,10,1,'정말 잘 읽었습니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (29,10,2,'추가적인 정보가 필요해요.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL),
	 (30,10,3,'이 포스트에 대한 깊이 있는 논의가 필요합니다.',TIMESTAMP'2024-09-19 15:36:42.512476',NULL);


-- # COMMENT_LIKES_202409191625

INSERT INTO SAMGAK.COMMENT_LIKES (ID,USER_ID,COMMENT_ID,CREATED_AT) VALUES
	 (1,1,1,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (2,2,1,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (3,3,1,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (4,1,2,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (5,2,2,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (6,3,2,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (7,1,3,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (8,2,3,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (9,3,3,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (10,1,4,TIMESTAMP'2024-09-19 15:38:24.214173');
INSERT INTO SAMGAK.COMMENT_LIKES (ID,USER_ID,COMMENT_ID,CREATED_AT) VALUES
	 (11,2,4,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (12,3,4,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (13,1,5,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (14,2,5,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (15,3,5,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (16,1,6,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (17,2,6,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (18,3,6,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (19,1,7,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (20,2,7,TIMESTAMP'2024-09-19 15:38:24.214173');
INSERT INTO SAMGAK.COMMENT_LIKES (ID,USER_ID,COMMENT_ID,CREATED_AT) VALUES
	 (21,3,7,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (22,1,8,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (23,2,8,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (24,3,8,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (25,1,9,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (26,2,9,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (27,3,9,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (28,1,10,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (29,2,10,TIMESTAMP'2024-09-19 15:38:24.214173'),
	 (30,3,10,TIMESTAMP'2024-09-19 15:38:24.214173');


-- # COMMENT_FILES_202409191625

INSERT INTO SAMGAK.COMMENT_FILES (ID,COMMENT_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (1,1,'comment_file1.jpg','첫 번째 댓글 파일.jpg','/uploads/comment_file1.jpg',1024,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (2,1,'comment_file2.png','두 번째 댓글 파일.png','/uploads/comment_file2.png',2048,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (3,2,'comment_file3.pdf','세 번째 댓글 파일.pdf','/uploads/comment_file3.pdf',3072,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (4,2,'comment_file4.docx','네 번째 댓글 파일.docx','/uploads/comment_file4.docx',4096,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (5,3,'comment_file5.mp4','다섯 번째 댓글 파일.mp4','/uploads/comment_file5.mp4',5120,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (6,3,'comment_file6.gif','여섯 번째 댓글 파일.gif','/uploads/comment_file6.gif',6000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (7,4,'comment_file7.zip','일곱 번째 댓글 파일.zip','/uploads/comment_file7.zip',7000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (8,4,'comment_file8.txt','여덟 번째 댓글 파일.txt','/uploads/comment_file8.txt',800,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (9,5,'comment_file9.csv','아홉 번째 댓글 파일.csv','/uploads/comment_file9.csv',1500,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (10,5,'comment_file10.pptx','열 번째 댓글 파일.pptx','/uploads/comment_file10.pptx',2500,TIMESTAMP'2024-09-19 15:40:15.809548');
INSERT INTO SAMGAK.COMMENT_FILES (ID,COMMENT_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (11,6,'comment_file11.mp3','열한 번째 댓글 파일.mp3','/uploads/comment_file11.mp3',3500,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (12,6,'comment_file12.wmv','열두 번째 댓글 파일.wmv','/uploads/comment_file12.wmv',4500,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (13,7,'comment_file13.webp','열세 번째 댓글 파일.webp','/uploads/comment_file13.webp',2000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (14,7,'comment_file14.bmp','열네 번째 댓글 파일.bmp','/uploads/comment_file14.bmp',3000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (15,8,'comment_file15.tiff','열다섯 번째 댓글 파일.tiff','/uploads/comment_file15.tiff',4000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (16,8,'comment_file16.psd','열여섯 번째 댓글 파일.psd','/uploads/comment_file16.psd',5000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (17,9,'comment_file17.ai','열일곱 번째 댓글 파일.ai','/uploads/comment_file17.ai',6000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (18,9,'comment_file18.indd','열여덟 번째 댓글 파일.indd','/uploads/comment_file18.indd',7000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (19,10,'comment_file19.eps','열아홉 번째 댓글 파일.eps','/uploads/comment_file19.eps',8000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (20,10,'comment_file20.svg','스무 번째 댓글 파일.svg','/uploads/comment_file20.svg',9000,TIMESTAMP'2024-09-19 15:40:15.809548');
INSERT INTO SAMGAK.COMMENT_FILES (ID,COMMENT_ID,FILE_NAME,ORIGINAL_NAME,FILE_PATH,FILE_SIZE,CREATED_AT) VALUES
	 (21,1,'comment_file21.txt','스물한 번째 댓글 파일.txt','/uploads/comment_file21.txt',1000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (22,2,'comment_file22.doc','스물두 번째 댓글 파일.doc','/uploads/comment_file22.doc',2000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (23,3,'comment_file23.ppt','스물세 번째 댓글 파일.ppt','/uploads/comment_file23.ppt',3000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (24,4,'comment_file24.pdf','스물네 번째 댓글 파일.pdf','/uploads/comment_file24.pdf',4000,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (25,5,'comment_file25.html','스물다섯 번째 댓글 파일.html','/uploads/comment_file25.html',500,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (26,6,'comment_file26.js','스물여섯 번째 댓글 파일.js','/uploads/comment_file26.js',1500,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (27,7,'comment_file27.css','스물일곱 번째 댓글 파일.css','/uploads/comment_file27.css',1200,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (28,8,'comment_file28.sql','스물여덟 번째 댓글 파일.sql','/uploads/comment_file28.sql',1800,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (29,9,'comment_file29.py','스물아홉 번째 댓글 파일.py','/uploads/comment_file29.py',2100,TIMESTAMP'2024-09-19 15:40:15.809548'),
	 (30,10,'comment_file30.rb','서른 번째 댓글 파일.rb','/uploads/comment_file30.rb',1900,TIMESTAMP'2024-09-19 15:40:15.809548');
