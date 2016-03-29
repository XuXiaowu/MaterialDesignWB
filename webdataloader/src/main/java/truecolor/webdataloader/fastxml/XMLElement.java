package truecolor.webdataloader.fastxml;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by cris on 14-7-11.
 *
 */
public class XMLElement {

    public Field field;
    public HashMap<String, Field> properties;
    public HashMap<String, XMLElement> elements;

    public void setField(Field field) {
        if(field == null) return;
        if(elements != null) {
            throw new IllegalArgumentException("XMLElement field & elements must not be NON NULL at same time");
        }
        this.field = field;
    }

    public void addProperty(String name, Field field) {
        if(name == null || field == null) return;
        if(properties == null) properties = new HashMap<String, Field>();
        properties.put(name, field);
    }

    public Field getProperty(String name) {
        if(properties == null) return null;
        return properties.get(name);
    }

    public void addElement(String name, XMLElement element) {
        if(name == null || element == null) return;
        if(field != null) {
            throw new IllegalArgumentException("XMLElement field & elements must not be NON NULL at same time");
        }
        if(elements == null) elements = new HashMap<String, XMLElement>();
        elements.put(name, element);
    }

    public XMLElement getElement(String name) {
        if(elements == null) return null;
        return elements.get(name);
    }
}
