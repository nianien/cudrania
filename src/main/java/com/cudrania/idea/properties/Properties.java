package com.cudrania.idea.properties;

import com.cudrania.core.io.Files;
import com.cudrania.core.loader.PropertiesLoader;
import com.cudrania.core.loader.ResourceLoader;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.core.xml.XMLDocument;
import com.cudrania.core.xml.XMLNode;

import org.dom4j.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可分组的属性配置类
 *
 * @author skyfalling
 */
public class Properties {

    /**
     * 默认包
     */
    private String defaultPackage;
    /**
     * 已加载的文件列表
     */
    private List<String> includes = new ArrayList<String>();
    /**
     * Map对象,用于存放配置属性
     */
    private Map<String, String> map = new ConcurrentHashMap<String, String>();

    /**
     * 构造方法,指定配置文件
     *
     * @param fileName
     */
    public Properties(String fileName) {
        this.loaFile(fileName);
    }

    /**
     * 私有构造方法
     */
    private Properties(Map<String, String> map) {
        this.map = map;
    }

    /**
     * 获取默认包中关键字获取对应的Value值
     *
     * @param key
     * @return String
     */
    public String getProperty(String key) {
        return this.getProperty(key, defaultPackage);
    }

    /**
     * 获取指定包中关键字获取对应的属性值<br>
     * 该方法的返回值不依赖于默认包
     *
     * @param key
     * @param packageName 包名
     * @return String
     */
    public String getProperty(String key, String packageName) {
        String leader = this.getPackageLeader(packageName);
        return this.map.get(leader + key);
    }

    /**
     * 获取默认包下的所有配置属性名称
     *
     * @return List&ltString>
     */
    public List<String> propertyNames() {
        return propertyNames(defaultPackage);
    }

    /**
     * 获取指定包下的所有配置属性名称
     *
     * @param packageName
     * @return List&ltString>
     */
    public List<String> propertyNames(String packageName) {
        List<String> list = new ArrayList<String>();
        for (String str : this.map.keySet()) {
            if (StringUtils.isEmpty(packageName) || str.startsWith(packageName)) {
                list.add(str);
            }
        }
        return list;
    }

    /**
     * 复制指定对象的配置,并设置默认包<br>
     * 当指定默认包后,获取的属性值都位于默认包或其子包中
     *
     * @param packageName
     * @return PackageProperty
     */
    public Properties defaultPackage(String packageName) {
        Properties p = new Properties(this.map);
        p.defaultPackage = packageName;
        return p;
    }

    /**
     * 获取当前配置对象的默认包
     *
     * @return
     */
    public String getDefaultPackage() {
        return defaultPackage;
    }


    /**
     * 加载Properties或XML配置文件
     *
     * @param fileName
     */
    private void loaFile(String fileName) {
        if (".xml".equalsIgnoreCase(Files.getFileExt(fileName))) {
            this.loadXML(fileName);
        } else {
            Map<Object, Object> pros = PropertiesLoader.load(new File(fileName));
            for (Object obj : pros.keySet()) {
                map.put((String) obj, (String) pros.get(obj));
            }
        }
    }

    /**
     * 加载XML配置文件
     *
     * @param fileName
     */
    private void loadXML(String fileName) {
        if (this.includes.contains(fileName))
            return;
        includes.add(fileName);
        Document document = XMLDocument.xmlFromFile(ResourceLoader
                .getFile(fileName));
        loadXmlProperty(XMLDocument.getXMLNodes(document, "property"), null);
        loadXmlPackage(XMLDocument.getXMLNodes(document, "package"), null);
        for (XMLNode node : XMLDocument.getXMLNodes(document, "include")) {
            loaFile(node.getAttribute("file"));
        }
    }

    /**
     * 加载XML文件的Package节点
     *
     * @param nodes
     * @param packageName
     */
    private void loadXmlPackage(List<XMLNode> nodes, String packageName) {
        String leader = this.getPackageLeader(packageName);
        for (XMLNode node : nodes) {
            loadXmlProperty(node.getChildren("property"), leader
                    + node.getAttribute("name"));
            loadXmlPackage(node.getChildren("package"), leader
                    + node.getAttribute("name"));
        }
    }

    /**
     * 加载XML文件的Property节点
     *
     * @param nodes
     * @param packageName
     */
    private void loadXmlProperty(List<XMLNode> nodes, String packageName) {
        String leader = this.getPackageLeader(packageName);
        for (XMLNode node : nodes) {
            this.map.put(leader + node.getAttribute("key"), node
                    .getAttribute("value"));
        }
    }

    /**
     * 根据PackageName获取相应的前导符
     *
     * @param packageName
     * @return
     */
    private String getPackageLeader(String packageName) {
        return StringUtils.isEmpty(packageName) ? "" : packageName
                + ".";
    }


}
