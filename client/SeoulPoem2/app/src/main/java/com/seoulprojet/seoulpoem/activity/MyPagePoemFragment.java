package com.seoulprojet.seoulpoem.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPagePoemListData;
import com.seoulprojet.seoulpoem.model.MyPagePoemResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePoemFragment extends Fragment {

    MyPage myPage;

    private String userEmail = null;
    private int loginType = 0;
    private String otherEmail = null;
    private int otherType = 0;

    private TextView poemCount;
    private View view;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;


    // network
    NetworkService service;
    private ArrayList<MyPagePoemListData> poemResults;

    //담은 작품인지 체크
    int bookmark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myPage = (MyPage)MyPage.myPage;

      view = inflater.inflate(R.layout.mypage_poem_fragment, container, false);
      poemCount = (TextView)view.findViewById(R.id.poem_frag_count_txt);

        Bundle extra = getArguments();
        userEmail = extra.getString("userEmail");
        loginType = extra.getInt("loginType");
        otherEmail = extra.getString("otherEmail");
        otherType = extra.getInt("otherType");

        recyclerView = (RecyclerView)view.findViewById(R.id.poem_frag_rv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // service
       service = ApplicationController.getInstance().getNetworkService();
        getPoem();

        return view;
    }

    /******************** adapter *****************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<MyPagePoemListData> poemResults;

        public RecyclerAdapter(ArrayList<MyPagePoemListData> poemResults){
            this.poemResults = poemResults;
        }

        public void setAdapter(ArrayList<MyPagePoemListData> poemResults){
            this.poemResults = poemResults;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poem_title, parent, false);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    int notice_id = poemResults.get(position).idarticles;
                    Intent intent = new Intent(getActivity().getApplicationContext(), ReadingPoemActivity.class);
                    intent.putExtra("articles_id", ""+notice_id);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    intent.putExtra("otherEmail", userEmail);
                    intent.putExtra("otherType", loginType);
                    startActivity(intent);
                }
            });
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MyPagePoemListData poemResult = poemResults.get(position);

            holder.poemTitle.setText(poemResult.title);
        }

        @Override
        public int getItemCount() {
            return poemResults != null ? poemResults.size() : 0;
        }
    }

    /********************* view holder ************************/
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView poemTitle;

        public MyViewHolder(View itemView){
            super(itemView);

            poemTitle = (TextView)itemView.findViewById(R.id.item_poem_title_txt);
        }
    }

    /****************** 시 리스트 가져오기 **********************/
    private void getPoem(){
        Call<MyPagePoemResult> requestPoem = service.getMyPoem(userEmail, loginType, otherEmail, otherType);

        requestPoem.enqueue(new Callback<MyPagePoemResult>() {
            @Override
            public void onResponse(Call<MyPagePoemResult> call, Response<MyPagePoemResult> response) {
                poemResults = response.body().msg.poems;
                poemCount.setText("# 총 " + response.body().msg.counts + "개");

                if(response.body().msg.counts == 0){
                    TextView textView = (TextView)view.findViewById(R.id.frag_none_poem_tv);
                    textView.setVisibility(View.VISIBLE);
                }
                else{
                    TextView textView = (TextView)view.findViewById(R.id.frag_none_poem_tv);
                    textView.setVisibility(View.INVISIBLE);
                }

                recyclerAdapter = new RecyclerAdapter(poemResults);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onFailure(Call<MyPagePoemResult> call, Throwable t) {
                Log.i("mypage poem", t.getMessage());
            }
        });
    }
}
