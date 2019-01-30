package org.gradle.epicture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountActivity extends Fragment {
    private SwipeRefreshLayout pullToRefresh;
    //final Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_account, container, false);
        fetchData();
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                fetchData();
            }
        });
        return view;
    }

    private void fetchData() {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        SharedPreferences MyToken = this.getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String accessToken = MyToken.getString("accessToken", "");
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/account/me/images")
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", "Epicture")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("callBack", "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    JSONObject data;
                    JSONArray items;
                    final List<Photo> photos = new ArrayList<Photo>();
                    try {
                        data = new JSONObject(response.body().string());
                        items = data.getJSONArray("data");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item;
                            item = items.getJSONObject(i);
                            Photo photo = new Photo();
                            photo.id = item.getString("id");
                            photo.title = item.getString("title");
                            photos.add(photo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            render(photos);
                        }
                    });
                }
            }
        });
    }

    private void render(final List<Photo> photos) {
        RecyclerView rv = (RecyclerView) getView().findViewById(R.id.rv_of_photos);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter<PhotoVH> adapter = new RecyclerView.Adapter<PhotoVH>() {
            @Override
            public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
                PhotoVH vh = new PhotoVH(getLayoutInflater().inflate(R.layout.item, null));
                vh.photo = (ImageView) vh.itemView.findViewById(R.id.photo);
                vh.title = (TextView) vh.itemView.findViewById(R.id.title);
                return vh;
            }

            @Override
            public void onBindViewHolder(PhotoVH holder, final int position) {
                Picasso.get().load("https://i.imgur.com/" + photos.get(position).id + ".jpg").into(holder.photo);
                if (photos.get(position).title.equals("null"))
                    holder.title.setText("");
                else
                    holder.title.setText(photos.get(position).title);

                    holder.photo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(80);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setTitle("Delete image ?");
                            alertDialogBuilder
                                    .setIcon(null)
                                    .setCancelable(false)
                                    .setNegativeButton("Confirmer", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            OkHttpClient client = new OkHttpClient();
                                            SharedPreferences MyToken = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                                            String name = MyToken.getString("username", "");
                                            String accessToken = MyToken.getString("accessToken", "");
                                            RequestBody body = RequestBody.create(null, new byte[]{});
                                            Request request = new Request.Builder()
                                                    .url("https://api.imgur.com/3/account/"+ name +"/image/" + photos.get(position).id)
                                                    .delete(body)
                                                    .addHeader("Authorization", "Bearer " + accessToken)
                                                    .addHeader("cache-control", "no-cache")
                                                    .addHeader("Postman-Token", "8c7e9ec4-d136-459c-9235-8c45f8b9ea73")
                                                    .build();
                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Log.e("non", "An error has occurred " + e);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    Log.e("oui", response.toString());
                                                }
                                            });

                                        }
                                        })
                                    .setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {};
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return true;
                        }
                    });
            }

            @Override
            public int getItemCount() {
                return photos.size();
            }
        };
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 30;
            }
        });
        rv.setAdapter(adapter);

    }

    private static class PhotoVH extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView title;

        public PhotoVH(View itemView) {
            super(itemView);
        }
    }

    private static class Photo {
        String id;
        String title;
    }

}