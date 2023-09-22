package com.akun.generator.utils;

import com.akun.generator.config.GeneratorParam;
import com.akun.generator.entity.ColumnEntity;
import com.akun.generator.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author chenshun ,akun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午11:40:24 ,2019年6月13日17:16:41
 */
public class GeneratorUtils {
    static List<String> unBaseColumns = new ArrayList<String>();

    static {
        unBaseColumns.add("funique_id");
        unBaseColumns.add("fcreator");
        unBaseColumns.add("fcreate_date");
        unBaseColumns.add("fmodifier");
        unBaseColumns.add("fmodify_date");
        unBaseColumns.add("unique_id");
        unBaseColumns.add("creator");
        unBaseColumns.add("create_date");
        unBaseColumns.add("modifier");
        unBaseColumns.add("modify_date");
        unBaseColumns.add("create_time");
        unBaseColumns.add("update_time");
        unBaseColumns.add("id");
    }

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
//        templates.add("template/index.vue.vm");
        templates.add("template/mapper.xml.vm");
        templates.add("template/entity.java.vm");
        templates.add("template/entity.html.vm");
        templates.add("template/entity.js.vm");
        templates.add("template/mapper.java.vm");
        templates.add("template/controller.java.vm");
//        templates.add("template/service.java.vm-bak");
//        templates.add("template/serviceImpl.java.vm-bak");
        templates.add("template/service.java.vm");
        templates.add("template/serviceImpl.java.vm");
        templates.add("template/entityDetailRes.java.vm");
        templates.add("template/entityInsertReq.java.vm");
        templates.add("template/entityListReq.java.vm");
        templates.add("template/entityListRes.java.vm");
        templates.add("template/entityUpdateReq.java.vm");
        templates.add("template/convert.java.vm");
//        templates.add("template/main.txt.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(Map<String, String> table,
                                     List<Map<String, String>> columns, ZipOutputStream zip, GeneratorParam param) {
        //配置信息
        Configuration config = getConfig();

        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), param.getTablePrefix());
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columsList = new ArrayList<>();
        List<ColumnEntity> baseColumsList = new ArrayList<>();
        boolean flag = false;
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            //列名转换成Java属性名

            String attrName = columnToJava(tableFieldToJava(columnEntity.getColumnName(), param.getTableFieldPrefix()));
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);

            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }
            //是否包含逻辑删除
            if ("delFlag".equalsIgnoreCase(columnEntity.getAttrName())) {
                flag = true;
            }

            columsList.add(columnEntity);
            if (!unBaseColumns.contains(columnEntity.getColumnName())) {
                baseColumsList.add(columnEntity);
            }
        }
        tableEntity.setColumns(columsList);
        tableEntity.setBaseColumns(baseColumsList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("baseColumns", tableEntity.getBaseColumns());
        map.put("package", param.getPackageName());
        map.put("author", param.getAuthor());
        map.put("email", param.getEmail());
        map.put("datetime", com.akun.generator.utils.DateUtils.format(new Date(), com.akun.generator.utils.DateUtils.DATE_TIME_PATTERN));
        map.put("moduleName", param.getModelName());
        map.put("secondModuleName", toLowerCaseFirstOne(className));
        map.put("enableCache", false);  //不开启二级缓存
        map.put("baseResultMap", true);
        map.put("enableQueryList", flag);
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), param.getPackageName(), param.getModelName())));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        if (columnName.contains("_")) {
            return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
        } else {
            return columnName;
        }
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix) && tableName.startsWith(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 表字段名转换成Java字段
     *
     * @param tableFieldName   表字段名称
     * @param tableFieldPrefix 表字段前缀
     * @return {@link String}
     */
    public static String tableFieldToJava(String tableFieldName, String tableFieldPrefix) {
        if (StringUtils.isNotBlank(tableFieldPrefix) && tableFieldName.startsWith(tableFieldPrefix)) {
            tableFieldName = tableFieldName.replaceFirst(tableFieldPrefix, "");
        }
        return tableFieldName;
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = moduleName + File.separator + moduleName + "-web/src" + File.separator + "main" + File.separator + "java" + File.separator;
        String frontPath = "ui" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }
        if (template.contains("index.vue.vm")) {
            return frontPath + "views" + File.separator + moduleName + File.separator + toLowerCaseFirstOne(className) + File.separator + "index.vue";
        }

        if (template.contains("service.java.vm")) {
//            packagePath = packagePath.replace("-web", "-service");
            return packagePath + "service" + File.separator + className + "Service.java";
        }
        if (template.contains("entityDetailRes.java.vm")) {
            packagePath = packagePath.replace("-web", "-api");
            return packagePath + "api/vo/res" + File.separator + className + "DetailRes.java";
        }
        if (template.contains("entityInsertReq.java.vm")) {
            packagePath = packagePath.replace("-web", "-api");
            return packagePath + "api/vo/req" + File.separator + className + "InsertReq.java";
        }
        if (template.contains("entityListReq.java.vm")) {
            packagePath = packagePath.replace("-web", "-api");
            return packagePath + "api/vo/req" + File.separator + className + "ListReq.java";
        }
        if (template.contains("entityListRes.java.vm")) {
            packagePath = packagePath.replace("-web", "-api");
            return packagePath + "api/vo/res" + File.separator + className + "ListRes.java";
        }
        if (template.contains("entityUpdateReq.java.vm")) {
            packagePath = packagePath.replace("-web", "-api");
            return packagePath + "api/vo/req" + File.separator + className + "UpdateReq.java";
        }
        if (template.contains("mapper.java.vm")) {
//            packagePath = packagePath.replace("-web", "-dao");
            return packagePath + "dao" + File.separator + className + "Mapper.java";
        }
        if (template.contains("entity.java.vm")) {
//            packagePath = packagePath.replace("-web", "-domain");
            return packagePath + "bean" + File.separator + className + ".java";
        }
        if (template.contains("entity.html.vm")) {
            return moduleName + File.separator + moduleName + "-web/src/main/resources/templates/" + toLowerCaseFirstOne(className) + ".html";
        }
        if (template.contains("entity.js.vm")) {
            return moduleName + File.separator + moduleName + "-web/src/main/resources/static/scripts/appjs/" + toLowerCaseFirstOne(className) + ".js";
        }
        if (template.contains("controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }
        if (template.contains("serviceImpl.java.vm")) {
//            packagePath = packagePath.replace("-web", "-service");
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }
        if (template.contains("convert.java.vm")) {
            return packagePath + "convert" + File.separator + className + "Converter.java";
        }
//        if (template.contains("service.java.vm")) {
//            packagePath = packagePath.replace("-web", "-manager");
//            return packagePath + "manager" + File.separator + className + "Manager.java";
//        }
//        if (template.contains("serviceImpl.java.vm")) {
//            packagePath = packagePath.replace("-web", "-manager");
//            return packagePath + "manager" + File.separator + "impl" + File.separator + className + "ManagerImpl.java";
//        }
        if (template.contains("mapper.xml.vm")) {
            return moduleName + File.separator + moduleName + "-web/src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
        }
        if (template.contains("main.txt.vm")) {
            return frontPath + "views" + File.separator + moduleName + File.separator + toLowerCaseFirstOne(className) + File.separator + "main.txt";
        }

        return null;
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }


}
