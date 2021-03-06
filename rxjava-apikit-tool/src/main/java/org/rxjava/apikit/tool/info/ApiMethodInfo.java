package org.rxjava.apikit.tool.info;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.rxjava.apikit.core.HttpMethodType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author happy
 */
@Setter
@Getter
public class ApiMethodInfo {
    /**
     * 索引号
     */
    private int index;
    /**
     * 函数名
     */
    private String name;
    /**
     * 方法请求url
     */
    private String url;
    /**
     * Http请求方法类型,默认为Get
     */
    private HttpMethodType[] types = new HttpMethodType[]{HttpMethodType.GET};
    /**
     * 原始类型
     */
    private TypeInfo resultType;
    /**
     * 返回值的类型
     */
    private TypeInfo resultDataType;
    /**
     * 参数列表
     */
    private ArrayList<ApiMethodParamInfo> params = new ArrayList<>();
    /**
     * 路径参数列表
     */
    private ArrayList<ApiMethodParamInfo> pathParams = new ArrayList<>();
    /**
     * 表单参数列表
     */
    private ArrayList<ApiMethodParamInfo> formParams = new ArrayList<>();
    /**
     * java注释信息
     */
    private JavadocInfo comment;
    /**
     * 是否需要登陆
     */
    private boolean login = true;

    public HttpMethodType getType() {
        return types.length > 0 ? types[0] : null;
    }

    public void addParam(ApiMethodParamInfo param) {
        params.add(param);
        if (param.isPathVariable()) {
            pathParams.add(param);
        }
        if (param.isFormParam()) {
            formParams.add(param);
        }
        if (formParams.size() > 1) {
            throw new RuntimeException("分析错误！暂时只支持单表单");
        }
    }

    protected void findTypes(TypeInfo type, List<TypeInfo> list) {
        list.add(type);
        if (CollectionUtils.isNotEmpty(type.getTypeArguments())) {
            type.getTypeArguments().forEach(t -> findTypes(t, list));
        }
    }

    public List<TypeInfo> getAllTypes() {
        return Stream.of(this)
                .flatMap(m -> {
                    List<TypeInfo> types = new ArrayList<>();
                    types.add(m.getResultDataType());
                    m.getParams().forEach(p -> types.add(p.getTypeInfo()));
                    return types.stream();
                })
                .flatMap(type -> {
                    List<TypeInfo> types = new ArrayList<>();
                    findTypes(type, types);
                    return types.stream();
                })
                .filter(typeInfo -> typeInfo.getType().equals(TypeInfo.Type.OTHER))
                .filter(typeInfo -> !typeInfo.isCollection())
                .filter(typeInfo -> !typeInfo.isGeneric())
                .distinct()
                .collect(Collectors.toList());
    }
}
