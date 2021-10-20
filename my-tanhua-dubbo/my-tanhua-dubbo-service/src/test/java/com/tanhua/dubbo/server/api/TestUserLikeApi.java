package com.tanhua.dubbo.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUserLikeApi {

    @Autowired
    private UserLikeApi userLikeApi;

    @Test
    public void testUserLike() {
        System.out.println(this.userLikeApi.likeUser(1L, 2L));
        System.out.println(this.userLikeApi.likeUser(1L, 3L));
        System.out.println(this.userLikeApi.likeUser(1L, 4L));

        System.out.println(this.userLikeApi.notLikeUser(1L, 5L));
        System.out.println(this.userLikeApi.notLikeUser(1L, 6L));

        System.out.println(this.userLikeApi.likeUser(1L, 5L));
        System.out.println(this.userLikeApi.notLikeUser(1L, 2L));
    }

    @Test
    public void testQueryList(){
        this.userLikeApi.queryLikeList(1L).forEach(a -> System.out.println(a));
        System.out.println("-------");
        this.userLikeApi.queryNotLikeList(1L).forEach(a -> System.out.println(a));
    }

    @Test
    public void testQueryCounts(){
        System.out.println(this.userLikeApi.queryMutualLikeCount(1L));
        System.out.println(this.userLikeApi.queryFanCount(1L));
        System.out.println(this.userLikeApi.queryLikeCount(1L));
    }
}