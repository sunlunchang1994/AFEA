package com.slc.afea.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import com.slc.afea.R;
import com.slc.afea.database.CollectRecord;
import com.slc.code.ui.baseadapter.recyclerview.CommonAdapter;
import com.slc.code.ui.baseadapter.recyclerview.base.ViewHolder;
import com.slc.code.ui.utils.ColorUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordAdapter extends CommonAdapter<CollectRecord> {
    private String[] operate_type;
    private int[] backRes;
    private DateFormat operateTimeDateFormat;

    public CollectRecordAdapter(Context context, List<CollectRecord> datas) {
        super(context, R.layout.item_collect_record, datas);
        operate_type = mContext.getResources().getStringArray(R.array.operate_type);
        int colorPrimary = ColorUtil.getColorPrimary(mContext);
        int colorAccent = ColorUtil.getColorAccent(mContext);
        backRes = new int[]{
                Color.argb(32, Color.red(colorPrimary), Color.green(colorPrimary), Color.blue(colorPrimary)),
                Color.argb(32, Color.red(colorAccent), Color.green(colorAccent), Color.blue(colorAccent))
        };
        operateTimeDateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    @Override
    protected void convert(ViewHolder holder, CollectRecord collectRecord, int position) {
        holder.setText(R.id.tv_friend_name, collectRecord.getName());
        holder.setText(R.id.tv_operate_type, operate_type[collectRecord.getOperateType()]);
        holder.setText(R.id.tv_operate_result, collectRecord.getCollect() + "");
        holder.setText(R.id.tv_operate_time, operateTimeDateFormat.format(new Date(collectRecord.getTime())));
        holder.getConvertView().setBackgroundColor(backRes[collectRecord.getOperateType()]);
    }
}
