package com.bbq.smart_router_annotion.bean;

public class Constans {
    //包名
    public static final String PKG = "com.bbq.smart_router";

    //.
    public static final String DOT = ".";

    //默认分组名
    public static final String DEFAULT_GROUP_NAME = "router";

    //获取moduleName的key
    public static final String ARGUMENTS_NAME = "ROUTER_MODULE_NAME";

    //Android中的类名
    public static final String ACTIVITY = "android.app.Activity";
    public static final String FRAGMNET = "android.app.Fragment";
    public static final String V4FRAGMENT = "android.support.v4.app.Fragment";

    //路由中的接口类
    public static final String SERVICE = PKG + DOT + "interfaces.IService";
    public static final String INTERCEPTOR = PKG + DOT + "interface.IInterceptor";
    public static final String ROUTEGROUP = PKG + DOT + "interfaces.IRouterGroup";
    public static final String ROUTEROOT = PKG + DOT + "interfaces.IRouterRoot";

    //注解生成类的路径和类名
    public static final String PAGENAME = PKG + DOT + "generated";
    public static final String GROUP_CLASS_NAME = "Router$$Group$$";
    public static final String ROOT_CLASS_NAME = "Router$$Root$$";

    //注解生成类的参数名
    public static final String GROUP_PARAM_NAME = "routerMap";
    public static final String ROOT_PARAM_NAME = "routers";

    //注解生成类的方法名
    public static final String GROUP_METHOD_NAME = "loadInfo";
    public static final String ROOT_METHOD_NAME = "loadInfo";

}
