package com.priamm.xmlrpc.serializer;

import com.priamm.xmlrpc.XMLRPCException;
import com.priamm.xmlrpc.XMLUtil;
import com.priamm.xmlrpc.xmlcreator.XmlElement;
import java.math.BigDecimal;
import org.w3c.dom.Element;

/**
 * This serializer is responsible for floating point numbers.
 * 
 * @author Tim Roes
 */
public class DoubleSerializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return Double.valueOf(XMLUtil.getOnlyTextContent(content.getChildNodes()));
	}

	public XmlElement serialize(Object object) {
		// Turn double value of object into a BigDecimal to get the
		// right decimal point format.
		BigDecimal bd = BigDecimal.valueOf(((Number)object).doubleValue());
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_DOUBLE, bd.toPlainString());
	}

}
