package com.bbq.smart_router_compile;

import com.bbq.smart_router_annotion.Router;
import com.bbq.smart_router_annotion.bean.Constans;
import com.bbq.smart_router_annotion.bean.RouterMeta;
import com.bbq.smart_router_annotion.bean.RouterType;
import com.bbq.smart_router_compile.utils.RouterCompileUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.SourceVersion.RELEASE_7;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
/**
 * compiled java version {@link AbstractProcessor#getSupportedSourceVersion()}
 */
@SupportedSourceVersion(RELEASE_7)

/**
 * 注解处理器接收的参数 {@link AbstractProcessor#getSupportedOptions()}
 */
@SupportedOptions({Constans.ARGUMENTS_NAME})

/**
 * Router 注解处理器
 */
public class RouterProcessor extends BaseProcessor {
    //key 组名  value 类名
    private Map<String, String> rootMap = new TreeMap<>();
    //分组 key 组名  value 对应组的路由信息
    private Map<String, List<RouterMeta>> groupMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set != null && !set.isEmpty()) {
            //Set nodes annotated by Router
            Set<? extends Element> annotatedWith = roundEnvironment.getElementsAnnotatedWith(Router.class);
            if (annotatedWith != null && annotatedWith.size() > 0) {
                processRouter(annotatedWith);
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(Router.class.getName()));
    }

    /**
     * 处理被注解的节点集合
     *
     * @param annotatedWith
     */
    private void processRouter(Set<? extends Element> annotatedWith) {
        RouterMeta routerMeta = null;
        //获得Activity的类型
        TypeElement activity = elementUtils.getTypeElement(Constans.ACTIVITY);
        //获取Service的类型
        TypeElement service = elementUtils.getTypeElement(Constans.SERVICE);
        //获取Fragment的类型
        TypeElement fragment = elementUtils.getTypeElement(Constans.FRAGMNET);
        //获取v4Fragment的类型
        TypeElement v4Fragment = elementUtils.getTypeElement(Constans.V4FRAGMENT);
        //代码块生成器
        CodeBlock.Builder builder = CodeBlock.builder();
        //单个的节点
        for (Element element : annotatedWith) {
            // 获取类信息 如Activity类
            TypeMirror typeMirror = element.asType();
            // 获取节点的注解信息
            Symbol.ClassSymbol cls = (Symbol.ClassSymbol) element;
            Router annotation = cls.getAnnotation(Router.class);
            if (annotation == null) {
                continue;
            }
            log.i(">>> start process annotation:targetClass：" + typeMirror + " | " + annotation);
            String[] pathList = annotation.path();
            for (String path : pathList) {
                //只能指定的类上面使用
                if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                    //存储Activity路由相关的信息,只有activity可配置拦截器
                    routerMeta = new RouterMeta(RouterType.ACTIVITY, annotation, element, path);
                } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                    //存储Service路由相关的信息
                    routerMeta = new RouterMeta(RouterType.SERVICE, annotation, element, path);
                } else if (typeUtils.isSubtype(typeMirror, fragment.asType()) || typeUtils.isSubtype(typeMirror, v4Fragment.asType())) {
                    //存储Fragment路由相关的信息
                    routerMeta = new RouterMeta(RouterType.FRAGMENT, annotation, element, path);
                } else {
                    log.i("-----typeUtils:" + typeUtils.toString());
                    throw new RuntimeException("Just Support Activity Fragment And Service Router!");
                }
                //检查是否配置group如果没有配置 则从path中截取组名
                checkRouterGroup(routerMeta);
            }
        }
        //获取IRouterGroup 类节点
        TypeElement routeGroupElement = elementUtils.getTypeElement(Constans.ROUTEGROUP);
        //获取IRouterRoot 类节点
        TypeElement routeRootElement = elementUtils.getTypeElement(Constans.ROUTEROOT);
        //生成 $$Group$$ 记录分组表
        generatedGroupTable(routeGroupElement, builder.build());
        //生成 $$Root$$ 记录路由表
        generatedRootTable(routeRootElement, routeGroupElement);
    }

    /**
     * 生成路由表class 类
     *
     * @param routeRootElement  路由表根节点
     * @param routeGroupElement 路由分组节点
     */
    private void generatedRootTable(TypeElement routeRootElement, TypeElement routeGroupElement) {
        //类型 Map<String,Class<? extends IRouteGroup>> routes>
        ParameterizedTypeName atlas = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(routeGroupElement))));
        //创建参数  Map<String,Class<? extends IRouteGroup>>> routes
        ParameterSpec altlas = ParameterSpec
                .builder(atlas, Constans.ROOT_PARAM_NAME)//参数名
                .build();//创建参数
        //public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadInfoMethodOfRootBuilder = MethodSpec.methodBuilder
                (Constans.ROOT_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(altlas);
        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadInfoMethodOfRootBuilder.addStatement(Constans.ROOT_PARAM_NAME + ".put($S, $T.class)",
                    entry.getKey(), ClassName.get(Constans.PAGENAME, entry.getValue()));
        }
        //生成 $Root$类
        String rootClassName = Constans.ROOT_CLASS_NAME + moduleName;
        try {
            JavaFile.builder(Constans.PAGENAME,
                    TypeSpec.classBuilder(rootClassName)
                            .addSuperinterface(ClassName.get(routeRootElement))
                            .addModifiers(PUBLIC)
                            .addMethod(loadInfoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成分组表class 类
     *
     * @param routeGroupElement IRouterGroup 类节点
     */
    private void generatedGroupTable(TypeElement routeGroupElement, CodeBlock codeBlock) {
        //创建参数类型 Map<String,RouterMeta>
        ParameterizedTypeName atlas = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterMeta.class));
        //创建参数 Map<String,RouterMeta> atlas
        ParameterSpec altlas = ParameterSpec
                .builder(atlas, Constans.GROUP_PARAM_NAME)//参数名
                .build();//创建参数
        //遍历分组 每一个分组 创建一个 $$Group$$类
        for (Map.Entry<String, List<RouterMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(Constans.GROUP_METHOD_NAME)//函数名
                    .addModifiers(PUBLIC)//作用域
                    .addAnnotation(Override.class)//添加一个注解
                    .addParameter(altlas);//添加参数
            // Group组中
            List<RouterMeta> groupData = entry.getValue();
            //遍历 生成函数体
            for (RouterMeta meta : groupData) {
                //添加函数体
                CodeBlock interceptors = buildInterceptors(getInterceptors(meta.getRouter()));
                //$S = 字符串
                //$T = 类，接口，或者枚举
                //$N = 方法名或者变量
                //$L = 数字
                //添加函数体
                builder.addStatement(Constans.GROUP_PARAM_NAME + ".put($S,$T.build($L,$T.class,$S,$S$L))",
                        meta.getPath(),
                        ClassName.get(RouterMeta.class),
                        meta.getType(),
                        ClassName.get((TypeElement) meta.getElement()),
                        meta.getPath(),
                        meta.getGroup(),
                        interceptors);
            }
            MethodSpec loadInto = builder.build();//函数创建完成loadInto();
            String groupClassName = Constans.GROUP_CLASS_NAME + entry.getKey();
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)//类名
                    .addSuperinterface(ClassName.get(routeGroupElement))//实现接口IRouteGroup
                    .addModifiers(PUBLIC)//作用域
                    .addMethod(loadInto)//添加方法
                    .build();//类创建完成
            //生成Java文件
            JavaFile javaFile = JavaFile
                    .builder(Constans.PAGENAME, typeSpec)//包名和类
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootMap.put(entry.getKey(), groupClassName);
        }

    }

    /**
     * 检查设置路由组
     *
     * @param routerMeta
     */
    private void checkRouterGroup(RouterMeta routerMeta) {
        if (routerVerify(routerMeta)) {
            List<RouterMeta> routerMetas = groupMap.get(routerMeta.getGroup());
            if (RouterCompileUtils.isEmpty(routerMetas)) {
                routerMetas = new ArrayList<>();
                routerMetas.add(routerMeta);
                groupMap.put(routerMeta.getGroup(), routerMetas);
            } else {
                routerMetas.add(routerMeta);
            }
        } else {
            log.i("router path no verify,please check");
        }
    }

    /**
     * 验证路由地址配置是否正确合法性
     *
     * @param routerMeta 存储的路由bean对象
     * @return true 路由地址配置正确  false 路由地址配置不正确
     */
    private boolean routerVerify(RouterMeta routerMeta) {
        String path = routerMeta.getPath();
        //路径不能为null
        if (RouterCompileUtils.isEmpty(path)) {
            throw new NullPointerException("@Router path not to be null or to length() == 0 ");
        }
        //路径不能为以/开头
        if (RouterCompileUtils.isStartWithSplash(path)) {
            throw new NullPointerException("@Router path can not start with '/ ' ----> " + path);
        }
        String group = routerMeta.getGroup();
        if (RouterCompileUtils.isEmpty(group)) {
            if (!RouterCompileUtils.isEmpty(moduleName)) {//设置module name为group
                routerMeta.setGroup(moduleName);
            } else {//设置group为默认值
                routerMeta.setGroup(Constans.DEFAULT_GROUP_NAME);
            }
        }
        return true;
    }

    /**
     * 获取拦截器
     *
     * @param page
     * @return
     */
    private List<? extends TypeMirror> getInterceptors(Router page) {
        try {
            page.interceptors();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }

    /**
     * 创建Interceptors
     */
    private CodeBlock buildInterceptors(List<? extends TypeMirror> interceptors) {
        CodeBlock.Builder b = CodeBlock.builder();
        if (interceptors != null && interceptors.size() > 0) {
            for (TypeMirror type : interceptors) {
                if (type instanceof Type.ClassType) {
                    Symbol.TypeSymbol e = ((Type.ClassType) type).asElement();
                    if (e instanceof Symbol.ClassSymbol /*&& isInterceptor(e)*/) {
                        b.add(", $S", getClassName(type));
                    }
                }
            }
        }
        return b.build();
    }
}
