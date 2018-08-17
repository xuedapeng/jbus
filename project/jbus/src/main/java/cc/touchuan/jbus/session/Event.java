package cc.touchuan.jbus.session;

public class Event {

	public Event(Session session, String eventName) {
		this.setSession(session);
		this.setEventName(eventName);
	}
	
	private Session session;
	private String eventName;
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
}
