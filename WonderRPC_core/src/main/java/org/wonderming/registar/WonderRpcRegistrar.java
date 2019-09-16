package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.wonderming.annotation.EnableWonderRpc;
import org.wonderming.annotation.WonderRpcClient;

import java.io.IOException;
import java.util.*;

/**
 * 模仿Feign客户端 重写一份自动扫描注解
 * @author wangdeming
 * @date 2019-09-08 17:24
 **/
public class WonderRpcRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

    private static final String BASE_PACKAGES = "basePackages";

    private static final String CLIENTS = "clients";

    static {
        System.out.println("***************************************");
        System.out.println("***************************************");
        System.out.println(" __        __              _           ");
        System.out.println(" \\ \\      / /__  _ __   __| | ___ _ __ ");
        System.out.println("  \\ \\ /\\ / / _ \\| '_ \\ / _` |/ _ \\ '__|");
        System.out.println("   \\ V  V / (_) | | | | (_| |  __/ |   ");
        System.out.println("    \\_/\\_/ \\___/|_| |_|\\__,_|\\___|_|  ");
        System.out.println("***************************************");
        System.out.println("***************************************");
    }

    private ResourceLoader resourceLoader;

    private Environment environment;

    private ClassLoader classLoader;

    private WonderRpcRegistrar(){

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerWonderClients(metadata,registry);
    }

    /**
     * 处理注解的扫描包
     * @param metadata AnnotationMetadata
     * @param registry BeanDefinitionRegistry
     */
    private void registerWonderClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        Set<String> basePackages;
        final Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableWonderRpc.class.getName());
        final AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(WonderRpcClient.class);
        final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get(CLIENTS);
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = getBasePackages(metadata);
        }else {
            final Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }

        basePackages.forEach(bp -> {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(bp);
            candidateComponents.forEach(candidateComponent -> {
                if (candidateComponent instanceof AnnotatedBeanDefinition){
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    final AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),"@WonderRpcClient can only be specified on an interface");
                    final Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(WonderRpcClient.class.getCanonicalName());
                    assert attributes != null;
                    registerWonderClient(registry,annotationMetadata,attributes);
                }
            });
        });
    }

    /**
     * 将接口类注册成BeanDefinition,装载到IOC容器中
     * @param registry BeanDefinitionRegistry
     * @param annotationMetadata AnnotationMetadata
     * @param attributes Map<String, Object>
     */
    private void registerWonderClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> attributes){
        String className = annotationMetadata.getClassName();
        final BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(WonderRpcClientFactoryBean.class);
        definition.addPropertyValue("name",attributes.get("name"));
        definition.addPropertyValue("methodInterceptor",getInterceptor());
        definition.addPropertyValue("proxyClass",attributes.get("proxyClass"));
        try {
            //Java反射获取类
            definition.addPropertyValue("type",Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        final AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary((Boolean) attributes.get("primary"));
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] {className});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder,registry);
    }

    /**
     * 判断扫描的是不是独立的Component或者是接口
     * @return ClassPathScanningCandidateComponentProvider
     */
    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()){
                    if (beanDefinition.getMetadata().isInterface()){
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(),WonderRpcRegistrar.this.classLoader);
                            return !target.isAnnotation();
                        } catch (ClassNotFoundException e) {
                            this.logger.error( "Could not load target class: " + beanDefinition.getMetadata().getClassName(),e);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableWonderRpc.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        assert attributes != null;
        for (String pkg : (String[]) attributes.get(BASE_PACKAGES)){
            if (StringUtils.hasText(pkg)){
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }


    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }

    private MethodInterceptor getInterceptor(){
        return new WonderRpcInterceptor();
    }
}
