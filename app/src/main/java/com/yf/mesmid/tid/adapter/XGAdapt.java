package com.yf.mesmid.tid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yf.mesmid.R;
import com.yf.mesmid.entity.XGInfo;

import java.util.List;

import lombok.Data;

public class XGAdapt extends BaseAdapter {
    Context context;
    List<XGInfo> list;

    public XGAdapt(Context context, List<XGInfo> List){
        this.context = context;
        this.list = List;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.xgitem, null);
        XGHolder holder = new XGHolder();
        holder.number = (TextView) view.findViewById(R.id.textItem_number);
        holder.tm = (TextView) view.findViewById(R.id.textItem_tm);
        holder.jlrq = (TextView) view.findViewById(R.id.textItem_jlrq);
        holder.ztbz = (TextView) view.findViewById(R.id.textItem_ztbz);
        holder.jlry = (TextView) view.findViewById(R.id.textItem_jlry);
        int number = list.get(position).getNumber();
        String tm = list.get(position).getBarcode();
        String jlrq = list.get(position).getRq();
        String ztbz = list.get(position).getZt();
        String jlry = list.get(position).getRy();

        if(0 == number) {
            view.setClickable(false);
            holder.number .setText("");
        }
        else {
            holder.number .setText(""+number);
            holder.tm .setText(tm);
            holder.jlrq .setText(jlrq);
            if("1".equals(ztbz)) holder.ztbz .setText("入冰箱");
            else if("2".equals(ztbz)) holder.ztbz .setText("回温");
            else if("3".equals(ztbz)) holder.ztbz .setText("机器搅拌");
            else if("4".equals(ztbz)) holder.ztbz .setText("人工搅拌");
            else holder.ztbz .setText("未知状态");
            holder.jlry .setText(jlry);
        }

        return view;
    }

    @Data
    private static class XGHolder {
        private TextView number;
        private TextView tm;
        private TextView jlrq;
        private TextView ztbz;
        private TextView jlry;
    }

}
