package com.cudrania.test.functions;

import com.cudrania.test.bean.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created on 2022/3/10
 *
 * @author liyifei
 */
public class TestSFunction implements Serializable {


    @Test
    @SneakyThrows
    public void test() {
        User user = new User();
        System.out.println(getPropertyName(user::getId));
        System.out.println(getPropertyName(User::getId));
    }

    public static <T> String getPropertyName(SS<T> lambda) {
        return getPropertyName0(lambda);
    }

    //获取lamba表达式中调用方法对应的属性名，比如lamba表达式：User::getSex，则返回字符串"sex"
    public static <T, R> String getPropertyName(SF<T, R> lambda) {
        return getPropertyName0(lambda);
    }

    //获取lamba表达式中调用方法对应的属性名，比如lamba表达式：User::getSex，则返回字符串"sex"
    public static String getPropertyName0(Serializable lambda) {
        try {
            //writeReplace从哪里来的？后面会讲到
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            //调用writeReplace()方法，返回一个SerializedLambda对象
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            //得到lambda表达式中调用的方法名，如 "User::getSex"，则得到的是"getSex"
            String getterMethod = serializedLambda.getImplMethodName();
            //去掉”get"前缀，最终得到字段名“sex"
            String fieldName = Introspector.decapitalize(getterMethod.replace("get", ""));
            return fieldName;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public interface SF<T, R> extends Function<T, R>, Serializable {
    }

    public interface SS<T> extends Supplier<T>, Serializable {
    }
}
