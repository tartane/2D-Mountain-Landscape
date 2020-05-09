package com.mountainlandscape.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountainlandscape.R;
import com.mountainlandscape.dialogs.AddEditLayerDialog;
import com.mountainlandscape.interfaces.ItemTouchHelperAdapter;
import com.mountainlandscape.interfaces.ItemTouchHelperViewHolder;
import com.mountainlandscape.interfaces.OnStartDragListener;
import com.mountainlandscape.models.CanvasOptions;
import com.mountainlandscape.models.Layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    private final List<Layer> mItems = new ArrayList<>();
    private Context mContext;
    private OnStartDragListener mStartDragListener;
    private LayerAdapterEvents mLayerAdapterListener;

    public LayerAdapter(List<Layer> items, Context context, OnStartDragListener startDragListener, LayerAdapterEvents layerAdapterListener) {
        this.mItems.addAll(items);
        this.mContext = context;
        this.mStartDragListener = startDragListener;
        this.mLayerAdapterListener = layerAdapterListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layer, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final Layer layer = mItems.get(position);
        holder.txtLayerName.setText(layer.getLayerName());
        holder.imgReOrder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditLayerDialog.showDialog(AddEditLayerDialog.EDialogMode.Edit, layer, getItemCount(), mContext, new AddEditLayerDialog.AddLayerDialogEvents() {
                    @Override
                    public void onSaveLayerPressed(Layer updatedLayer) {
                        mItems.set(position, updatedLayer);
                        notifyItemChanged(position);

                        //only send update event if we need to redraw.
                        if(layer.getResourceId() != updatedLayer.getResourceId() ||
                           layer.getShadowResourceId() != updatedLayer.getShadowResourceId()) {
                            CanvasOptions.getInstance(mContext).editLayer(position, updatedLayer);
                        }
                    }

                    @Override
                    public void onDeleteLayerPressed(Layer layer) {
                        mItems.remove(position);
                        notifyItemRemoved(position);
                        CanvasOptions.getInstance(mContext).removeLayer(layer);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(Layer layer) {
        if(layer != null) {
            mItems.add(layer);
            notifyDataSetChanged();
        }
    }

    public void addAllItems(List<Layer> layers) {
        for(Layer layer: layers) {
            mItems.add(layer);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public List<Layer> getItems() {
        return mItems;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @BindView(R.id.txtLayerName)
        TextView txtLayerName;

        @BindView(R.id.imgReOrder)
        ImageView imgReOrder;

        @BindView(R.id.imgEdit)
        ImageView imgEdit;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }
        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public interface LayerAdapterEvents {
        void onLayerUpdated(Layer layer);
    }
}
