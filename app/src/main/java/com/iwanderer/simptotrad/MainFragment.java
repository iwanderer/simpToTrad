package com.iwanderer.simptotrad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leslie on 2016/6/9.
 */
public class MainFragment extends Fragment {

    private EditText mEditText;
    private Button mButton;
    private String searchWord;
    private static final String TAG = "MainFragment";
    private static final String KEY = "2d5cfab07bc24cb5e755b5b142ca642f";
    private TextView mTextView;
    private int type = 0;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, parent, false);

        mTextView = (TextView) view.findViewById(R.id.text);
        mEditText = (EditText) view.findViewById(R.id.from);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchWord = s.toString();
                if (searchWord != null) {
                    handle();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mButton = (Button) view.findViewById(R.id.convert_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchWord != null) {
                    handle();
                }
            }
        });

        mRadioButton1 = (RadioButton) view.findViewById(R.id.simp_to_trad);
        mRadioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        mRadioButton2 = (RadioButton) view.findViewById(R.id.trad_to_simp);
        mRadioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        mRadioButton3 = (RadioButton) view.findViewById(R.id.simp_to_huoxin);
        mRadioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        return view;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case (R.id.simp_to_trad):
                if (checked) {
                    setType(1);
                    handle();
                }
                break;
            case (R.id.trad_to_simp):
                if (checked) {
                    setType(0);
                    handle();
                }
                break;
            case (R.id.simp_to_huoxin):
                if (checked) {
                    setType(2);
                    handle();
                }
                break;
        }
    }

    public void handle() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            setType(type);
            Log.d(TAG, "Type is " + getType());
            new MyTask().execute(searchWord);
        } else {
            // display error
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return result(getDataFromNet(params[0]));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mTextView.setText(result);
        }
    }

    //
    //把要转换的字合并到查询URL
    public String getUrl(String search) {
        String url = Uri.parse("http://japi.juhe.cn/charconvert/change.from").buildUpon()
                .appendQueryParameter("text", search)
                .appendQueryParameter("type", type + "")
                .appendQueryParameter("key", KEY).build().toString();
        return url;
    }

    //连接服务器，获取String型数据
    public String getDataFromNet(String url) throws IOException {
        String address = getUrl(url);

        URL urlSpec = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) urlSpec.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toString();

        } finally {
            connection.disconnect();
        }
    }

    //解析网络数据，返回结果
    public String result(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        String result = jsonObject.getString("outstr");
        Log.d("result method", result);
        return result;
    }
}
