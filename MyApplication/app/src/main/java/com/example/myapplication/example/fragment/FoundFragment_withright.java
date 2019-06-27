package com.example.myapplication.example.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.example.adapter.FoundNewsAdapter_withright;
import com.example.myapplication.example.entity.NewsListForFound;
import com.example.myapplication.example.user.NewsDetailActivity;
import com.example.myapplication.example.user.NewsDetailActivity_withright;
import com.example.myapplication.example.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class FoundFragment_withright extends Fragment implements AdapterView.OnItemClickListener {
    private com.yanzhenjie.recyclerview.SwipeRecyclerView list;
    private ListView foundList;
    private FoundNewsAdapter_withright adapter;
    private Context mContext;
    int j=0;
    int z=0;
    int k=0;
    private List<NewsListForFound> mList;
    private com.yanzhenjie.recyclerview.SwipeRecyclerView mRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_found_withright, null);
        findViewById(v);
        initView();
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        return v;
    }

    private void initData() {
        LinearLayoutManager manager=new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        //mLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);

    }
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize((R.dimen.dp_70));
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setBackground(
                    R.drawable.selector_red)
                    //.setImage(R.drawable.d2bef2ce57765e7b2f06a5806f398ee2)
                    .setText("删除")
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        reLoadNews();
    }

    private void reLoadNews() {
        String url = Constants.BASE_URL + "News?method=getNewsList";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .build()
                .execute(new MyStringCallback());
    }

    public void findViewById(View v) {
        mRecyclerView=(com.yanzhenjie.recyclerview.SwipeRecyclerView) v.findViewById(R.id.found_list);
        //foundList = (ListView) v.findViewById(R.id.found_list);
        mRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener);

    }
    private OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if(menuPosition==0){
                    delete(mList.get(position).getNewsId());
                }
            }
        }
    };
    private void delete(int newsId) {
        String url = Constants.BASE_URL + "News?method=delete";
        OkHttpUtils
                .post()
                .url(url)
                .id(1)
                .addParams("newsId", newsId + "")
                .build()
                .execute(new MyStringCallback2());
    }
    public class MyStringCallback2 extends StringCallback {
        @Override
        public void onResponse(String response, int id) {

            Gson gson = new Gson();

            switch (id) {
                case 1:
                        Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                        reLoadNews();
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }
    public void initView() {
        mContext = getActivity();
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewsDetailActivity_withright.class);
                intent.putExtra("newsId", mList.get(position).getNewsId());
                startActivity(intent);
            }
        });
        //foundList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NewsDetailActivity.class);
        intent.putExtra("newsId", mList.get(position).getNewsId());
        startActivity(intent);
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onResponse(String response, int id) {

            Gson gson = new Gson();
            try {
                Type type = new TypeToken<ArrayList<NewsListForFound>>() {
                }.getType();
                mList = gson.fromJson(response, type);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                mList = null;
            }
            switch (id) {
                case 1:
                    if (mList != null && mList.size() > 0) {
                        forfor(0,mList.size());
                    }
                    break;
                default:
                    Toast.makeText(mContext, "what！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call arg0, Exception arg1, int arg2) {
            Toast.makeText(mContext, "网络链接出错！", Toast.LENGTH_SHORT).show();
        }
    }
    public synchronized  void forfor(int j,int length)
    {
        z=length;
        k=j;

        if(j<length)
        {
            System.out.println("333333333333333"+mList.get(j).getImage());
            String url = Constants.BASE_URL + "Download?method=getNewsImage";
            OkHttpUtils
                    .get()//
                    .url(url)//
                    .addParams("imageName", mList.get(j).getImage())
                    .build()//
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            Toast.makeText(mContext, "无法获取图片！", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(Bitmap bitmap, int i) {
                            System.out.println("图片"+k);
                            mList.get(k).setBitmap(bitmap);
                            k++;
                            System.out.println("k++="+k);
                            forfor(k,z);
                            return ;
                        }
                    });
        }
        else
        {
            adapter = new FoundNewsAdapter_withright(mContext, mList);
            initData();
        }

    }
}
