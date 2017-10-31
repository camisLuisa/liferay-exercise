package com.example.luisafarias.myapplication.fragments;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.luisafarias.myapplication.R;
import com.example.luisafarias.myapplication.interfaces.WeRetrofitService;
import com.example.luisafarias.myapplication.model.Channel;
import com.example.luisafarias.myapplication.model.Rss;
import com.example.luisafarias.myapplication.model.Repositorio;
import com.example.luisafarias.myapplication.model.RetrofitClient;
import com.example.luisafarias.myapplication.util.Constants;
import com.wedeploy.android.auth.Authorization;
import com.wedeploy.android.auth.TokenAuthorization;

import org.json.JSONException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRssFragment extends Fragment {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			_userId = getArguments().getString(Constants.USER_ID);
			_token = getArguments().getString(Constants.TOKEN);
			_rss = getArguments().getParcelable(Constants.RSS);
			//_newOrEdit = getArguments().getBoolean(Constants.NEW_OR_EDIT);
			_authorization = new TokenAuthorization(_token);
			//Log.d("NewRssFragment", "testando");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		_view = inflater.inflate(R.layout.fragment_new_rss, container, false);
		ClipboardManager cbm = (ClipboardManager) _view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData cd = cbm.getPrimaryClip();
		ClipData.Item item = cd.getItemAt(0);
		final String copied = item.getText().toString();

		_urlEditText = _view.findViewById(R.id.newUrlFeed);
		if (android.webkit.URLUtil.isValidUrl(copied)){
			_urlEditText.setText(copied);
		}

		Button save = _view.findViewById(R.id.save);
//		if (_newOrEdit) { /**updateFeed**/
//			_urlEditText.setText(_rss.getUrl());
//
//			save.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					try {
//						updateFeed(_rss);
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		} else {
			/**NewFeed**/
			save.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

						String url = _urlEditText.getText().toString();
						Rss rss = new Rss(url, _userId, null);
						Log.d("teste", url);
						try {
							if (android.webkit.URLUtil.isValidUrl(url)){
								saveNewFeed(rss);
							}else {
								Snackbar.make(v.getRootView().findViewById(R.id.coordinator),"Url invalida", Snackbar.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Log.e(NewRssFragment.class.getName(), e.getMessage());
						} catch (IOException e) {
							e.printStackTrace();
						}

				}
			});

		return _view;
	}

	public void updateFeed(final Rss rss) throws JSONException, IOException {
		//aqui tbm o channel deve ser setado
		//feed.setTitle(_nome.getText().toString());
		getChannelNF(rss, new CallBackChannel() {
			@Override
			public void onSuccess(Channel channel) throws JSONException {

				rss.setUrl(_urlEditText.getText().toString());
				Repositorio.getInstance()
						.updateFeed(rss, _authorization, new Repositorio.CallbackFeed() {
							@Override
							public void onSuccess(Rss rss) {

							}

							@Override
							public void onFailure(Exception e) {
								Log.e("NewFeedFrament", e.getMessage());
							}
						});
			}

			@Override
			public void onFailure(Throwable t) {
				Log.e("NewFeedFrament", t.getMessage());
			}
		});

	}

	public void saveNewFeed(final Rss rss) throws JSONException, IOException {
		getChannelNF(rss, new CallBackChannel() {
			//Rss feedParameter = feed;
			@Override
			public void onSuccess(Channel channel) throws JSONException {
				String title = channel.getTitle();
				rss.setChannel(channel);
				Log.d("NewRssFragment", "deu certo");
				Repositorio.getInstance()
						.addFeed(rss, _authorization, new Repositorio.CallbackFeed() {
							@Override
							public void onSuccess(Rss rss) {
								Log.d(NewRssFragment.class.getName(), "salvo com sucesso");

							}

							@Override
							public void onFailure(Exception e) {

								Log.e(NewRssFragment.class.getName(), e.getMessage());
							}
						});
			}

			@Override
			public void onFailure(Throwable t) {
				Log.e("NewFeedFrament", t.getMessage());
			}
		});

	}

	public void getChannelNF(Rss _rss, final CallBackChannel callBackChannel) throws IOException {
		WeRetrofitService wrs = RetrofitClient.getInstance(_rss.getPartMain())
			.create(WeRetrofitService.class);
		wrs.getItems(_rss.getPartXml()).enqueue(new Callback<Rss>() {
			@Override
			public void onResponse(Call<Rss> call, Response<Rss> response) {
				if (response.isSuccessful()) {
					_channel = response.body().getChannel();
					try {
						callBackChannel.onSuccess(_channel);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(Call<Rss> call, Throwable t) {

				Log.e("NewRssFragment", t.getMessage());
				callBackChannel.onFailure(t);
			}
		});
	}

	public interface CallBackChannel{
		void onSuccess(Channel channel) throws JSONException;
		void onFailure(Throwable t);
	}

	private Authorization _authorization;
	private Channel _channel;
	private Rss _rss;
	private boolean _newOrEdit = false;
	private String _token;
	private String _userId;
	private View _view;
	private EditText _urlEditText;
}
