package cc.touchuan.jbus.common.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ZCustomConfig implements IConfig{
	
	static Logger logger = Logger.getLogger(ZCustomConfig.class);
	
	private Properties prop = new Properties();
	private boolean validProp = false;
	
	public ZCustomConfig(String configFile) {

        try {        	
            InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
            prop.load(in);           
            validProp = true; 

        } catch (IOException e) {
        	logger.error("", e);
        	validProp = false;
        }
	}
	

    public String getProp(String name) { 	
    	return prop.getProperty(name);
    }
    
    // 设置并覆盖文件配置项
    public void setProp(String name, String value) {
    	prop.setProperty(name, value);
    }
    
	public boolean isValid() {
		return validProp;
	}

}
