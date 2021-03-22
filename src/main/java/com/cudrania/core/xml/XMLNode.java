package com.cudrania.core.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * XML文档对应的内存节点类
 *
 * @author skyfalling
 */
public class XMLNode {
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点文本内容
     */
    private String nodeText = "";
    /**
     * 节点Map对象
     */
    private Map<String, String> attributes = new HashMap<String, String>();
    /**
     * 父节点,根节点无父节点
     */
    private XMLNode parent;
    /**
     * 孩子结点列表
     */
    private List<XMLNode> children = new ArrayList<XMLNode>();


    /**
     * 获取父节点
     *
     * @return
     */
    public XMLNode getParent() {
        return parent;
    }

    /**
     * 设置父节点
     *
     * @param parent
     */
    public void setParent(XMLNode parent) {
        this.parent = parent;
    }

    /**
     * 添加属性
     *
     * @param name
     * @param value
     */
    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    /**
     * 设置节点属性
     *
     * @param map
     */
    public void addAttributes(Map<String, String> map) {
        if (map == null || map.isEmpty())
            return;
        this.attributes.putAll(map);
    }

    /**
     * 添加孩子结点
     *
     * @param xmlNode
     */
    public void addChild(XMLNode xmlNode) {
        this.children.add(xmlNode);
    }

    /**
     * 添加孩子结点
     *
     * @param children
     */
    public void addChildren(List<XMLNode> children) {
        this.children.addAll(children);
    }

    /**
     * 根据属性名称获取节点属性
     *
     * @param attributeName
     * @return 节点属性
     */
    public String getAttribute(String attributeName) {
        return this.attributes.get(attributeName);
    }

    /**
     * 获取节点所有属性
     *
     * @return 节点属性Map对象
     */
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    /**
     * 获取索引值为index的孩子节点
     *
     * @param index
     * @return XMLNode对象
     */
    public XMLNode getChild(int index) {
        if (index >= 0 && index < this.getNumberOfChildren()) {
            return this.children.get(index);
        }
        return null;
    }

    /**
     * 获取名为name的孩子节点
     *
     * @param name
     * @return XMLNode对象
     */
    public XMLNode getChild(String name) {
        for (XMLNode node : this.children) {
            if (node.nodeName.equals(name))
                return node;
        }
        return null;
    }

    /**
     * 获取所有孩子结点
     *
     * @return XMLNode实例列表
     */
    public List<XMLNode> getChildren() {
        return this.children;
    }

    /**
     * 获取所有名为name的孩子结点
     *
     * @param name
     * @return XMLNode实例列表
     */
    public List<XMLNode> getChildren(final String name) {
        return getChildren(node -> node.nodeName.equals(name));
    }

    /**
     * 获取所有符合选择条件的孩子结点
     *
     * @param selector
     * @return XMLNode实例列表
     */
    public List<XMLNode> getChildren(Predicate<XMLNode> selector) {
        List<XMLNode> list = new ArrayList<XMLNode>();
        for (XMLNode node : this.children) {
            if (selector.test(node))
                list.add(node);
        }
        return list;
    }

    /**
     * 获取节点名称
     *
     * @return 节点名称
     */
    public String getNodeName() {
        return this.nodeName;
    }

    /**
     * 获取节点文本内容
     *
     * @return 节点文本内容
     */
    public String getNodeText() {
        return this.nodeText;
    }

    /**
     * 获取孩子结点个数
     *
     * @return 孩子结点个数
     */
    public int getNumberOfChildren() {
        return this.children.size();
    }

    /**
     * 是否有孩子结点
     *
     * @return 如果有孩子结点返回true, 否则返回false.
     */
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    /**
     * 设置节点名称
     *
     * @param nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * 设置节点内容
     *
     * @param nodeText
     */
    public void setNodeText(String nodeText) {
        this.nodeText = nodeText;
    }

}
