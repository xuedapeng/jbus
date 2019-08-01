package cc.touchuan.jbus.auth.db;

public class DeviceTypeEntity {

	private Integer id;
	private String name;
	private String fingerprint;
	private String aliasRule;
	private Integer heartbeat;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	public String getAliasRule() {
		return aliasRule;
	}
	public void setAliasRule(String aliasRule) {
		this.aliasRule = aliasRule;
	}
	public Integer getHeartbeat() {
		return heartbeat;
	}
	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
	}
	
	
	
}
