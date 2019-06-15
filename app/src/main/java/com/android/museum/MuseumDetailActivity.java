package com.android.museum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.museum.Model.Museum;
import com.android.museum.Model.MuseumService;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MuseumDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 100;

    private Banner banner;
    private String id;
    private List<String> pic_url_list = new ArrayList<>();

    String imageToUploadPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail);

        banner = findViewById(R.id.museum_pic);
        TextView tv_name = findViewById(R.id.museum_name);
        TextView tv_dept = findViewById(R.id.museum_dept);
        TextView tv_city = findViewById(R.id.museum_city);
        TextView tv_region = findViewById(R.id.museum_region);
        TextView tv_address = findViewById(R.id.museum_address);
        TextView tv_cp = findViewById(R.id.museum_cp);
        TextView tv_open_time = findViewById(R.id.museum_open_time);
        TextView tv_close = findViewById(R.id.museum_close);
        TextView tv_close_time = findViewById(R.id.museum_close_time);
        TextView tv_website = findViewById(R.id.museum_website);
        Button bt_back = findViewById(R.id.back);
        FloatingActionButton addPhotoButton = (FloatingActionButton) findViewById(R.id.add_photo);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        tv_name.setText(intent.getStringExtra("name"));
        tv_dept.setText(intent.getStringExtra("dept"));
        tv_city.setText(intent.getStringExtra("city"));
        tv_region.setText(intent.getStringExtra("region"));
        tv_address.setText(intent.getStringExtra("address"));
        tv_cp.setText(intent.getStringExtra("cp"));
        tv_open_time.setText(intent.getStringExtra("open_time"));
        tv_close.setText(intent.getStringExtra("close"));
        tv_close_time.setText(intent.getStringExtra("close_time"));
        tv_website.setText(intent.getStringExtra("website"));

        get_pic();

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On créé une image temporaire qui accueillera la photo prise avec la caméra
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getPackageManager()) != null){
                    //Create a file to store the image
                    File imgFile = null;
                    try {
                        imgFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(getApplicationContext(), "Erreur de création de fichier", Toast.LENGTH_SHORT).show();
                    }
                    if (imgFile != null) {
                        Uri imgURI = FileProvider.getUriForFile(MuseumDetailActivity.this,
                                "com.android.museum.provider", imgFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
                        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
                    }
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        // On génère un nom de fichier unique
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // On créé l'image temporaire
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageToUploadPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Résultat de la prise de photo
        if (resultCode == Activity.RESULT_OK) {
            uploadFile();
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Photo annulée", Toast.LENGTH_SHORT).show();
        }
    }

    private void get_pic(){
        // On récupère les photos du musée
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    String pic_info = (String)msg.obj;
                    pic_info = pic_info.substring(1, pic_info.length()-1);
                    String[] pic_url_temp = pic_info.split(",");
                    List<String> titles = new ArrayList<>();
                    for (int i=0; i<pic_url_temp.length; i++){
                        titles.add("Photos du musée");
                        pic_url_list.add(pic_url_temp[i].substring(1, pic_url_temp[i].length()-1));
                    }
                    banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                    banner.setImageLoader(new MyLoader());
                    banner.setBannerAnimation(Transformer.Default);
                    banner.setBannerTitles(titles);
                    banner.setDelayTime(3000);
                    banner.isAutoPlay(true);
                    banner.setIndicatorGravity(BannerConfig.CENTER);
                    banner.setImages(pic_url_list).start();
                }else if (msg.what == 2){
                    Toast.makeText(getApplicationContext(), "Erreur de connexion réseau", Toast.LENGTH_SHORT).show();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                GetRemoteData getRemoteData = new GetRemoteData(handler);
                getRemoteData.get_pic_info(id);
            }
        }).start();
    }

    /**
     * On utilise Glide pour afficher toutes les images
     */
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
        }
    }

    private void uploadFile() {
        // On créé le service retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://vps449928.ovh.net/api/musees/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MuseumService service = retrofit.create(MuseumService.class);

        File file = new File(imageToUploadPath);

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // On lie le fichier avec son nom et sa description
        MultipartBody.Part body = MultipartBody.Part
                .createFormData("file", file.getName(), requestFile);
        RequestBody description = RequestBody.create(MultipartBody.FORM, "image-type");

        // On exécute la requête
        Call<ResponseBody> call = service.uploadPhotoMuseum(body, description, id);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MuseumDetailActivity.this, "L'image a bien été uploadée",
                        Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MuseumDetailActivity.this, "Une erreur est survenue",
                        Toast.LENGTH_SHORT);
            }
        });
    }
}
