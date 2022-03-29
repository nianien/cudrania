package com.cudrania.core.xml;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建Document对象,从而获取XMLNode集合
 *
 * @author skyfalling
 */
public class XMLDocument {
    /**
     * 获取指定名称的XML节点集合
     *
     * @param document
     * @param nodeName
     * @return 获取document对象中名称为nodeName的一级节点集合
     */
    public static List<XMLNode> getXMLNodes(Document document, String nodeName) {
        List<XMLNode> xmlNodes = new ArrayList<XMLNode>();
        for (Element element : select(document.getRootElement(), nodeName)) {
            xmlNodes.add(toXmlNode(element));
        }
        return xmlNodes;
    }

    /**
     * 根据文件对象创建XML文档
     *
     * @param file
     * @return Document对象
     */
    public static Document xmlFromFile(File file) {
        try {
            return xmlFromStream(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 根据文件路径创建XML文档
     *
     * @param filePath
     * @return Document对象
     */
    public static Document xmlFromPath(String filePath) {
        return xmlFromFile(new File(filePath));
    }

    /**
     * 根据InputStream对象创建XML文档
     *
     * @param inputStream
     * @return Document对象
     */
    public static Document xmlFromStream(InputStream inputStream) {
        try {
            SAXReader saxReader = createSAXReader();
            // 这里自动关闭流?
            return saxReader.read(inputStream);
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(inputStream);
        }
    }

    /**
     * 根据文本创建XML文档,并对字符"&amp;"进行转义
     *
     * @param text
     * @return Document对象
     */
    public static Document xmlFromText(String text) {
        try {
            // 将不作为转义功能的字符&进行转义
            InputSource source = new InputSource(new StringReader(
                    text.replaceAll("&(?!gt;|lt;|quot;|apos;|amp;)", "&amp;")));
            return createSAXReader().read(source);
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 创建SAXReader对象,不验证DTD
     *
     * @return
     * @throws Exception
     */
    private static SAXReader createSAXReader() {
        try {
            SAXReader reader = new SAXReader();
            reader.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            return reader;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }


    /**
     * Element对象转XMLNode对象
     *
     * @param element
     */
    private static XMLNode toXmlNode(Element element) {
        XMLNode xmlNode = new XMLNode();
        // 复制节点名称
        xmlNode.setNodeName(element.getName());
        // 复制节点内容
        xmlNode.setNodeText(element.getTextTrim());
        // 赋值结点属性
        for (Object obj : element.attributes()) {
            Attribute attr = (Attribute) obj;
            xmlNode.addAttribute(attr.getName(), attr.getText());
        }
        for (Object e : element.elements()) {
            // 递归调用
            XMLNode child = toXmlNode((Element) e);
            child.setParent(xmlNode);
            // 添加当前孩子
            xmlNode.addChild(child);
        }
        return xmlNode;
    }

    /**
     * 根据路径选择节点,以根节点为当前路径
     *
     * @param root
     * @param xpath
     * @return
     */
    private static List<Element> select(Element root, String xpath) {
        List<Element> list = new ArrayList<Element>();
        list.add(root);
        String[] names = xpath.split("/");
        for (String name : names) {
            if (name.isEmpty())
                continue;
            List<Element> next = new ArrayList<Element>();
            for (Element e : list) {
                next.addAll(e.elements(name));
            }
            list = next;
        }
        return list;
    }

}
