package com.example.javademo.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.javademo.constant.LoadStatus;
import com.example.javademo.widget.LoadStatusLiveData;
import com.example.javademo.widget.LoadingDialog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends Fragment {

    public Context mContext;
    public DB binding;
    public VM mViewModel;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        initLoadStatus(); //数据请求状态
        initView();
        initData();
        initLiveDataObserve();
    }

    private  void initViewModel(){
        mViewModel = ViewModelProviders.of(this).get(getVMClass());
    }

    private void initLoadStatus(){
        loadingDialog = new LoadingDialog(mContext);
        LoadStatusLiveData loadStatusLiveData = LoadStatusLiveData.getInstance();
        loadStatusLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                switch (status){
                    case LoadStatus.STATUS_LOADING:
                        loadingDialog.show("加载中");
                        break;
                    case LoadStatus.STATUS_UPLOADING:
                        loadingDialog.show("正在上传");
                        break;
                    case LoadStatus.STATUS_REQUEST:
                        loadingDialog.show("正在请求");
                        break;
                    case LoadStatus.STATUS_CONTENT: //加载完成
                        loadingDialog.dismiss();
                        break;
                    case LoadStatus.STATUS_EMPTY:
                        break;
                    case LoadStatus.STATUS_ERROR: //服务器错误
                        loadingDialog.dismiss();
                        break;
                    case LoadStatus.STATUS_NO_NETWORK: //网络错误
                        loadingDialog.dismiss();
                        break;
                }

            }
        });
    }


    /**
     * 获取资源ID
     *
     * @return 布局资源ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化界面
     */
    public abstract void initView();

    public abstract void initData();

    public abstract void initLiveDataObserve();

    private Class<VM> getVMClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        return (Class<VM>) actualTypeArguments[0];
    }
}
