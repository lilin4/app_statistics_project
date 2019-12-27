package util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class PropertiesUtil {

    public static void copyProperties(Object src, Object dest) {
        try {
            //源对象BI
            BeanInfo bisrc = Introspector.getBeanInfo(src.getClass());
            //目标对象BI
            //BeanInfo bidest = Introspector.getBeanInfo(dest.getClass());
            //属性描述符
            PropertyDescriptor[] propertyDescriptors = bisrc.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                //get方法
                Method getMethod = pd.getReadMethod();
                //set方法
                Method setMethod = pd.getWriteMethod();
                //方法名
                String name = setMethod.getName();
                //方法的类型
                Class<?>[] parameterTypes = setMethod.getParameterTypes();
                //获取src get方法返回值
                Object value = getMethod.invoke(src);
                try {
                   // setMethod.invoke(dest, value);
                    Method destSteter = dest.getClass().getMethod(name, parameterTypes);
                    destSteter.invoke(dest,value);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
