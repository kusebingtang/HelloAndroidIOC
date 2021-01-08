package com.example.helloioc;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InjectUtils {
    public static void inject(Object context) {
        //布局的注入
        injectLayout(context);

        //控件的注入
        injectView(context);

    }

    private static void injectView(Object context) {
        Class<?> aClass = context.getClass();
        //反射获取Activity的属性对象列表
        Field[] fields = aClass.getDeclaredFields();

        for (Field field : fields) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {//Field 对象有 @ViewInject 注解
                int viewId = viewInject.value();
                //运行到这里，每个按钮的ID已经取到了-->viewId
                //注入就是反射执行findViewById方法
                try {
                    Method method=aClass.getMethod("findViewById",int.class);
                    //通过反射方法调用，找到view
                    View findView = (View) method.invoke(context, viewId);
                    field.setAccessible(true);//private 压制
                    field.set(context,findView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectLayout(Object context) {
        //1、先获取@ContentView(R.layout.activity_main)的值
        int layout = -1;
        Class<?> clazz = context.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);//@ContentView
        if (contentView != null) {
            layout = contentView.value();
            try {
                //setContentView 反射获取
                Method method = context.getClass().getMethod("setContentView", int.class);
                method.invoke(context, layout);//context.setContentView(layout)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
