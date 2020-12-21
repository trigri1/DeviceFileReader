package com.test.filespath.feature.main.extension;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.filespath.R;
import com.test.filespath.feature.main.OnExtensionSelectedListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExtensionsDialog extends Dialog implements ExtensionsAdapter.OnExtensionClickListener {

    @BindView(R.id.rv_extensions)
    RecyclerView rvExtensions;

    private ExtensionsAdapter adapter = new ExtensionsAdapter(this);

    private OnExtensionSelectedListener listener;

    public ExtensionsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_extension_list);
        ButterKnife.bind(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvExtensions.setAdapter(adapter);
    }

    public void setData(List<String> list) {
        adapter.updateList(list);
    }


    public void setOnExtensionSelectedListener(OnExtensionSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onExtensionCLick(String extension) {
        listener.onExtensionSelected(extension);
        dismiss();
    }
}
