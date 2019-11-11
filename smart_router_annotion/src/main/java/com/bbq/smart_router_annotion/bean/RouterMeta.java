package com.bbq.smart_router_annotion.bean;

import com.bbq.smart_router_annotion.Router;

import java.util.Arrays;

import javax.lang.model.element.Element;

public class RouterMeta {
    /**
     * 路由的类型
     */
    private int type;

    /**
     * 节点类,支持类型
     */
    private Element element;

    /**
     * 注解使用的类对象
     */
    private Class<?> destination;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 路由组
     */
    private String group;

    /**
     * 拦截器
     */
    private String[] interceptors;

    /**
     * 注解类
     */
    private Router router;

    public static RouterMeta build(int type, Class<?> destination, String path, String group, String... interceptors) {
        return new RouterMeta(type, null, destination, path, group, interceptors);
    }


    public RouterMeta() {
    }

    public RouterMeta(int type, Router router, Element element, String path, String... interceptors) {
        this(type, element, null, path, router.group(), interceptors);
        this.router = router;
    }

    public RouterMeta(int type, Element element, Class<?> destination, String path, String group, String... interceptors) {
        this.type = type;
        this.destination = destination;
        this.element = element;
        this.path = path;
        this.group = group;
        this.interceptors = interceptors;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(String... interceptors) {
        this.interceptors = interceptors;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public String toString() {
        return "RouterMeta{" +
                "type=" + type +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", interceptors=" + Arrays.toString(interceptors) +
                '}';
    }
}

