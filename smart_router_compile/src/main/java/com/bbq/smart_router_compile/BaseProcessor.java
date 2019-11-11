package com.bbq.smart_router_compile;

import com.bbq.smart_router_annotion.bean.Constans;
import com.bbq.smart_router_compile.utils.Debugger;
import com.bbq.smart_router_compile.utils.RouterCompileUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {
    //文件生成器 类/资源
    protected Filer filer;
    //类型工具类
    protected Types typeUtils;
    //节点工具类
    protected Elements elementUtils;
    //消息类
    protected Messager messager;
    //获取传递的参数（主要用于获取moduleName）
    protected Map<String, String> options;
    //模块的名称
    protected String moduleName;
    //log调式
    protected Debugger log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        log = new Debugger(messager);
        filer = processingEnvironment.getFiler();
        options = processingEnvironment.getOptions();
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        // 获取传递的参数（get module name）
        if (!options.isEmpty()) {
            moduleName = options.get(Constans.ARGUMENTS_NAME);
            log.i(">>> module annotationProcessor init:" + moduleName);
        }
        //需要用到moduleName进行分组
        if (RouterCompileUtils.isEmpty(moduleName)) {
            throw new NullPointerException("Not set Processor Parmaters.Please set module name in gradle");
        }
    }

    /**
     * 从字符串获取TypeElement对象
     */
    public TypeElement typeElement(String className) {
        return elementUtils.getTypeElement(className);
    }

    /**
     * 从字符串获取TypeMirror对象
     */
    public TypeMirror typeMirror(String className) {
        return typeElement(className).asType();
    }

    /**
     * 从字符串获取ClassName对象
     */
    public ClassName className(String className) {
        return ClassName.get(typeElement(className));
    }

    /**
     * 从字符串获取TypeName对象，包含Class的泛型信息
     */
    public TypeName typeName(String className) {
        return TypeName.get(typeMirror(className));
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    public boolean isSubType(TypeMirror type, String className) {
        return type != null && typeUtils.isSubtype(type, typeMirror(className));
    }

    public boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    public boolean isSubType(Element element, TypeMirror typeMirror) {
        return element != null && typeUtils.isSubtype(element.asType(), typeMirror);
    }

    /**
     * 非抽象类
     */
    public boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(
                Modifier.ABSTRACT);
    }

    /**
     * 非抽象子类
     */
    public boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    /**
     * 非抽象子类
     */
    public boolean isConcreteSubType(Element element, TypeMirror typeMirror) {
        return isConcreteType(element) && isSubType(element, typeMirror);
    }
}
