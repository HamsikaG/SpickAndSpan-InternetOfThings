package com.example.lab4.lab4android;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.lab4.lab4android.utils.BTUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

import static android.content.ContentValues.TAG;
import static com.example.lab4.lab4android.R.string.url;


public class ScannerFragment extends Fragment implements DeviceScanListAdapter.OnBeaconFoundListener{

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    String token;
    private DeviceScanListAdapter mDeviceAdapter;
    private OkHttpClient client;

    public ScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
         client = new OkHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, view);
        mDeviceAdapter = new DeviceScanListAdapter(this.getContext(),this);
        ListView listViewScannedItems = (ListView) view.findViewById(R.id.listViewScannedItems);
        listViewScannedItems.setAdapter(mDeviceAdapter);
        return view;
    }


    @OnCheckedChanged(R.id.buttonScanner)
    void changeScanStatus(CompoundButton buttonView, boolean isChecked){
        Timber.d("Scan button clicked");
        if(isChecked){
            // Clear existing devices (assumes none are connected)
            mDeviceAdapter.clear();
            progressBar.setVisibility(View.VISIBLE);
            BTUtils.startScanning(this.getContext(), mDeviceAdapter, buttonView,progressBar);
        }else{
            BTUtils.stopScanning(this.getContext());
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBeaconFound(String majorId) throws IOException {
        //send the major id to node server through its REST API, it should know which location each major id corresponds to.
        //major ids are sent as 40,41,42,43. 40 and 41 are my beacon ids, 42, 43 should be yours

        System.out.println("onbeacon found with major ID "+majorId);


       new PostDataTask().execute(majorId);

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("response", call.request().body().toString());
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("response", response.body().string());
//            }
//
//
//        });

       /* String url = getString(R.string.url) + majorId;
        Timber.d("url is " + url);

        Request request = new Request.Builder().url(url).build();

//            Response response = client.newCall(request).execute();
          client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Timber.e("IO exception which connecting to server ");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Timber.d("response code is " + response.code());


                }
            });*/

        }


    }

class PostDataTask extends AsyncTask<String, Void, String> {

   // ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setMessage("Inserting data...");
        //progressDialog.show();
    }

    @Override
    protected String doInBackground(String... param) {

        try {
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            String token= FirebaseInstanceId.getInstance().getToken();
            System.out.println("token is "+token);
            Log.w(TAG,"token is "+token);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, String> params = new HashMap<String, String>();
            params.put("timestamp",ts);
            params.put("doctor_id", "1863");
            params.put("room_id", param[0]);
            params.put("device_id",token);
            params.put("clean", "");
            // params.put("name", "your name");
            JSONObject parameter = new JSONObject(params);
            OkHttpClient client = new OkHttpClient();
            System.out.println("token sent");
            RequestBody body = RequestBody.create(JSON, parameter.toString());
            Request request = new Request.Builder()
                    .url("http://192.168.0.6:1000/status")
                    .post(body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();

            client.newCall(request).execute();
        } catch (IOException ex) {
            return "Network error !";

        }
        return null;
    }

   // @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//        // mResult.setText(result);
//        System.out.println("result is "+result);
////        if (progressDialog != null) {
////            progressDialog.dismiss();
////        }
//    }

//    private String postData(String urlPath) throws IOException, JSONException {
//        final String TAG = "MyActivity";
//        StringBuilder result = new StringBuilder();
//        BufferedWriter bufferedWriter = null;
//        BufferedReader bufferedReader = null;
//        String token= FirebaseInstanceId.getInstance().getToken();
//        System.out.println("token is "+token);
//        Log.w(TAG,"token is "+token);
//        Log.w(TAG,"url is "+urlPath);
//
//        try {
//            System.out.println("inside post");
//            //Create data to send to server
//            JSONObject dataToSend = new JSONObject();
//            dataToSend.put("doctor_id",4524);
//            dataToSend.put("doctor_name", "Varun");
//            dataToSend.put("clean", "yes");
//            dataToSend.put("room_id", 24);
//           // dataToSend.put("regToken",token);
//
//            //Initialize and config request, then connect to server.
//            URL url = new URL(urlPath);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setReadTimeout(10000 /* milliseconds */);
//            urlConnection.setConnectTimeout(10000 /* milliseconds */);
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoOutput(true);  //enable output (body data)
//            urlConnection.setRequestProperty("Content-Type", "application/json");// set header
//            System.out.println("urlconnection is "+urlConnection);
//            urlConnection.connect();
//
//            //Write data into server
//            OutputStream outputStream = urlConnection.getOutputStream();
//            System.out.println("poutputstream is :"+outputStream);
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
//            bufferedWriter.write(dataToSend.toString());
//            bufferedWriter.flush();
//
//            //Read data response from server
//         //   InputStream inputStream = urlConnection.getInputStream();
//           // bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        //    String line;
////            while ((line = bufferedReader.readLine()) != null) {
////                result.append(line).append("\n");
////            }
//        } finally {
//            if (bufferedReader != null) {
//                bufferedReader.close();
//            }
//            if (bufferedWriter != null) {
//                bufferedWriter.close();
//            }
//        }
//      //  System.out.println("result.tostring is"+result.toString());
//        return result.toString();
//    }
}

