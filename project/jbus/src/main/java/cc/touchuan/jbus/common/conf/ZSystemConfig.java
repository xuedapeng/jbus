package cc.touchuan.jbus.common.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.exception.JbusException;


public class ZSystemConfig implements IConfig{

	static Logger logger = Logger.getLogger(ZSystemConfig.class);
	
	private static final String SYSTEM_CONFIG_FILE = "jbus_config.properties";
	
	private static ZSystemConfig singleInst = null;
	
	private Properties prop = new Properties();
	private boolean validProp = false;
	
	private ZSystemConfig() {

        String configFile = this.getDefaultSystemConfigFile();
        File file =new File(configFile);
        if (!file.exists()) {
        	return;
        }
        
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
            prop.load(in); 
            validProp = true;

        } catch (IOException e) {
        	logger.error("", e);
        	validProp = false;
        }
	}	
	
	public static ZSystemConfig getInstance() {

    	if (singleInst == null) {    		
    		singleInst = new ZSystemConfig();
    	}
    	
    	return singleInst;
	}
    
	// 读取配置项
    public static String getProperty(String name) {
    	
    	return getInstance().prop.getProperty(name);
    }    
    
    // 设置并覆盖文件配置项
    public static void setProperty(String name, String value) {
    	
    	getInstance().prop.setProperty(name, value);
    }
    
    private String getDefaultSystemConfigFile() {

		
        String configFile = Paths.get(this.getDefaultSystemConfigPath(), SYSTEM_CONFIG_FILE).toString();
    	
        return configFile;
    }
    
    // 默认在jar同级目录下
    private String getDefaultSystemConfigPath() {
    	
    	  java.net.URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
          String filePath = null ;
          try {
              filePath = java.net.URLDecoder.decode (url.getPath(), "utf-8");
          } catch (Exception e) {
        	  logger.error("", e);
        	  throw new JbusException(e);
          }

       if (filePath.endsWith(".jar")) {
          filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
       }

       java.io.File file = new java.io.File(filePath);

       filePath = file.getAbsolutePath();
        
       return filePath;
    }

	public String getProp(String name) {
		return getProperty(name);
	}

	public void setProp(String name, String value) {
		setProperty(name, value);
	}

	public boolean isValid() {
		return validProp;
	}
    

    public static String getSystemConfigPath() {
    	return getInstance().getDefaultSystemConfigPath();
    	
    }
    
}
