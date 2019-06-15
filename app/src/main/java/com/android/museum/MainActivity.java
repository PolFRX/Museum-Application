package com.android.museum;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.museum.Model.Museum;
import com.android.museum.Model.MuseumService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView lv_museum;
    private TextView tv_none;
    private SQLiteHelper sqLiteHelper;
    private List<HashMap<String, String>> museum_list;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定控件
        FloatingActionButton bt_scan = (FloatingActionButton) findViewById(R.id.scan_code);
        lv_museum = findViewById(R.id.museum_list);
        tv_none = findViewById(R.id.none);

        //初始化控件
        lv_museum.setVisibility(View.GONE);
        tv_none.setVisibility(View.VISIBLE);

        sqLiteHelper = new SQLiteHelper(this);
        museum_list = sqLiteHelper.get_museum_list();
        if (museum_list!=null){
            lv_museum.setVisibility(View.VISIBLE);
            tv_none.setVisibility(View.GONE);
            adapter = new SimpleAdapter(this, museum_list, android.R.layout.simple_list_item_2,
                    new String[]{"name", "time"}, new int[]{android.R.id.text1, android.R.id.text2});
            lv_museum.setAdapter(adapter);
            lv_museum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, MuseumDetailActivity.class);
                    Museum museum = sqLiteHelper.get_museum_detail(museum_list.get(i).get("id"));
                    launchMuseumActivity(intent, museum);
                }
            });
        }

        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCaptureActivity(CaptureActivity.class);           //设置打开摄像头的Activity
                integrator.setPrompt("Scannez le code barre");               //底部的提示文字，设为""可以置空
                integrator.setCameraId(0);                                      //前置或者后置摄像头
                integrator.setBeepEnabled(true);                                //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

    }

    /**
     * 二维码扫描结果回调
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            Toast.makeText(this, "QR code vide", Toast.LENGTH_SHORT).show();
        }else{
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                String result = scanResult.getContents(); // return the museum's id
                Log.d("Code barre", result);
                //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                try{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://vps449928.ovh.net/api/musees/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    MuseumService service = retrofit.create(MuseumService.class);

                    Call<Museum> call = service.getMuseum(result);
                    call.enqueue(new Callback<Museum>() {
                        @Override
                        public void onResponse(Call<Museum> call, Response<Museum> response) {
                            if (response.isSuccessful()) {
                                Museum museum = response.body();
                                museum.save(sqLiteHelper);
                                Intent intent = new Intent(MainActivity.this, MuseumDetailActivity.class);
                                launchMuseumActivity(intent, museum);
                            } else {
                                Toast.makeText(MainActivity.this, "QR code invalide", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Museum> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Connexion non disponible", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(this, "QR code invalide", Toast.LENGTH_SHORT).show();
                    Log.d("Analyse du QR code", e.toString());
                }
            }
        }
    }

    protected void onResume(){
        super.onResume();
        museum_list = sqLiteHelper.get_museum_list();
        if (museum_list!=null) {
            lv_museum.setVisibility(View.VISIBLE);
            tv_none.setVisibility(View.GONE);
            adapter = new SimpleAdapter(this, museum_list, android.R.layout.simple_list_item_2, new String[]{"name", "time"}, new int[]{android.R.id.text1, android.R.id.text2});
            lv_museum.setAdapter(adapter);
            lv_museum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, MuseumDetailActivity.class);
                    Museum museum = sqLiteHelper.get_museum_detail(museum_list.get(i).get("id"));
                    launchMuseumActivity(intent, museum);
                }
            });
        }
    }

    private void launchMuseumActivity(Intent intent, Museum museum) {
        intent.putExtra("id", museum.getId());
        intent.putExtra("name", museum.getNom());
        intent.putExtra("open_time", museum.getPeriode_ouverture());
        intent.putExtra("address", museum.getAdresse());
        intent.putExtra("city", museum.getVille());
        intent.putExtra("close", museum.isFerme());
        intent.putExtra("close_time", museum.getFermeture_annuelle());
        intent.putExtra("website", museum.getSite_web());
        intent.putExtra("cp", museum.getCp());
        intent.putExtra("region", museum.getRegion());
        intent.putExtra("dept", museum.getDept());
        startActivity(intent);
    }

}
