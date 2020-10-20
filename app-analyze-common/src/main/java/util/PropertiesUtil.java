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
                //set方法
                //Method setMethod = pd.getWriteMethod();
                // String name = setMethod.getName();
                //Class<?>[] parameterTypes = setMethod.getParameterTypes();
                //get方法
                Method getMethod = pd.getReadMethod();
                //方法名
                String name =getMethod.getName();
                //方法的类型
                Class<?>[] parameterTypes =getMethod.getParameterTypes();
                //获取src get方法返回值
                Object value = getMethod.invoke(src);
                try {
                    //Method destSteter = 
                    dest.getClass().getMethod(name, parameterTypes).invoke(dest,value);
                    //destSteter.invoke(dest,value);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
