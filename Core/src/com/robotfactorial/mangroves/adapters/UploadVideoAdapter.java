package com.robotfactorial.mangroves.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.robotfactorial.mangroves.R;
import com.robotfactorial.mangroves.entities.Video;
import com.robotfactorial.mangroves.models.ListReportVideoModel;
import com.robotfactorial.mangroves.util.VideoUtils;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Matt McHugh
 * Date: 10/5/12
 */
public class UploadVideoAdapter extends BaseListAdapter<Video> {
	class Widgets extends com.robotfactorial.mangroves.views.View {

		public Widgets(View view) {
			super(view);
			this.photo = (ImageView) view.findViewById(R.id.upload_photo);
		}

		ImageView photo;
	}

	private ListReportVideoModel mListReportVideoModel;

	private List<Video> items;

	public UploadVideoAdapter(Context context) {
		super(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = inflater.inflate(R.layout.upload_photo, parent, false);
		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		widgets.photo.setImageDrawable(getVideoThumbnail(getItem(position).getVideo()));

		return row;
	}

	@Override
	public void refresh() {
		mListReportVideoModel = new ListReportVideoModel();
		items = mListReportVideoModel.getPendingVideos(context);
		this.setItems(items);
	}

	public void refresh(int reportId) {
		mListReportVideoModel = new ListReportVideoModel();
		items = mListReportVideoModel.getPendingVideosByReportId(reportId);
		this.setItems(items);

	}

	public String pendingVideos(int reportId) {
		mListReportVideoModel = new ListReportVideoModel();
		items = mListReportVideoModel.getPendingVideosByReportId(reportId);
		StringBuilder videos = new StringBuilder();
		for (Video video : items) {
			if (video.getVideo().length() > 0) {
				videos.append(video.getVideo()).append(",");
			}
		}

		// delete the last |
		if (videos.length() > 0) {
			videos.deleteCharAt(videos.length() - 1);
		}
		return videos.toString();
	}

	private Drawable getVideoThumbnail(String fileName) {
		File video = new File(fileName);
		Uri videoUri = Uri.fromFile(video);
		return new BitmapDrawable(context.getResources(), VideoUtils.getVideoThumbnail((Activity)context, videoUri));
	}
}
