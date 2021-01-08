package com.example.helloioc;

import java.lang.reflect.Method;

public class InjectUtils {
    public static void inject(Object context) {
        //布局的注入
        injectLayout(context);

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
                Method method = context.getClass().getMethod("setContentView",int.class);
                method.invoke(context,layout);//context.setContentView(layout)
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
