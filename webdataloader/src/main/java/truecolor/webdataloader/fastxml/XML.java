package truecolor.webdataloader.fastxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;

import truecolor.webdataloader.fastxml.annotations.XMLField;
import truecolor.webdataloader.fastxml.annotations.XMLProperty;
import truecolor.webdataloader.fastxml.annotations.XMLType;

/**
 * Created by cris on 14-7-9.
 *
 */
public class XML {

    public static <T> T parseObject(String text, Class<T> clazz) {
        XmlPullParser parser = createXmlPullParser(text);
        if(parser == null) return null;

        try {
            if(parser.getEventType() == XmlPullParser.START_DOCUMENT) {
                parser.nextTag();
                if(parser.getName().equals("error")) {
                    return null;
                }
            }
        } catch (XmlPullParserException e) {
            return null;
        } catch(IOException e) {
            return null;
        }

        return parseObject(parser, clazz);
    }

    /*
     * parse & deserialize object
     */
    private static <T> T parseObject(XmlPullParser parser, Class<T> clazz) {
        XMLElement element = parseXmlElement(clazz); // get from clazz
        if(element == null) return null;

        T obj = newInstance(clazz);
        if(obj == null) return null;
        try {
            deserialize(parser, obj, element);
            return obj;
        } catch (XmlPullParserException ignore) {
        } catch(IOException ignore) {
        }
        return null;
    }

