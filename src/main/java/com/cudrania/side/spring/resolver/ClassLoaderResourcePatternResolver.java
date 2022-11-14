package com.cudrania.side.spring.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * 根据ClassLoader加载Spring资源
 * Created on 2022/10/29
 *
 * @author liyifei
 */
@Slf4j
public class ClassLoaderResourcePatternResolver extends PathMatchingResourcePatternResolver {

    private boolean includeParent;

    /**
     * 指定ClassLoader,用于加载spring资源
     *
     * @param classLoader
     */
    public ClassLoaderResourcePatternResolver(ClassLoader classLoader) {
        this(classLoader, false);
    }

    /**
     * 指定ClassLoader,用于加载spring资源
     *
     * @param classLoader   用于加载资源的classloader
     * @param includeParent 是否加载父classloader的资源
     */
    public ClassLoaderResourcePatternResolver(ClassLoader classLoader, boolean includeParent) {
        super(classLoader);
    }

    /**
     * 重写该方法,不加载父ClassLoader资源
     *
     * @param classLoader
     * @param result
     */
    @Override
    protected void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) ?
                                new UrlResource(url) :
                                new UrlResource(ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR));
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    } catch (MalformedURLException ex) {
                        if (log.isDebugEnabled()) {
                            log.debug("Cannot search for matching files underneath [" + url +
                                    "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files since ClassLoader [" + classLoader +
                            "] does not support 'getURLs()': " + ex);
                }
            }
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries(result);
        }

        if (classLoader != null && includeParent) {
            try {
                // Hierarchy traversal...
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader +
                            "] does not support 'getParent()': " + ex);
                }
            }
        }
    }
}
