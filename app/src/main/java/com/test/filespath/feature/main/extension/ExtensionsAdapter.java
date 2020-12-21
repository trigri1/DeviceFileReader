package com.test.filespath.feature.main.extension;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.test.filespath.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExtensionsAdapter extends RecyclerView.Adapter<ExtensionsAdapter.ExtensionViewHolder> {
    private OnExtensionClickListener onExtensionCLick;

    public ExtensionsAdapter(OnExtensionClickListener onExtensionCLick) {
        this.onExtensionCLick = onExtensionCLick;
    }

    private final DiffUtil.ItemCallback<String> diffUtilCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final AsyncListDiffer<String> differ = new AsyncListDiffer<String>(this, diffUtilCallback);

    public void updateList(List<String> newList) {
        differ.submitList(newList);
    }

    @NonNull
    @Override
    public ExtensionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_extension, parent, false);
        return new ExtensionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtensionViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }


    class ExtensionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_extension_name)
        TextView tvExtensionName;

        public ExtensionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String extension) {
            tvExtensionName.setText(extension);
            itemView.setOnClickListener(v -> onExtensionCLick.onExtensionCLick(extension));
        }
    }

    interface OnExtensionClickListener {
        void onExtensionCLick(String extension);
    }
}
