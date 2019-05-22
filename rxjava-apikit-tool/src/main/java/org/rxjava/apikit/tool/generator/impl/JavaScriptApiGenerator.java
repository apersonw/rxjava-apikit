package org.rxjava.apikit.tool.generator.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.rxjava.apikit.tool.info.ApiClassInfo;
import org.rxjava.apikit.tool.info.ParamClassInfo;
import org.rxjava.apikit.tool.utils.JsonUtils;
import org.rxjava.apikit.tool.utils.LocalPathUtils;
import org.rxjava.apikit.tool.wrapper.BuilderWrapper;
import org.rxjava.apikit.tool.wrapper.JavaScriptApiWrapper;
import org.rxjava.apikit.tool.wrapper.JavaScriptParamClassWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author happy 2019-05-09 17:46
 */
public class JavaScriptApiGenerator extends AbstractCommonGenerator {

    /**
     * 生成Api类文件
     */
    @Override
    public void generateApiFile(ApiClassInfo apiInfo) throws Exception {
        JavaScriptApiWrapper wrapper = new JavaScriptApiWrapper(context, apiInfo, rootPackage, apiNameMaper);
        File tsFile = createApiFile(wrapper, "d.ts");
        executeModule(
                wrapper,
                getTemplateFile("Api.d.httl"),
                tsFile
        );
    }

    /**
     * 生成参数类文件
     */
    @Override
    public void generateParamFile(BuilderWrapper<ParamClassInfo> wrapper) throws Exception {
        File tsFile = createParamClassFile(wrapper, "d.ts");
        executeModule(
                wrapper,
                getTemplateFile("ParamClass.d.httl"),
                tsFile
        );
    }

    /**
     * 创建参数类包装器
     */
    @Override
    protected BuilderWrapper<ParamClassInfo> createParamClassWarpper(ParamClassInfo paramClassInfo, String distPack, String distName) {
        JavaScriptParamClassWrapper javaScriptParamClassWrapper = new JavaScriptParamClassWrapper(context, paramClassInfo, rootPackage);
        javaScriptParamClassWrapper.setDistFolder(distPack);
        return javaScriptParamClassWrapper;
    }

    private String getTemplateFile(String name) {
        return "/org/rxjava/apikit/tool/generator/es6/" + name;
    }

    /**
     * 生成基本文件
     */
    @Override
    public void generateBaseFile() throws Exception {
        generateIndexFile();
        generatePackageFile();
    }

    /**
     * 生成index文件
     */
    private void generateIndexFile() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        List<Map.Entry> apis = context.getApis().getValues().stream().map(apiInfo -> {
            JavaScriptApiWrapper wrapper = new JavaScriptApiWrapper(context, apiInfo, rootPackage, apiNameMaper);
            String value = wrapper.getDistPackage().replace(".", "/") + '/' + wrapper.getDistClassName();
            return new AbstractMap.SimpleImmutableEntry<>(wrapper.getDistClassName(), value);
        }).collect(Collectors.toList());

        parameters.put("apis", apis);

        File tsFile = LocalPathUtils.packToPath(outPath, "", "index", ".d.ts");

        execute(
                parameters,
                getTemplateFile("index.d.httl"),
                tsFile
        );
    }

    /**
     * 生成package文件
     */
    private void generatePackageFile() throws IOException {
        File packageFile = LocalPathUtils.packToPath(outPath, "", "package", ".json");
        ObjectNode packageJson;
        if (packageFile.exists()) {
            packageJson = (ObjectNode) JsonUtils.MAPPER.readTree(packageFile);
        } else {
            try (InputStream inputStream = JavaScriptApiGenerator.class.getResourceAsStream(getTemplateFile("package.json"))) {
                packageJson = (ObjectNode) JsonUtils.MAPPER.readTree(inputStream);
            }
        }

        packageJson.put("name", "");
        if (this.version != null) {
            String prevVersionText = packageJson.get("version").asText();
            if (prevVersionText != null) {
                prevVersionText = prevVersionText.replaceAll("([^.]+)\\.([^.]+)\\.([^.]+)", "$1.$2." + version);
            } else {
                prevVersionText = "1.0." + version;
            }
            packageJson.put("version", prevVersionText);
        }
        JsonUtils.MAPPER.writerWithDefaultPrettyPrinter().writeValue(packageFile, packageJson);
    }
}