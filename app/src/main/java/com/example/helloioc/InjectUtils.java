package com.example.helloioc;

import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectUtils {
    public static void inject(Object context) {
        //布局的注入
        injectLayout(context);

        //控件的注入
        injectView(context);

        //事件的注入
        injectEvent(context);

    }

    private static void injectEvent(Object context) {
        Class<?> aClass = context.getClass();
        Method[] methods = aClass.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();//注意，别把代码写死了 method.getAnnotation(OnClick.class);
            for (Annotation annotation : annotations) {//每个方法上会有多个注解
                //annotation是事件比如onClick 就去取对应的注解
                Class<? extends Annotation> annotationType = annotation.annotationType();
                EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                //如果没有eventBase，则表示当前方法不是一个事件处理的方法
                if (eventBase == null) {
                    continue;
                }
                //否则就是一个事件处理的方法
                //开始获取事件处理的相关信息（三要素）
                //1.setOnClickListener 订阅关系
//                String listenerSetter();
                String listenerSetter = eventBase.listenerSetter();
                //2.new View.OnClickListener()  事件本身
//                Class<?> listenerType();
                Class<?> listenerType = eventBase.listenerType();
                //3.事件处理程序
//                String callbackMethod();
                String callBackMethod = eventBase.callbackMethod();

                //得到3要素之后，就可以执行代码了
                Method valueMethod = null;
                try {
                    //反射得到id,再根据ID号得到对应的VIEW（Button）
                    valueMethod = annotationType.getDeclaredMethod("value");
                    int[] viewId = (int[]) valueMethod.invoke(annotation);
                    for (int id : viewId) {
                        //为了得到Button对象,使用findViewById
                        Method findViewById = aClass.getMethod("findViewById", int.class);
                        View view = (View) findViewById.invoke(context, id);
                        //运行到这里，view就相到于我们写的Button
                        if (view == null) {
                            continue;
                        }
                        //activity==context    click===method
                        ListenerInvocationHandler listenerInvocationHandler =
                                new ListenerInvocationHandler(context, method);

                        //做代理   new View.OnClickListener()对象
                        Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader()
                                , new Class[]{listenerType}, listenerInvocationHandler);
                        //执行  让proxy执行的onClick()
                        //参数1  setOnClickListener（）
                        //参数2  new View.OnClickListener()对象
                        //   view.setOnClickListener（new View.OnClickListener()）
                        Method onClickMethod = view.getClass().getMethod(listenerSetter, listenerType);
                        onClickMethod.invoke(view, proxy);
                        //这时候，点击按钮时就会去执行代理类中的invoke方法()
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
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
                    Method method = aClass.getMethod("findViewById", int.class);
                    //通过反射方法调用，找到view
                    View findView = (View) method.invoke(context, viewId);
                    field.setAccessible(true);//private 压制
                    field.set(context, findView);
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
