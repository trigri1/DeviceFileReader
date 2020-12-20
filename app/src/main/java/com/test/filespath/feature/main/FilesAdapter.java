package com.test.filespath.feature.main;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
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

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    private OnItemClickListener onItemClickListener;

    public FilesAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private final DiffUtil.ItemCallback<FileModel> diffUtilCallback = new DiffUtil.ItemCallback<FileModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull FileModel oldItem, @NonNull FileModel newItem) {
            Log.e("listExternalStorage", oldItem.hashCode() + "  <====>  " + newItem.hashCode());
            return oldItem.hashCode() == newItem.hashCode();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FileModel oldItem, @NonNull FileModel newItem) {
            return oldItem.name.equalsIgnoreCase(newItem.name)
                    && oldItem.path.equalsIgnoreCase(newItem.path)
                    && oldItem.size == newItem.size;
        }
    };

    private final AsyncListDiffer<FileModel> differ = new AsyncListDiffer<FileModel>(this, diffUtilCallback);

    public void updateList(List<FileModel> newList) {
        differ.submitList(newList);
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_file_name)
        TextView tvFileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(FileModel fileModel) {
            String title = fileModel.name;

            if (fileModel.query != null && !fileModel.query.isEmpty()) {
                int startIndex = title.toLowerCase().indexOf(fileModel.query.toLowerCase());
                int endIndex = startIndex + fileModel.query.length();

                Log.e("Span", "startIndex = " + startIndex + " <=====> " + " endIndex = " + endIndex);
                Log.e("Span", "name = " + title + " <=====> " + " query = " + fileModel.query);

                SpannableString str = new SpannableString(title);
                str.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvFileName.setText(str);
            } else {
                tvFileName.setText(title);
            }

            itemView.setOnClickListener(v -> onItemClickListener.OnItemCLick(fileModel));
        }
    }

    interface OnItemClickListener {
        void OnItemCLick(FileModel fileModel);
    }
}
