package com.cudrania.spring.beans;

import com.nianien.core.reflect.Reflections;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * {@link SpringBean}对象的调度类<br/> for example:
 * <pre>
 * package com.my.bean
 * &#064;ImportResource("classpath:spring-root.xml")
 * public class MyBean extends {@link SpringBean} {
 *
 *    &#064;AutoWired
 *    private Service service;
 *
 *    public void doSomething(){
 *       service.doSomething();
 *    }
 *
 *    public void doOtherThing(int a,String b,...){
 *       service.doSomething();
 *    }
 *  }
 * </pre>
 * 调用命令如下:
 * <ol>
 * <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doSomething</li>
 * <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doOtherThing 1 test</li>
 * </ol>
 *
 * @author skyfalling
 * @date 16/11/15
 * @see SpringBean
 */
public class BeanRunner {

    /**
     * bean对象方法执行入口
     *
     * @param args beanClass#methodName [args...]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String beanClass = StringUtils.substringBeforeLast(args[0], "#");
        String method = StringUtils.substringAfterLast(args[0], "#");
        String[] params;
        if (StringUtils.isEmpty(method)) {
            method = args[1];
            params = Arrays.copyOfRange(args, 2, args.length);
        } else {
            params = Arrays.copyOfRange(args, 1, args.length);
        }

        Object bean = Class.forName(beanClass).newInstance();
        if (bean instanceof SpringBean) {
            bean = ((SpringBean) bean).init();
        }
        Reflections.invoke(method, bean, params);
    }


}