    private static void deserialize(XmlPullParser parser, Object object, XMLElement element)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, null, null);

        if(element.properties != null) {
            int num = parser.getAttributeCount();
            for (int i = 0; i < num; i++) {
                String name = parser.getAttributeName(i);
                String value = parser.getAttributeValue(i);
                Field field = element.getProperty(name);
                setFieldValue(object, field, value);
            }
        }

        if(element.field != null) {
            setFieldValue(parser, object, element.field);
        } else {
            while(parser.nextTag() != XmlPullParser.END_TAG) {
                String name = parser.getName();
                XMLElement e = element.getElement(name);
                if(e == null) {
                    skipSubTree(parser);
                    continue;
                }
                deserialize(parser, object, e);
            }
        }
    }

    private static void setFieldValue(Object obj, Field field, String text)
            throws XmlPullParserException, IOException {
        if(text == null || obj == null || field == null) return;
        try {
            Class<?> type = field.getType();
            if(type.isArray()) return;
            // byte, char, short
            if(type == int.class) {
                field.setInt(obj, getInt(text));
            } else if(type == long.class) {
                field.setLong(obj, getLong(text));
            } else if(type == float.class) {
                field.setFloat(obj, getFloat(text));
            } else if(type == double.class) {
                field.setDouble(obj, getDouble(text));
            } else if(type == boolean.class) {
                field.setBoolean(obj, getBoolean(text));
            } else if(type == String.class) {
                field.set(obj, text);
            }
        } catch(IllegalAccessException ignore) {
        }
    }

    private static void setFieldValue(XmlPullParser parser, Object obj, Field field)
            throws XmlPullParserException, IOException {
        if(parser == null || obj == null || field == null) return;
        try {
            Class<?> type = field.getType();
            if(type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if(componentType == int.class) {
                    int value = getInt(parser.nextText());
                    int[] data = (int[])field.get(obj);
                    if(data == null) {
                        data = new int[]{ value };
                    } else {
                        int num = data.length;
                        int[] newData = new int[num + 1];
                        System.arraycopy(data, 0, newData, 0, num);
                        newData[num] = value;
                        data = newData;
                    }
                    field.set(obj, data);
                } else if(componentType == long.class) {
                    long value = getLong(parser.nextText());
                    long[] data = (long[])field.get(obj);
                    if(data == null) {
                        data = new long[]{ value };
                    } else {
                        int num = data.length;
                        long[] newData = new long[num + 1];
                        System.arraycopy(data, 0, newData, 0, num);
                        newData[num] = value;
                        data = newData;
                    }
                    field.set(obj, data);
                } else if(componentType == float.class) {
                    float value = getFloat(parser.nextText());
                    float[] data = (float[])field.get(obj);
                    if(data == null) {
                        data = new float[]{ value };
                    } else {
                        int num = data.length;
                        float[] newData = new float[num + 1];
                        System.arraycopy(data, 0, newData, 0, num);
                        newData[num] = value;
                        data = newData;
                    }
                    field.set(obj, data);
                } else if(componentType == boolean.class) {
                    field.setBoolean(obj, getBoolean(parser.nextText()));
                    boolean value = getBoolean(parser.nextText());
                    boolean[] data = (boolean[])field.get(obj);
                    if(data == null) {
                        data = new boolean[]{ value };
                    } else {
                        int num = data.length;
                        boolean[] newData = new boolean[num + 1];
                        System.arraycopy(data, 0, newData, 0, num);
                        newData[num] = value;
                        data = newData;
                    }
                    field.set(obj, data);
                } else if(componentType == String.class) {
                    String value = parser.nextText();
                    String[] data = (String[])field.get(obj);
                    if(data == null) {
                        data = new String[]{ value };
                    } else {
                        int num = data.length;
                        String[] newData = new String[num + 1];
                        System.arraycopy(data, 0, newData, 0, num);
                        newData[num] = value;
                        data = newData;
                    }
                    field.set(obj, data);
                } else {
                    Object value = parseObject(parser, componentType);
                    Object data = field.get(obj);
                    if(data == null) {
                        data = Array.newInstance(componentType, 1);
                        Array.set(data, 0, value);
                    } else {
                        int num = Array.getLength(data);
                        Object newData = Array.newInstance(componentType, num + 1);
                        System.arraycopy(data, 0, newData, 0, num);
                        Array.set(newData, num, value);
                        data = newData;
                    }
                    field.set(obj, data);
                }
            } else {
                // byte, char, short
                if(type == int.class) {
                    field.setInt(obj, getInt(parser.nextText()));
                } else if(type == long.class) {
                    field.setLong(obj, getLong(parser.nextText()));
                } else if(type == float.class) {
                    field.setFloat(obj, getFloat(parser.nextText()));
                } else if(type == double.class) {
                    field.setDouble(obj, getDouble(parser.nextText()));
                } else if(type == boolean.class) {
                    field.setBoolean(obj, getBoolean(parser.nextText()));
                } else if(type == String.class) {
                    field.set(obj, parser.nextText());
                } else {
                    field.set(obj, parseObject(parser, type));
                }
            }
        } catch(IllegalAccessException ignore) {
        }
    }

    protected static int getInt(String str) {
        return getInt(str, 0);
    }

    protected static int getInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException ignore) {
        }
        return defaultValue;
    }

    protected static long getLong(String str) {
        return getLong(str, 0);
    }

    protected static long getLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch(NumberFormatException ignore) {
        }
        return defaultValue;
    }

    protected static float getFloat(String str) {
        return getFloat(str, 0);
    }

    protected static float getFloat(String str, float defaultValue) {
        if(str == null) return defaultValue;

        try {
            return Float.parseFloat(str);
        } catch(NumberFormatException ignore) {
        }
        return defaultValue;
    }

    protected static double getDouble(String str) {
        return getDouble(str, 0);
    }

    protected static double getDouble(String str, double defaultValue) {
        if(str == null) return defaultValue;

        try {
            return Float.parseFloat(str);
        } catch(NumberFormatException ignore) {
        }
        return defaultValue;
    }

    protected static boolean getBoolean(String str) {
        return getBoolean(str, false);
    }

    protected static boolean getBoolean(String str, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(str);
        } catch(NumberFormatException ignore) {
        }
        return defaultValue;
    }

    private static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, null);
        int level = 1;
        while (level > 0) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.END_TAG) {
                --level;
            } else if (eventType == XmlPullParser.START_TAG) {
                ++level;
            }
        }
    }

    /*
     * parse xml element
     */
    private static HashMap<Class, XMLElement> sXmlElemntCache = new HashMap<Class, XMLElement>();
    private static XMLElement parseXmlElement(Class clazz) {
        if(!clazz.isAnnotationPresent(XMLType.class)) return null;

        // TODO: use cache
        XMLElement root = sXmlElemntCache.get(clazz);
        if(root != null) return root;

        root = new XMLElement();
        XMLElement element = root;

        XMLType xmlType = (XMLType)clazz.getAnnotation(XMLType.class);
        String name = xmlType.name();
        String separator = xmlType.separator();
        if(!"".equals(name)) {
            String[] names = name.split(separator);
            if(names != null && names.length > 0) {
                for(String n : names) {
                    XMLElement e = new XMLElement();
                    element.addElement(n, e);
                    element = e;
                }
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if(field.isAnnotationPresent(XMLField.class)) {
                XMLField xmlField = field.getAnnotation(XMLField.class);
                name = xmlField.name();
                if(!"".equals(name)) {
                    setElementField(element, separator, name, field);
                } else {
                    element.setField(field);
                }
            } else if(field.isAnnotationPresent(XMLProperty.class)) {
                XMLProperty xmlProperty = field.getAnnotation(XMLProperty.class);
                name = xmlProperty.name();
                if(!"".equals(name)) {
                    setElementProperty(element, separator, name, field);
                }
            }
        }

        sXmlElemntCache.put(clazz, root);
        return root;
    }

    private static void setElementField(XMLElement element, String separator, String name, Field field) {
        String[] names = name.split(separator);
        if(names == null || names.length <= 0) return;

        int lastPos = names.length - 1;
        while(lastPos >= 0) {
            String n = names[lastPos];
            if(n != null && !"".equals(n)) break;
            lastPos--;
        }
        if(names[lastPos] == null || "".equals(names[lastPos])) return;

        for(int i = 0; i < lastPos; i++) {
            String n = names[i];
            if(n == null || "".equals(n)) continue;

            XMLElement e = element.getElement(n);
            if(e == null) {
                e = new XMLElement();
                element.addElement(n, e);
            }
            element = e;
        }

        XMLElement e = element.getElement(names[lastPos]);
        if(e != null) {
            throw new IllegalArgumentException("XML parseObject duplicate field name" + names[lastPos]);
        }
        e = new XMLElement();
        e.setField(field);
        element.addElement(names[lastPos], e);
    }

    private static void setElementProperty(XMLElement element, String separator, String name, Field field) {
        String[] names = name.split(separator);
        if(names == null || names.length <= 0) return;

        int lastPos = names.length - 1;
        while(lastPos >= 0) {
            String n = names[lastPos];
            if(n != null && !"".equals(n)) break;
            lastPos--;
        }
        if(names[lastPos] == null || "".equals(names[lastPos])) return;

        for(int i = 0; i < lastPos; i++) {
            String n = names[i];
            if(n == null || "".equals(n)) continue;

            XMLElement e = element.getElement(n);
            if(e == null) {
                e = new XMLElement();
                element.addElement(n, e);
            }
            element = e;
        }

        element.addProperty(names[lastPos], field);
    }

    /*
     * xml pull parser factory
     */
    private static XmlPullParserFactory sFactory;
    private static XmlPullParser createXmlPullParser(String text) {
        if(sFactory == null) {
            try {
                sFactory = XmlPullParserFactory.newInstance();
            } catch (XmlPullParserException e) {
                throw new IllegalStateException("Could not create a factory");
            }
        }

        if(sFactory == null || text == null) return null;

        XmlPullParser parser;
        try {
            parser = sFactory.newPullParser();
            InputStream is = new ByteArrayInputStream(text.getBytes());
            parser.setInput(is, null);
        } catch (XmlPullParserException e) {
            throw new IllegalArgumentException();
        }
        return parser;
    }

    /*
     * new instance by Class
     */
    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch(InstantiationException ignore) {
//            e.printStackTrace();
        } catch(IllegalAccessException ignore) {
//            e.printStackTrace();
        }
        return null;
    }
}
