package core.cache.def;

import core.game.node.Node;
import core.tools.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Definition<T extends Node> {
	protected int id;
	protected String name = "null";
	protected String examine;
	protected String[] options;
	protected final Map<String, Object> handlers = new HashMap<String, Object>();

	public Definition() {

	}

	public boolean hasOptions() {
		return hasOptions(true);
	}

	public boolean hasOptions(boolean examine) {
		if (name.equals("null") || options == null) {
			return false;
		}
		for (String option : options) {
			if (option != null && !option.equals("null")) {
				if (examine || !option.equals("Examine")) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <V> V getConfiguration(String key) {
		return (V) handlers.get(key);
	}

	@SuppressWarnings("unchecked")
	public <V> V getConfiguration(String key, V fail) {
		V object = (V) handlers.get(key);
		if (object == null) {
			return fail;
		}
		return object;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExamine() {
		if (examine == null) {
			try {
				if(handlers.get("examine") != null)
					examine = handlers.get("examine").toString();
			} catch (Exception e){
				e.printStackTrace();
			}
			if(examine == null) {
				if (name.length() > 0) {
					examine = "It's a" + (StringUtils.isPlusN(name) ? "n " : " ") + name + ".";
				} else {
					examine = "null";
				}
			}
		}
		return examine;
	}

	public void setExamine(String examine) {
		this.examine = examine;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public Map<String, Object> getHandlers() {
		return handlers;
	}

}