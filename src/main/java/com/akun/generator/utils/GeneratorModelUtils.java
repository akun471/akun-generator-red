package com.akun.generator.utils;

import com.akun.generator.config.GeneratorParam;
import com.akun.generator.entity.DataSourceEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author lw, akun
 * @email qzywyd@163.com
 * @date 2018年2月23日
 */
public class GeneratorModelUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("template/application.yaml.vm");
        templates.add("template/application-dev.yaml.vm");
        templates.add("template/application-uat.yaml.vm");
        templates.add("template/application-prod.yaml.vm");
        templates.add("template/IndexController.java.vm");
        templates.add("template/ExampleApplication.vm");
        templates.add("template/logback.vm");
        templates.add("template/pom.vm");
        templates.add("template/pom-api.vm");
//        templates.add("template/pom-dao.vm");
//        templates.add("template/pom-domain.vm");
////        templates.add("template/pom-manager.vm");
//        templates.add("template/pom-service.vm");
        templates.add("template/pom-web.vm");
        templates.add("template/spy.properties.vm");
//        templates.add("template/Swagger2.java.vm");
//        templates.add("template/BaseController.java.vm");
//        templates.add("template/BaseService.java.vm");
//        templates.add("template/CommonResponse.java.vm");
//        templates.add("template/CommonResponseCodeEnum.java.vm");
//        templates.add("template/MyMetaObjectHandler.java.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(ZipOutputStream zip, GeneratorParam param, DataSourceEntity dataSourceEntity, List<String> tableNameList) {

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("modelname", param.getModelName());
        map.put("modelnameCase", param.getModelName().substring(0, 1).toUpperCase() + param.getModelName().substring(1));
        map.put("port", param.getPort());
        map.put("package", param.getPackageName());
        map.put("tableNameList", tableNameList);
        map.put("author", param.getAuthor());
        map.put("email", param.getEmail());
        map.put("datetime", DateUtils.format(new Date(), com.akun.generator.utils.DateUtils.DATE_TIME_PATTERN));
        map.put("pollName", dataSourceEntity.getPollName());
        map.put("driverClassName", dataSourceEntity.getDriverClassName());
        map.put("url", dataSourceEntity.getUrl());
        map.put("username", dataSourceEntity.getUsername());
        map.put("password", dataSourceEntity.getPassword());

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
                zip.putNextEntry(new ZipEntry(getFileName(template, param.getModelName(), param.getPackageName())));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //静态template/static文件地址
        toZip(new File("").getAbsolutePath() + "/src/main/resources/static", zip, param.getModelName() + "/" + param.getModelName() + "-web/src/main/resources/", true);

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

    public static String getBaseFile() {
        return System.getProperty("user.dir") + File.separator + "gameley-generator"
                + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "model" + File.separator + "example";
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String modelname, String packageName) {
        String packagePath = modelname + File.separator + modelname + "-web/src" + File.separator + "main" + File.separator + "java" + File.separator;
        String modelPath = modelname + File.separator + modelname + "-web/src" + File.separator + "main" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        switch (template) {
            case "template/application.yaml.vm":
                return modelPath + "resources" + File.separator + "application.yml";

            case "template/application-dev.yaml.vm":
                return modelPath + "resources" + File.separator + "application-dev.yml";

            case "template/application-uat.yaml.vm":
                return modelPath + "resources" + File.separator + "application-uat.yml";

            case "template/application-prod.yaml.vm":
                return modelPath + "resources" + File.separator + "application-prod.yml";

            case "template/logback.vm":
                return modelPath + "resources" + File.separator + "logback.xml";

            case "template/spy.properties.vm":
                return modelPath + "resources" + File.separator + "spy.properties";

            case "template/Swagger2.java.vm":
                return packagePath + "config/Swagger2.java";

            case "template/MyMetaObjectHandler.java.vm":
                return packagePath + "config/MyMetaObjectHandler.java";

            case "template/BaseService.java.vm":
                packagePath = packagePath.replace("-web", "-service");
                return packagePath + "service/BaseService.java";
           case "template/BaseController.java.vm":
                return packagePath + "controller/BaseController.java";
           case "template/CommonResponse.java.vm":
                packagePath = packagePath.replace("-web", "-api");
                return packagePath + "api/vo/CommonResponse.java";
           case "template/CommonResponseCodeEnum.java.vm":
                packagePath = packagePath.replace("-web", "-api");
                return packagePath + "api/enums/CommonResponseCodeEnum.java";

            case "template/pom.vm":
                return modelname + File.separator + "pom.xml";
            case "template/pom-api.vm":
                return modelname + File.separator + modelname + "-api/pom.xml";
            case "template/pom-dao.vm":
                return modelname + File.separator + modelname + "-dao/pom.xml";
            case "template/pom-domain.vm":
                return modelname + File.separator + modelname + "-domain/pom.xml";
            case "template/pom-manager.vm":
                return modelname + File.separator + modelname + "-manager/pom.xml";
            case "template/pom-service.vm":
                return modelname + File.separator + modelname + "-service/pom.xml";
            case "template/pom-web.vm":
                return modelname + File.separator + modelname + "-web/pom.xml";

            case "template/IndexController.java.vm":
                return packagePath + "index/IndexController.java";

            case "template/ExampleApplication.vm":
                return packagePath + modelname.substring(0, 1).toUpperCase() + modelname.substring(1) + "Application.java";

            case "template/spring-jsf-provider.xml.vm":
                return modelPath + "resources" + File.separator + "spring-jsf-provider.xml";

            default:
                return null;
        }
    }

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param zos              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, ZipOutputStream zos, String modelPath, boolean KeepDirStructure)
            throws RuntimeException {

        long start = System.currentTimeMillis();
        try {
//            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), modelPath, KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, String modelPath,
                                 boolean KeepDirStructure) throws Exception {

        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(modelPath + name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(modelPath + name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), modelPath, KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), modelPath, KeepDirStructure);
                    }

                }
            }
        }
    }


}
