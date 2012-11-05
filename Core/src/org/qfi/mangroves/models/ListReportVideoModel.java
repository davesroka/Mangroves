/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package org.qfi.mangroves.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import org.qfi.mangroves.database.Database;
import org.qfi.mangroves.entities.Media;
import org.qfi.mangroves.entities.Video;
import org.qfi.mangroves.util.VideoUtils;

/**
 * @author eyedol
 */
public class ListReportVideoModel extends Model {

	private int id;

	private String video;

	private List<Media> mMedia;

	private List<ListReportVideoModel> mVideoModel;

	private static final String PENDING = "pendingvideos/";

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVideo() {
		return this.video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.qfi.mangroves.models.Model#load(android.content.Context)
	 */
	@Override
	public boolean load() {
		return false;
	}

	public boolean load(int reportId) {
		mMedia = Database.mMediaDao.fetchReportVideo(reportId);
		if (mMedia != null) {
			return true;
		}
		return false;
	}

	public List<ListReportVideoModel> getVideos() {
		mVideoModel = new ArrayList<ListReportVideoModel>();

		if (mMedia != null && mMedia.size() > 0) {
			ListReportVideoModel videoModel = new ListReportVideoModel();
			videoModel.setId(mMedia.get(0).getDbId());
			videoModel.setVideo(mMedia.get(0).getLink());
			mVideoModel.add(videoModel);
		}

		return mVideoModel;
	}

	public List<Video> getPendingVideos(Context context) {
		List<Video> videos = new ArrayList<Video>();
		File[] pendingVideos = VideoUtils.getPendingVideos(context);
		if (pendingVideos != null && pendingVideos.length > 0) {
			int id = 0;
			for (File file : pendingVideos) {
				if (file.exists()) {
					id += 1;
					Video video = new Video();
					video.setDbId(id);
					video.setVideo(PENDING + file.getName());
					videos.add(video);
				}
			}

		}

		return videos;
	}

	public List<Video> getPendingVideosByReportId(int reportId) {
		List<Video> videos = new ArrayList<Video>();
		mMedia = Database.mMediaDao.fetchReportVideo(reportId);
		if (mMedia != null && mMedia.size() > 0) {
			for (Media item : mMedia) {
				Video video = new Video();
				video.setDbId(item.getDbId());
				video.setVideo(item.getLink());
				videos.add(video);
			}
		}

		return videos;
	}

	public List<ListReportVideoModel> getVideosByReportId(int reportId) {
		mVideoModel = new ArrayList<ListReportVideoModel>();
		mMedia = Database.mMediaDao.fetchReportVideo(reportId);
		if (mMedia != null && mMedia.size() > 0) {
			for (Media item : mMedia) {
				ListReportVideoModel videoModel = new ListReportVideoModel();
				videoModel.setId(item.getDbId());
				videoModel.setVideo(item.getLink());
				mVideoModel.add(videoModel);
			}
		}

		return mVideoModel;
	}

	public int totalReportVideos() {
		if (mMedia != null && mMedia.size() > 0) {
			return mMedia.size();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.qfi.mangroves.models.Model#save(android.content.Context)
	 */
	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
