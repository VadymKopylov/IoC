package com.kopylov.ioc.util;

import com.kopylov.ioc.entity.*;
import com.kopylov.ioc.exception.NoSuchBeanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanCreatorITest {

    private static List<BeanDefinition> beanDefinitions;
    private static Map<String, Bean> beans;
    private static BeanCreator beanCreator;

    @BeforeAll
    static void setUp() {
        beanDefinitions = List.of(
                new BeanDefinition("userService", "com.kopylov.ioc.entity.UserService"),
                new BeanDefinition("paymentService", "com.kopylov.ioc.entity.PaymentService"),
                new BeanDefinition("mailService", "com.kopylov.ioc.entity.MailService")
        );

        beanDefinitions.get(0).getProperty().put("user", "Vadym");
        beanDefinitions.get(1).getProperty().put("paymentType", "visa");
        beanDefinitions.get(2).getProperty().put("protocol", "POP3");
        beanDefinitions.get(2).getProperty().put("port", "3000");

        beanDefinitions.get(0).getRefProperty().put("mailService", "mailService");
        beanDefinitions.get(1).getRefProperty().put("mailService", "mailService");

        beans = new HashMap<>();
        beans.put("userService", new Bean("userService", new UserService()));
        beans.put("paymentService", new Bean("paymentService", new PaymentService()));
        beans.put("mailService", new Bean("mailService", new MailService()));

        beanCreator = new BeanCreator(beanDefinitions);
    }

    @Test
    void testFillIdAndClass() {
        Map<String, Bean> actualStringBeanMap = beanCreator.fillIdAndClass(beanDefinitions);

        assertThat(actualStringBeanMap, hasKey("userService"));
        assertThat(actualStringBeanMap, hasKey("paymentService"));
        assertThat(actualStringBeanMap, hasKey("mailService"));

        Bean userBean = actualStringBeanMap.get("userService");
        assertThat(userBean, notNullValue());
        assertThat(userBean.getId(), equalTo("userService"));
        assertThat(userBean.getValue(), instanceOf(UserService.class));

        Bean paymentBean = actualStringBeanMap.get("paymentService");
        assertThat(paymentBean, notNullValue());
        assertThat(paymentBean.getId(), equalTo("paymentService"));
        assertThat(paymentBean.getValue(), instanceOf(PaymentService.class));

        Bean mailBean = actualStringBeanMap.get("mailService");
        assertThat(mailBean, notNullValue());
        assertThat(mailBean.getId(), equalTo("mailService"));
        assertThat(mailBean.getValue(), instanceOf(MailService.class));
    }

    @Test
    void testFillProperties() {
        Map<String, Bean> actualBeans = beanCreator.fillProperties(beans, beanDefinitions);

        UserService userService = (UserService) actualBeans.get("userService").getValue();
        PaymentService paymentService = (PaymentService) actualBeans.get("paymentService").getValue();
        MailService mailService = (MailService) actualBeans.get("mailService").getValue();

        assertThat(actualBeans, hasKey("userService"));
        assertThat(actualBeans, hasKey("paymentService"));
        assertThat(actualBeans, hasKey("mailService"));

        assertThat(userService.getUser(), equalTo("Vadym"));
        assertThat(paymentService.getPaymentType(), equalTo("visa"));
        assertThat(mailService.getProtocol(), equalTo("POP3"));
        assertThat(mailService.getPort(), equalTo(3000));
    }

    @Test
    void testFillRefProperties() {
        Map<String, Bean> actualBeans = beanCreator.fillRefProperties(beans, beanDefinitions);

        assertNotNull(actualBeans);

        assertThat(actualBeans, hasKey("userService"));
        assertThat(actualBeans, hasKey("paymentService"));
        assertThat(actualBeans, hasKey("mailService"));

        assertThat(actualBeans.get("userService").getValue(), instanceOf(UserService.class));
        assertThat(actualBeans.get("userService").getValue(),
                hasProperty("mailService", sameInstance(actualBeans.get("mailService").getValue())));

        assertThat(actualBeans.get("paymentService").getValue(), instanceOf(PaymentService.class));
        assertThat(actualBeans.get("paymentService").getValue(),
                hasProperty("mailService", sameInstance(actualBeans.get("mailService").getValue())));

        assertThat(actualBeans.get("mailService").getValue(), instanceOf(MailService.class));
    }

    @Test
    void testFillPropertiesThrowsIllegalArgumentExceptionWhenCorrectBeanIsNotPassed(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            beanCreator.fillProperties(new HashMap<>(),beanDefinitions);
        });
    }
    @Test
    void testFillRefPropertiesThrowsNoSuchBeanExceptionWhenCorrectRefPropertyIsNotPassed(){
        Assertions.assertThrows(NoSuchBeanException.class, () -> {
            beanDefinitions.get(0).getRefProperty().clear();
            beanDefinitions.get(0).getRefProperty().put("notExistService", "notExistService");
            beanCreator.fillRefProperties(beans,beanDefinitions);
        });
    }

    @Test
    void testFillRefPropertiesThrowsNoSuchFieldExceptionWhenFieldInBeanDoesNotExist(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            Map<String, Bean> testMap = new HashMap<>();
            testMap.put("userService",new Bean("userService", new Object()));
            beanCreator.fillRefProperties(testMap,beanDefinitions);
        });
    }
}