package com.yrh.firewall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yrh.firewall.R;
import com.yrh.firewall.model.PhoneInfo;

import java.util.List;

/**
 * Created by Yrh on 2015/12/13.
 */
public class PhoneInfoAdapter extends RecyclerView.Adapter<PhoneInfoViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<PhoneInfo> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
        void onItemLongClick(View view, int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public PhoneInfoAdapter(Context mContext, List<PhoneInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhoneInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_black_list_item, parent, false);
        return new PhoneInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhoneInfoViewHolder holder, final int position) {
        PhoneInfo phoneInfo = mDatas.get(position);
        holder.tvPhoneInfo.setText(phoneInfo.getPhoneNum());

        // 设置监听事件
        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 添加数据
     * @param phoneInfo
     */
    public void addDate(PhoneInfo phoneInfo) {
        mDatas.add(0, phoneInfo);
        notifyItemInserted(mDatas.indexOf(phoneInfo));
//        notifyDataSetChanged();
    }

    /**
     * 删除数据
     * @param pos
     */
    public void deleteData(int pos) {
        mDatas.remove(pos);
        notifyItemRemoved(pos);
    }

    /**
     * 修改数据
     * @param newData
     */
    public void changeData(List<PhoneInfo> newData) {
        mDatas = newData;
        notifyDataSetChanged();
    }
}

class PhoneInfoViewHolder extends RecyclerView.ViewHolder {

    TextView tvPhoneInfo;

    public PhoneInfoViewHolder(View itemView) {
        super(itemView);

        tvPhoneInfo = (TextView) itemView.findViewById(R.id.tvBlackListItemPhoneNum);
    }
}
