package com.clio.greenbean.spring.service;

import com.clio.greenbean.domain.User;
import com.clio.greenbean.exception.UsernameDuplicatedException;
import com.clio.greenbean.mybatis.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * created by 吾乃逆世之神也 on 2019/11/1
 */
class UserServiceTest {
    private static UserService userService;
    private static UserMapper mockUserMapper;
    
    @BeforeAll
    static void setUp(){
        mockUserMapper = Mockito.mock(UserMapper.class);
        userService = new UserService(mockUserMapper);
    }
    
    @Test
    void testInsertUser(){
//        使用spy
//        userService = Mockito.spy(userService);
//        Mockito.doReturn(true).when(userService).validateUsernameDuplicated("b");
//       不使用spy
        Mockito.when(mockUserMapper.getUserByUsername("b")).thenReturn(null);
        
        Mockito.doAnswer((InvocationOnMock invocation)->{
            User mockUser = invocation.getArgument(0);
            Mockito.when(mockUser.getId()).thenReturn(666);
            return null;
        }).when(mockUserMapper).insertUserBasicInfo(Mockito.any(User.class));
        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getUsername()).thenReturn("b");
        userService.insertUser(mockUser);
        // 验证调用顺序
        InOrder inOrder = Mockito.inOrder(mockUserMapper);
        inOrder.verify(mockUserMapper).insertUserBasicInfo(mockUser);
        inOrder.verify(mockUserMapper).insertUserAuthority(666,mockUser.getAuthority());
    }
    
    @Test
    void testInsertUserWithDuplicatedUsername() {
        Mockito.when(mockUserMapper.getUserByUsername("a")).thenReturn(Mockito.mock(User.class));
        User mockUser =  Mockito.mock(User.class);
        Mockito.when(mockUser.getUsername()).thenReturn("a");
        Assertions.assertThrows(UsernameDuplicatedException.class, ()-> userService.insertUser(mockUser));
    }
    
    @Test
    void testValidateUsernameDuplicatedExist(){
        Mockito.when(mockUserMapper.getUserByUsername("a")).thenReturn(Mockito.mock(User.class));
        boolean validateUserA = userService.validateUsernameDuplicated("a");
        Assertions.assertFalse(validateUserA);
    }
    
    @Test
    void testValidateUsernameDuplicatedNotExist(){
        Mockito.when(mockUserMapper.getUserByUsername("b")).thenReturn(null);
        boolean validateUserB = userService. validateUsernameDuplicated("b");
        Assertions.assertTrue(validateUserB);
    }
    
}
