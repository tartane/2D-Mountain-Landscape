package com.mountainlandscape.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountainlandscape.R;
import com.mountainlandscape.models.CanvasOptions;
import com.mountainlandscape.models.PresetsAdapterViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PresetsAdapter extends RecyclerView.Adapter<PresetsAdapter.BaseViewHolder> {


    private Context context;
    private List<PresetsAdapterViewModel> presets;
    private LayoutInflater inflater;
    private OnItemClickListener mItemClickListener;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRESET = 1;
    public static final int TYPE_CUSTOM_PRESET = 2;
    public static final int TYPE_NO_CUSTOM = 3;

    public PresetsAdapter(Context context, List<PresetsAdapterViewModel> items) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.presets = items;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.row_preset_header, parent, false);
                return new PresetsAdapter.HeaderViewHolder(headerView);
            case TYPE_PRESET:
                View presetView = inflater.inflate(R.layout.row_preset, parent, false);
                return new PresetsAdapter.PresetViewHolder(presetView);
            case TYPE_CUSTOM_PRESET:
                View customPresetView = inflater.inflate(R.layout.row_preset, parent, false);
                return new PresetsAdapter.CustomPresetViewHolder(customPresetView);
            case TYPE_NO_CUSTOM:
                View noCustomView = inflater.inflate(R.layout.row_no_custom, parent, false);
                return new PresetsAdapter.NoCustomPresetViewHolder(noCustomView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.txtHeaderTitle.setText(presets.get(position).getHeaderTitle());
        } else if(holder instanceof PresetViewHolder) {
            final CanvasOptions preset = presets.get(position).getPreset();
            PresetViewHolder presetHolder = (PresetViewHolder) holder;
            presetHolder.txtPresetName.setText(preset.getName());
        } else if(holder instanceof CustomPresetViewHolder) {
            final CanvasOptions preset = presets.get(position).getPreset();
            CustomPresetViewHolder customPresetHolder = (CustomPresetViewHolder) holder;
            customPresetHolder.txtPresetName.setText(preset.getName());
            customPresetHolder.imgDeletePreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    mItemClickListener.onItemDeleteClick(preset, position);
                }
            });
        }

    }

    private void checkDeleteNoCustomMessage() {
        for(int i = 0; i < presets.size(); i++) {
            if(presets.get(i).getItemType() == TYPE_CUSTOM_PRESET) {
                return;
            }
        }

        presets.add(new PresetsAdapterViewModel(null, TYPE_NO_CUSTOM));

        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        CanvasOptions.getInstance(context).deleteSavedCanvasOptions(presets.get(position).getPreset());
        presets.remove(position);
        notifyItemRemoved(position);

        checkDeleteNoCustomMessage();
    }

    @Override
    public int getItemViewType(int position) {
       return presets.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    public CanvasOptions getItem(int position) {
        return this.presets.get(position).getPreset();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mItemClickListener != null && (this instanceof PresetViewHolder || this instanceof CustomPresetViewHolder)) {
                int position = getAdapterPosition();
                mItemClickListener.onItemClick(view, getItem(position), position);
            }
        }
    }

    class HeaderViewHolder extends BaseViewHolder {

        @BindView(R.id.txtHeaderTitle)
        TextView txtHeaderTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }

    class PresetViewHolder extends BaseViewHolder{

        @BindView(R.id.txtPresetName)
        TextView txtPresetName;

        @BindView(R.id.imgDeletePreset)
        ImageView imgDeletePreset;

        public PresetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgDeletePreset.setVisibility(View.GONE);
        }
    }

    class CustomPresetViewHolder extends BaseViewHolder{

        @BindView(R.id.txtPresetName)
        TextView txtPresetName;

        @BindView(R.id.imgDeletePreset)
        ImageView imgDeletePreset;

        public CustomPresetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    class NoCustomPresetViewHolder extends BaseViewHolder{

        @BindView(R.id.txtNoCustom)
        TextView txtNoCustom;

        public NoCustomPresetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, CanvasOptions item, int position);
        void onItemDeleteClick(CanvasOptions item, int position);
    }

}
