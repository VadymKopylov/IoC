package com.kopylov.ioc.context;

import com.kopylov.ioc.entity.*;
import com.kopylov.ioc.exception.BeanInstantiationException;
import com.kopylov.ioc.exception.NoSuchBeanException;
import com.kopylov.ioc.exception.NoUniqueBeanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathApplicationContextTest {

    private final ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext();

    @Test
    void testGetBeanByName() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserService userService = new UserService();
        PaymentService paymentService = new PaymentService();
        MailService mailService = new MailService();

        beanMap.put("userServiceBean", new Bean("bean1", userService));
        beanMap.put("paymentServiceBean", new Bean("bean2", paymentService));
        beanMap.put("mailServiceBean", new Bean("bean3", mailService));

        classPathApplicationContext.setBeans(beanMap);

        UserService actualUserServiceBean = (UserService) classPathApplicationContext.getBean("userServiceBean");
        PaymentService actualPaymentServiceBean = (PaymentService) classPathApplicationContext.getBean("paymentServiceBean");
        MailService actualMailServiceBean = (MailService) classPathApplicationContext.getBean("mailServiceBean");

        assertNotNull(actualUserServiceBean);
        assertNotNull(actualPaymentServiceBean);
        assertNotNull(actualMailServiceBean);
        assertEquals(userService, actualUserServiceBean);
        assertEquals(paymentService, actualPaymentServiceBean);
        assertEquals(mailService, actualMailServiceBean);
    }

    @Test
    void testGetBeanByClass() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserService userService = new UserService();
        PaymentService paymentService = new PaymentService();
        MailService mailService = new MailService();

        beanMap.put("userServiceBean", new Bean("bean1", userService));
        beanMap.put("paymentServiceBean", new Bean("bean2", paymentService));
        beanMap.put("mailServiceBean", new Bean("bean3", mailService));

        classPathApplicationContext.setBeans(beanMap);

        UserService actualUserServiceBean = classPathApplicationContext.getBean(UserService.class);
        PaymentService actualPaymentServiceBean = classPathApplicationContext.getBean(PaymentService.class);
        MailService actualMailServiceBean = classPathApplicationContext.getBean(MailService.class);

        assertNotNull(actualUserServiceBean);
        assertNotNull(actualPaymentServiceBean);
        assertNotNull(actualMailServiceBean);
        assertEquals(userService, actualUserServiceBean);
        assertEquals(paymentService, actualPaymentServiceBean);
        assertEquals(mailService, actualMailServiceBean);
    }

    @Test
    void testGetBeanByIdAndClass() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserService userService = new UserService();
        PaymentService paymentService = new PaymentService();
        MailService mailService = new MailService();

        beanMap.put("userServiceBean", new Bean("bean1", userService));
        beanMap.put("paymentServiceBean", new Bean("bean2", paymentService));
        beanMap.put("mailServiceBean", new Bean("bean3", mailService));

        classPathApplicationContext.setBeans(beanMap);

        UserService actualUserServiceBean = classPathApplicationContext.getBean("userServiceBean",UserService.class);
        PaymentService actualPaymentServiceBean = classPathApplicationContext.getBean("paymentServiceBean",PaymentService.class);
        MailService actualMailServiceBean = classPathApplicationContext.getBean("mailServiceBean",MailService.class);

        assertNotNull(actualUserServiceBean);
        assertNotNull(actualPaymentServiceBean);
        assertNotNull(actualMailServiceBean);
        assertEquals(userService, actualUserServiceBean);
        assertEquals(paymentService, actualPaymentServiceBean);
        assertEquals(mailService, actualMailServiceBean);
    }

    @Test
    void testGetBeanNames() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        beanMap.put("paymentServiceBean", new Bean("bean2", new PaymentService()));
        beanMap.put("mailServiceBean", new Bean("bean3", new MailService()));

        classPathApplicationContext.setBeans(beanMap);
        List<String> actualBeansNames = classPathApplicationContext.getBeanNames();
        List<String> expectedBeansNames = Arrays.asList("userServiceBean", "paymentServiceBean", "mailServiceBean");
        assertTrue(actualBeansNames.containsAll(expectedBeansNames));
        assertTrue(expectedBeansNames.containsAll(actualBeansNames));
    }

    @Test
    void testGetBeanNamesThrowsNoUniqueBeanException(){
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        beanMap.put("userServiceRepeatedBean", new Bean("bean2", new UserService()));
        classPathApplicationContext.setBeans(beanMap);
        Assertions.assertThrows(NoUniqueBeanException.class, () -> {
            classPathApplicationContext.getBean(UserService.class);
        });
    }

    @Test
    void testGetBeanNamesThrowsBeanInstantiationExceptionWhenIdIsEmpty(){
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        classPathApplicationContext.setBeans(beanMap);
        Assertions.assertThrows(BeanInstantiationException.class, () -> {
            classPathApplicationContext.getBean("");
        });
    }

    @Test
    void testGetBeanNamesThrowsNoSuchBeanExceptionWhenClassIsNotExist(){
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        classPathApplicationContext.setBeans(beanMap);
        Assertions.assertThrows(NoSuchBeanException.class, () -> {
            classPathApplicationContext.getBean(MailService.class);
        });
    }

    @Test
    void testGetBeanNamesThrowsNoSuchBeanExceptionWhenBeanIsNotExist(){
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        classPathApplicationContext.setBeans(beanMap);
        Assertions.assertThrows(NoSuchBeanException.class, () -> {
            classPathApplicationContext.getBean("mailService");
        });
    }

    @Test
    void testGetBeanThrowBeanInstantiationExceptionWhenInputClassIsNull(){
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("userServiceBean", new Bean("bean1", new UserService()));
        classPathApplicationContext.setBeans(beanMap);
        Assertions.assertThrows(BeanInstantiationException.class, () -> {
            classPathApplicationContext.getBean((Class<Object>) null);
        });
    }
}