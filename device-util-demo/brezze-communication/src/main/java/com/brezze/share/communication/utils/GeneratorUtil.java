package com.brezze.share.communication.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * mysql 代码生成器演示例子
 * </p>
 *
 * @author jobob
 * @since 2018-09-12
 */
public class GeneratorUtil {


    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        String projectPath = System.getProperty("user.dir");
        // 数据源配置
        DataSourceConfig dsc = getDataSourceConfig();
        // 全局配置
        GlobalConfig gc = getGlobalConfig(projectPath);
        // 包配置
        PackageConfig pc = getPackageConfig();
        // 自定义配置
        InjectionConfig cfg = getInjectionConfig(projectPath);
        // 策略配置
        StrategyConfig strategy = getStrategyConfig();
        // 设置模板文件
        TemplateConfig tlc = getTemplateConfig();

        mpg.setDataSource(dsc);
        mpg.setGlobalConfig(gc);
        mpg.setPackageInfo(pc);
        mpg.setTemplate(tlc);
        mpg.setStrategy(strategy);
        //mpg.setCfg(cfg);
        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();

    }

    private static DataSourceConfig getDataSourceConfig() {
        // TODO 配置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/db_sharing_test?useSSL=false&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        // dsc.setSchemaName("public");
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        return dsc;
    }

    private static GlobalConfig getGlobalConfig(String projectPath) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectPath);
        gc.setAuthor("Henry");
        gc.setOpen(false);
        // 是否覆盖
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        // 是否在xml中添加二级缓存配置
        //gc.setEnableCache(true);

        // 自定义controller类名称,默认:%sController
        gc.setControllerName(null);
        // 自定义service类名称,默认:I%sService
        gc.setServiceName("%sService");
        // 自定义service实现类名称,默认:%ServiceImpl
        gc.setServiceImplName(null);
        // 自定义mapper类名称,默认:%sMapper
        gc.setMapperName(null);
        // 自定义mapper xml名称,默认:%sMapper
        gc.setXmlName(null);
        // 自定义实体名称
        gc.setEntityName(null);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        return gc;
    }

    private static PackageConfig getPackageConfig() {
        PackageConfig pc = new PackageConfig();
        Map<String, String> pathInfo = new HashMap<>();
        // TODO Configure relative paths
        pc.setParent("com.brezze.share.communication.cabinet");
        // TODO Configure absolute path
        String serverPath = "D:\\workspace\\demo\\device-util-demo\\brezze-communication";

        // TODO entity、mapper、xml、controller、service、serviceImpl
        //Simply comment out the corresponding paths for files that don't need to be generated. Like controller
        pathInfo.put("entity_path", serverPath + "\\src\\main\\java\\com\\brezze\\share\\communication\\cabinet\\entity");
        pathInfo.put("mapper_path", serverPath + "\\src\\main\\java\\com\\brezze\\share\\communication\\cabinet\\mapper");
        pathInfo.put("xml_path", serverPath + "\\src\\main\\resources\\mapper");
//        pathInfo.put("controller_path", serverPath + "\\src\\main\\java\\com\\brezze\\share\\api\\controller");
        pathInfo.put("service_path", serverPath + "\\src\\main\\java\\com\\brezze\\share\\communication\\cabinet\\service");
        pathInfo.put("service_impl_path", serverPath + "\\src\\main\\java\\com\\brezze\\share\\communication\\cabinet\\service\\impl");

        pc.setPathInfo(pathInfo);
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        return pc;
    }

    private static TemplateConfig getTemplateConfig() {
        TemplateConfig tlc = new TemplateConfig();
        return tlc;
    }

    private static StrategyConfig getStrategyConfig() {
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 驼峰命名
        strategy.setCapitalMode(true);
        // 表名前缀(生成的实体会省略这个前缀)
        strategy.setTablePrefix("bz");
        // 生成 @RestController 控制器
        strategy.setRestControllerStyle(true);
        //strategy.setSuperControllerClass("com.brezze.share.base.controller.BaseController");
        strategy.setSuperServiceClass("com.baomidou.mybatisplus.extension.service.IService");
        strategy.setSuperServiceImplClass("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl");
        strategy.setSuperMapperClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
        strategy.setSuperEntityClass("com.brezze.share.communication.cabinet.entity.BaseEntity");

        strategy.setInclude(getInclude());
        //strategy.setExclude(getExclude());
        strategy.setSuperEntityColumns("id", "create_time", "update_time");
        strategy.setEntityLombokModel(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntitySerialVersionUID(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        return strategy;
    }

    private static InjectionConfig getInjectionConfig(String projectPath) {

        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        // 自定义模板配置
        List<FileOutConfig> focList = new ArrayList<>();

        cfg.setFileOutConfigList(focList);

        return cfg;
    }


    /**
     * 获取包含的表
     *
     * @return
     */
    private static String[] getInclude() {
        // TODO 配置要生成的表
        return new String[]{
//                "bz_cabinet_log",
                "bz_cabinet",
//                "bz_cabinet_position",
//                "bz_advert",
//                "bz_power_bank_data",
//                "bz_cabinet_pinboard",
        };
    }


}
