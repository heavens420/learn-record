package com.zlx.deployment.config;

import com.zlx.deployment.exposeInterface.Calculator;
import com.zlx.deployment.util.DeployUtils;
import com.zlx.deployment.util.SpringAnnotationUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

@Configuration
public class HotDeployWithSpring {

    @Resource
    private ApplicationContext applicationContext;

    private static final String jarAddress = "C:\\Users\\heave\\Desktop\\jar-file\\hot-deployment-0.0.1-SNAPSHOT.jar";
    private static final String jarPath = "file:\\" + jarAddress;

//    @Resource
//    private DefaultListableBeanFactory defaultListableBeanFactory;

    public void hotDeployWithSpring(DefaultListableBeanFactory defaultListableBeanFactory) throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarAddress);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, ClassLoader.getSystemClassLoader());


        for (String className : classNameSet) {
            className = className.replace("BOOT-INF.classes.", "");

            Class clazz = urlClassLoader.loadClass(className);
//            Calculator calculator = (Calculator) clazz.newInstance();
//            int sum = calculator.add(1, 2);
//            System.out.println("sum=" + sum);

            if (DeployUtils.isSpringBeanClass(clazz)) {
                String beanName = DeployUtils.transformName(className);
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
            }
        }
    }


    public void hotDeployWithSpring2(DefaultListableBeanFactory defaultListableBeanFactory) throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarAddress);
//        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader());
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, ClassLoader.getSystemClassLoader());

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        for (String className : classNameSet) {
            className = className.replace("BOOT-INF.classes.", "");

            Class<?> clazz = urlClassLoader.loadClass(className);

            // 是否有spring注解
            Boolean flag = SpringAnnotationUtils.hasSpringAnnotation(clazz);
            if (flag) {
                // 2.2交给spring管理
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
                // 此处beanName使用全路径名是为了防止beanName重复
                String packageName = className.substring(0, className.lastIndexOf(".") + 1);
                String beanName = className.substring(className.lastIndexOf(".") + 1);
                beanName = packageName + beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                // 2.3注册到spring的beanFactory中
                beanFactory.registerBeanDefinition(beanName, beanDefinition);
                // 2.4允许注入和反向注入
                beanFactory.autowireBean(clazz);
                beanFactory.initializeBean(clazz, beanName);
            }
        }
    }

//    @Bean
    public Calculator hotDeployWithReflect() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader());
        Class clazz = urlClassLoader.loadClass("com.zlx.deployment.exposeInterfaceImpl.CalculatorImplWithSpring");
        Calculator calculator = (Calculator) clazz.newInstance();
//        int result = calculator.add(1, 2);
//        System.out.println(result);
        return calculator;

    }

}
