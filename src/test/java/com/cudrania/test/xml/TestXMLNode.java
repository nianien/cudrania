package com.cudrania.test.xml;

import com.cudrania.core.loader.ResourceLoader;
import com.cudrania.core.xml.XMLDocument;
import com.cudrania.core.xml.XMLNode;

import org.dom4j.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lining05
 * Date: 2013-02-07
 */
public class TestXMLNode {
    @Test
    public void test() {
        Document document = XMLDocument.xmlFromFile(ResourceLoader.getFile("datasource.xml"));
        List<XMLNode> list = XMLDocument.getXMLNodes(document, "/source/config");
        System.out.println(list.size());

        for (XMLNode node : list) {
            System.out.println(node.getAttributes());
        }
    }
}
