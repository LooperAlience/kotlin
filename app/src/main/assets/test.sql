#dbCreate /* 카테고리 테이블 생성.*/
CREATE TABLE IF NOT EXISTS test(
    test_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    userid VARCHAR(255) NOT null,
    regdate DATETIME not null default current_timestamp,
    CONSTRAINT unique_test UNIQUE (test_rowid)
)
#dbDrop
drop table test
#insert
insert into test(userid)values('hika')
#select
select userid from test where userid=@userid@