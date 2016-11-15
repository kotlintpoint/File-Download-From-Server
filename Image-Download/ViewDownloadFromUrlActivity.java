

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;


public class ViewDownloadFromUrlActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonFetch;
    private Button buttonDownload;
    private ImageView imageView;
    private EditText editTextName;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_download);

        buttonFetch = (Button) findViewById(R.id.buttonFetch);
        buttonDownload = (Button) findViewById(R.id.buttonDownload);
        editTextName = (EditText) findViewById(R.id.editText);
        imageView  = (ImageView) findViewById(R.id.imageView);

        buttonFetch.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.show();
        if(view == buttonFetch){
            fetchImage("Your Image Url");
        }else if(view == buttonDownload){
            downloadImage("Your Image Url");
        }
    }

    private void downloadImage(final String url) {
        (new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    InputStream is = new URL(url).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    return bitmap;
                } catch (Exception e) {
                    Log.i("Error",e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap s) {
                super.onPostExecute(s);
                if(s!=null) {
                    saveFileToSdCard(s);
                }
                pd.dismiss();
            }
        }).execute();

    }

    private void saveFileToSdCard(Bitmap bitmap) {
        String path=Environment.getExternalStorageDirectory()+"/"+editTextName.getText().toString()+".jpg";
        try {
            FileOutputStream fos=new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Toast.makeText(ViewDownloadFromUrlActivity.this, "Success!!!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ViewDownloadFromUrlActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ViewDownloadFromUrlActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchImage(String url) {
        Glide.with(getApplicationContext()).load(url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        pd.dismiss();
    }
}
