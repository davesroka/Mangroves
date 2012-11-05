package org.qfi.mangroves.entities;

import org.qfi.mangroves.models.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Matt McHugh
 * Date: 10/5/12
 */
public class Video extends Model implements IDbEntity {
	private int id;
	private String video;

	public int getDbId() {
		return id;
	}

	public void setDbId(int id) {
		this.id = id;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	@Override
	public boolean load() {
		return false;
	}

	@Override
	public boolean save() {
		return false;
	}
}
