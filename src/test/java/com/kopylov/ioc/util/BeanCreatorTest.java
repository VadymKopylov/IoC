package com.kopylov.ioc.util;

import com.kopylov.ioc.entity.*;
import com.kopylov.ioc.exception.BeanInstantiationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanCreatorTest {

    private final BeanDefinition userServiceBeanDefinition =
            new BeanDefinition("userService", "com.kopylov.ioc.entity.UserService");
    private final BeanDefinition paymentServiceBeanDefinition =
            new BeanDefinition("paymentService", "com.kopylov.ioc.entity.PaymentService");
    private final BeanDefinition mailServiceBeanDefinition =
            new BeanDefinition("mailService", "com.kopylov.ioc.entity.MailService");
    private final BeanCreator beanCreator =
            new BeanCreator(List.of(userServiceBeanDefinition, paymentServiceBeanDefinition, mailServiceBeanDefinition));

    @Test
    void testCreateBeans() {
        Map<String,Bean> beans = beanCreator.createBeans();
        Bean actualUserServiceBean = beans.get("userService");
        Bean actualPaymentServiceBean = beans.get("paymentService");
        Bean actualMailServiceBean = beans.get("mailService");

        assertNotNull(actualUserServiceBean);
        assertNotNull(actualPaymentServiceBean);
        assertNotNull(actualMailServiceBean);

        assertEquals("userService", actualUserServiceBean.getId());
        assertEquals("paymentService",actualPaymentServiceBean.getId());
        assertEquals("mailService",actualMailServiceBean.getId());

        assertEquals(UserService.class, actualUserServiceBean.getValue().getClass());
        assertEquals(PaymentService.class,actualPaymentServiceBean.getValue().getClass());
        assertEquals(MailService.class,actualMailServiceBean.getValue().getClass());
    }

    @Test
    void testFillPropertiesReturnCorrectFieldFromBeanDefinition() {
        userServiceBeanDefinition.getProperty().put("user", "Vadym");
        List<BeanDefinition> beanDefinitions = List.of(userServiceBeanDefinition);
        Map<String, Bean> beans = new HashMap<>();
        beans.put("userService", new Bean("userService", new UserService()));
        Map<String, Bean> stringBeanMap = beanCreator.fillProperties(beans, beanDefinitions);

        assertThat(stringBeanMap, hasKey("userService"));

        UserService userService = (UserService) stringBeanMap.get("userService").getValue();

        assertThat(userService.getUser(), equalTo("Vadym"));
    }

    @Test
    void testRefFillPropertiesReturnCorrectRefFieldFromBeanDefinition() {
        userServiceBeanDefinition.getRefProperty().put("mailService", "mailService");
        List<BeanDefinition> beanDefinitions = List.of(userServiceBeanDefinition, mailServiceBeanDefinition);
        Map<String, Bean> beans = new HashMap<>();
        beans.put("userService", new Bean("userService", new UserService()));
        beans.put("mailService", new Bean("mailService", new MailService()));
        Map<String, Bean> stringBeanMap = beanCreator.fillRefProperties(beans, beanDefinitions);

        assertThat(stringBeanMap, hasKey("userService"));
        assertThat(stringBeanMap, hasKey("mailService"));

        assertThat(stringBeanMap.get("userService").getValue(), instanceOf(UserService.class));
        assertThat(stringBeanMap.get("userService").getValue(),
                hasProperty("mailService", sameInstance(stringBeanMap.get("mailService").getValue())));
    }

    @Test
    void testFillIdAndClassThrowBeanInstantiationExceptionWhenWrongPathSpecified() {
        Assertions.assertThrows(BeanInstantiationException.class, () -> {
            beanCreator.fillIdAndClass(List.of(
                    new BeanDefinition("userService", "wrong.path.to.Service")));
        });
    }
}


