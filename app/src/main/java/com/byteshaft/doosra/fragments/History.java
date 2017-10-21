package com.byteshaft.doosra.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.doosra.R;
import com.byteshaft.doosra.gettersetter.OpinionHistory;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class History extends Fragment {

    private View mBaseView;
    private ListView listView;
    private ArrayList<OpinionHistory> opinionHistoryArrayList;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_history, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.history));
        listView = mBaseView.findViewById(R.id.list_opinion_history);
        getOpinionHistory();
        opinionHistoryArrayList = new ArrayList<>();
        return mBaseView;
    }

    public void getOpinionHistory() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("History", request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        System.out.println("Test " + jsonArray.getJSONObject(i));
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        OpinionHistory opinionHistory = new OpinionHistory();
                                        opinionHistory.setFullName(jsonObject.getString("full_name"));
                                        opinionHistory.setConcern(jsonObject.getString("concern"));
                                        opinionHistory.setExistingDisease(jsonObject.getString("existing_disease"));
                                        opinionHistory.setShortHistory(jsonObject.getString("short_history"));
                                        opinionHistory.setHeigh(jsonObject.getString("height"));
                                        opinionHistory.setWeight(jsonObject.getString("weight"));
                                        opinionHistory.setMedicalFileUrl(jsonObject.getString("medical_file"));
                                        opinionHistory.setLabResultImageUrl(jsonObject.getString("lab_result_file"));
                                        opinionHistory.setXrayFileUrl(jsonObject.getString("xray_file"));
                                        opinionHistory.setOtherFileUrl(jsonObject.getString("other_file"));
                                        opinionHistory.setId(jsonObject.getInt("id"));
                                        opinionHistoryArrayList.add(opinionHistory);

                                    }
                                    System.out.println("length is : " + jsonArray.length());
                                    adapter = new HistoryAdapter(opinionHistoryArrayList);
                                    listView.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("GET", String.format("%sopinion", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }


    /// adapter

    class HistoryAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<OpinionHistory> opinionHistories;

        public HistoryAdapter(ArrayList<OpinionHistory> opinionHistories) {
            this.opinionHistories = opinionHistories;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.delegate_opinion_button, parent, false);  // TODO: 20/10/2017 Create Delegate for History
                viewHolder = new ViewHolder();
                viewHolder.button = convertView.findViewById(R.id.opinion_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // get data from getter/setters
            OpinionHistory history = opinionHistories.get(position);
            viewHolder.button.setText(history.getFullName());
            // set Typeface
            viewHolder.button.setTypeface(AppGlobals.typeface);
            return convertView;
        }

        @Override
        public int getCount() {
            return opinionHistories.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    // view holder

    class ViewHolder {
        public TextView button;
    }

}
