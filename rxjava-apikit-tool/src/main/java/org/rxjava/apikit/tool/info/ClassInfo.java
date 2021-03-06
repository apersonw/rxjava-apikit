package org.rxjava.apikit.tool.info;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author happy
 */
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ClassInfo {
    /**
     * Java文档信息
     */
    private JavadocInfo javadocInfo;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 类名
     */
    private String name;

    public String getFullName() {
        return packageName + "." + name;
    }

    public ClassInfo(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
    }
}
